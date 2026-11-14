using System;
using System.Collections.Generic;
using System.IdentityModel.Tokens.Jwt;
using System.Linq;
using System.Security.Claims;
using System.Text;
using System.Threading.Tasks;
using JobMate.Application.Common.Interfaces;
using JobMate.Application.Common.Models;
using JobMate.Application.DTOs.Auth;
using JobMate.Domain.Entities;
using Microsoft.AspNetCore.Identity;
using Microsoft.EntityFrameworkCore;
using Microsoft.Extensions.Configuration;
using Microsoft.IdentityModel.Tokens;

namespace JobMate.Infrastructure.Identity
{
    public class AuthService : IAuthService
    {
        private readonly UserManager<ApplicationUser> _userManager;
        private readonly IJwtTokenGenerator _jwtTokenGenerator;
        private readonly IJobMateDbContext _context;
        private readonly IConfiguration _configuration;

        public AuthService(
            UserManager<ApplicationUser> userManager,
            IJwtTokenGenerator jwtTokenGenerator,
            IJobMateDbContext context,
            IConfiguration configuration)
        {
            _userManager = userManager;
            _jwtTokenGenerator = jwtTokenGenerator;
            _context = context;
            _configuration = configuration;
        }

        public async Task<ApiResponse<AuthResponse>> RegisterAsync(RegisterRequest request)
        {
            var userExists = await _userManager.FindByEmailAsync(request.Email);
            if (userExists != null)
                return ApiResponse<AuthResponse>.Failure("User with this email already exists.");

            var user = new ApplicationUser
            {
                Email = request.Email,
                UserName = request.Email,
                FullName = request.FullName,
                SecurityStamp = Guid.NewGuid().ToString()
            };

            var result = await _userManager.CreateAsync(user, request.Password);
            if (!result.Succeeded)
            {
                var errors = string.Join(", ", result.Errors.Select(e => e.Description));
                return ApiResponse<AuthResponse>.Failure(errors);
            }

            // Default role is Candidate
            await _userManager.AddToRoleAsync(user, "Candidate");

            return await GenerateAuthResponse(user);
        }

        public async Task<ApiResponse<AuthResponse>> LoginAsync(LoginRequest request)
        {
            var user = await _userManager.FindByEmailAsync(request.Email);
            if (user == null || !await _userManager.CheckPasswordAsync(user, request.Password))
                return ApiResponse<AuthResponse>.Failure("Invalid email or password.");

            return await GenerateAuthResponse(user);
        }

        public async Task<ApiResponse<AuthResponse>> RefreshTokenAsync(string token, string refreshToken, string ipAddress)
        {
            var principal = GetPrincipalFromExpiredToken(token);
            if (principal == null)
                return ApiResponse<AuthResponse>.Failure("Invalid token.");

            var userId = principal.FindFirst(ClaimTypes.NameIdentifier)?.Value;
            var hashedRefreshToken = _jwtTokenGenerator.HashToken(refreshToken);

            var user = await _context.RefreshTokens
                .Where(u => u.UserId == userId && u.Token == hashedRefreshToken)
                .Join(_userManager.Users, rt => rt.UserId, u => u.Id, (rt, u) => new { RefreshToken = rt, User = u })
                .FirstOrDefaultAsync();

            if (user == null || !user.RefreshToken.IsActive)
                return ApiResponse<AuthResponse>.Failure("Invalid or expired refresh token.");

            // Revoke current refresh token
            user.RefreshToken.IsRevoked = true;
            user.RefreshToken.RevokedAt = DateTime.UtcNow;
            user.RefreshToken.RevokedByIp = ipAddress;

            // Generate new tokens
            var roles = await _userManager.GetRolesAsync(user.User);
            var newToken = _jwtTokenGenerator.GenerateToken(user.User.Id, user.User.UserName!, roles);
            var newRefreshToken = _jwtTokenGenerator.GenerateRefreshToken();
            var newHashedRefreshToken = _jwtTokenGenerator.HashToken(newRefreshToken);

            // Save new refresh token
            var refreshTokenEntity = new RefreshToken
            {
                Token = newHashedRefreshToken,
                ExpiresAt = DateTime.UtcNow.AddDays(7),
                CreatedAt = DateTime.UtcNow,
                CreatedByIp = ipAddress,
                UserId = user.User.Id
            };

            user.RefreshToken.ReplacedByToken = newHashedRefreshToken;
            _context.RefreshTokens.Add(refreshTokenEntity);
            await _context.SaveChangesAsync();

            return ApiResponse<AuthResponse>.Success(new AuthResponse
            {
                Id = user.User.Id,
                FullName = user.User.FullName,
                Email = user.User.Email!,
                Token = newToken,
                RefreshToken = newRefreshToken,
                Roles = roles.ToList()
            });
        }

        public async Task<ApiResponse<bool>> RevokeTokenAsync(string refreshToken, string ipAddress)
        {
            var hashedToken = _jwtTokenGenerator.HashToken(refreshToken);
            var tokenEntity = await _context.RefreshTokens
                .FirstOrDefaultAsync(x => x.Token == hashedToken);

            if (tokenEntity == null || !tokenEntity.IsActive)
                return ApiResponse<bool>.Failure("Invalid token.");

            tokenEntity.IsRevoked = true;
            tokenEntity.RevokedAt = DateTime.UtcNow;
            tokenEntity.RevokedByIp = ipAddress;

            await _context.SaveChangesAsync();
            return ApiResponse<bool>.Success(true, "Token revoked.");
        }

        public async Task<ApplicationUserDto?> GetCurrentUserAsync(string userId)
        {
            var user = await _userManager.FindByIdAsync(userId);
            if (user == null) return null;

            var roles = await _userManager.GetRolesAsync(user);

            return new ApplicationUserDto
            {
                Id = user.Id,
                FullName = user.FullName,
                Email = user.Email!,
                Roles = roles.ToList()
            };
        }

        public async Task<ApiResponse<bool>> UpdateProfileAsync(string userId, UpdateProfileRequest request)
        {
            var user = await _userManager.FindByIdAsync(userId);
            if (user == null)
                return ApiResponse<bool>.Failure("User not found.");

            user.FullName = request.FullName;
            if (!string.IsNullOrEmpty(request.PhoneNumber))
            {
                user.PhoneNumber = request.PhoneNumber;
            }

            var result = await _userManager.UpdateAsync(user);
            if (!result.Succeeded)
            {
                var errors = string.Join(", ", result.Errors.Select(e => e.Description));
                return ApiResponse<bool>.Failure(errors);
            }

            return ApiResponse<bool>.Success(true, "Profile updated successfully.");
        }

        private async Task<ApiResponse<AuthResponse>> GenerateAuthResponse(ApplicationUser user)
        {
            var roles = await _userManager.GetRolesAsync(user);

            var claims = new List<Claim>
            {
                new Claim(JwtRegisteredClaimNames.Sub, user.Id),
                new Claim(ClaimTypes.NameIdentifier, user.Id),
                new Claim(ClaimTypes.Name, user.UserName!)
            };

            if (user.CompanyId.HasValue)
            {
                claims.Add(new Claim("CompanyId", user.CompanyId.Value.ToString()));
            }

            foreach (var role in roles)
            {
                claims.Add(new Claim(ClaimTypes.Role, role));
            }

            var token = _jwtTokenGenerator.GenerateTokenWithClaims(claims);
            var refreshToken = _jwtTokenGenerator.GenerateRefreshToken();
            var hashedRefreshToken = _jwtTokenGenerator.HashToken(refreshToken);

            var refreshTokenEntity = new RefreshToken
            {
                Token = hashedRefreshToken,
                ExpiresAt = DateTime.UtcNow.AddDays(7),
                CreatedAt = DateTime.UtcNow,
                CreatedByIp = "127.0.0.1",
                UserId = user.Id
            };

            _context.RefreshTokens.Add(refreshTokenEntity);
            await _context.SaveChangesAsync();

            return ApiResponse<AuthResponse>.Success(new AuthResponse
            {
                Id = user.Id,
                FullName = user.FullName,
                Email = user.Email!,
                Token = token,
                RefreshToken = refreshToken,
                Roles = roles.ToList()
            });
        }

        private ClaimsPrincipal? GetPrincipalFromExpiredToken(string token)
        {
            var tokenValidationParameters = new TokenValidationParameters
            {
                ValidateAudience = false,
                ValidateIssuer = false,
                ValidateIssuerSigningKey = true,
                IssuerSigningKey = new SymmetricSecurityKey(Encoding.UTF8.GetBytes(_configuration["JwtSettings:Key"]!)),
                ValidateLifetime = false // Here we are saying that we don't care about the token's expiration date
            };

            var tokenHandler = new JwtSecurityTokenHandler();
            var principal = tokenHandler.ValidateToken(token, tokenValidationParameters, out SecurityToken securityToken);
            if (securityToken is not JwtSecurityToken jwtSecurityToken || !jwtSecurityToken.Header.Alg.Equals(SecurityAlgorithms.HmacSha256, StringComparison.InvariantCultureIgnoreCase))
                throw new SecurityTokenException("Invalid token");

            return principal;
        }
    }
}
