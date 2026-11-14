using System.Collections.Generic;

namespace JobMate.Application.Common.Interfaces
{
    public interface IJwtTokenGenerator
    {
        string GenerateToken(string userId, string userName, IEnumerable<string> roles);
        string GenerateTokenWithClaims(IEnumerable<System.Security.Claims.Claim> claims);
        string GenerateRefreshToken();
        string HashToken(string token);
    }
}
