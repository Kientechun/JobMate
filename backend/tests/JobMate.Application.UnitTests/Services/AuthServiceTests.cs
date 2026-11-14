using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using JobMate.Application.Common.Interfaces;
using JobMate.Application.DTOs.Auth;
using JobMate.Domain.Entities;
using JobMate.Infrastructure.Identity;
using Microsoft.AspNetCore.Identity;
using Microsoft.Extensions.Configuration;
using Moq;
using Xunit;

namespace JobMate.Application.UnitTests.Services
{
    public class AuthServiceTests
    {
        private readonly Mock<UserManager<ApplicationUser>> _mockUserManager;
        private readonly Mock<IJwtTokenGenerator> _mockJwtTokenGenerator;
        private readonly Mock<IJobMateDbContext> _mockContext;
        private readonly Mock<IConfiguration> _mockConfiguration;
        private readonly AuthService _authService;

        public AuthServiceTests()
        {
            var store = new Mock<IUserStore<ApplicationUser>>();
            _mockUserManager = new Mock<UserManager<ApplicationUser>>(store.Object, null, null, null, null, null, null, null, null);
            _mockJwtTokenGenerator = new Mock<IJwtTokenGenerator>();
            _mockContext = new Mock<IJobMateDbContext>();
            _mockConfiguration = new Mock<IConfiguration>();

            _authService = new AuthService(
                _mockUserManager.Object,
                _mockJwtTokenGenerator.Object,
                _mockContext.Object,
                _mockConfiguration.Object);
        }

        [Fact]
        public async Task RegisterAsync_ShouldReturnFailure_WhenUserExists()
        {
            // Arrange
            var request = new RegisterRequest { Email = "test@example.com", FullName = "Test User", Password = "Password123!" };
            _mockUserManager.Setup(m => m.FindByEmailAsync(request.Email)).ReturnsAsync(new ApplicationUser());

            // Act
            var result = await _authService.RegisterAsync(request);

            // Assert
            Assert.False(result.Succeeded);
            Assert.Contains("User with this email already exists.", result.Errors);
        }

        [Fact]
        public async Task RegisterAsync_ShouldReturnSuccess_WhenDataIsValid()
        {
            // Arrange
            var request = new RegisterRequest { Email = "new@example.com", FullName = "New User", Password = "Password123!" };
            _mockUserManager.Setup(m => m.FindByEmailAsync(request.Email)).ReturnsAsync((ApplicationUser)null);
            _mockUserManager.Setup(m => m.CreateAsync(It.IsAny<ApplicationUser>(), request.Password))
                .ReturnsAsync(IdentityResult.Success)
                .Callback<ApplicationUser, string>((u, p) => u.Id = "new-user-id"); // Set Id to avoid NRE
            _mockUserManager.Setup(m => m.AddToRoleAsync(It.IsAny<ApplicationUser>(), "Candidate")).ReturnsAsync(IdentityResult.Success);
            _mockUserManager.Setup(m => m.GetRolesAsync(It.IsAny<ApplicationUser>())).ReturnsAsync(new List<string> { "Candidate" });

            _mockJwtTokenGenerator.Setup(m => m.GenerateTokenWithClaims(It.IsAny<IEnumerable<System.Security.Claims.Claim>>())).Returns("token");
            _mockJwtTokenGenerator.Setup(m => m.GenerateRefreshToken()).Returns("refresh");
            _mockJwtTokenGenerator.Setup(m => m.HashToken(It.IsAny<string>())).Returns("hashed");

            _mockContext.Setup(c => c.RefreshTokens.Add(It.IsAny<RefreshToken>()));

            // Act
            var result = await _authService.RegisterAsync(request);

            // Assert
            Assert.True(result.Succeeded);
            _mockUserManager.Verify(m => m.CreateAsync(It.Is<ApplicationUser>(u => u.Email == request.Email), request.Password), Times.Once);
        }

        [Fact]
        public async Task UpdateProfileAsync_ShouldUpdateFullName()
        {
            // Arrange
            var userId = "user1";
            var user = new ApplicationUser { Id = userId, FullName = "Old Name" };
            var request = new UpdateProfileRequest { FullName = "New Name" };

            _mockUserManager.Setup(m => m.FindByIdAsync(userId)).ReturnsAsync(user);
            _mockUserManager.Setup(m => m.UpdateAsync(user)).ReturnsAsync(IdentityResult.Success);

            // Act
            var result = await _authService.UpdateProfileAsync(userId, request);

            // Assert
            Assert.True(result.Succeeded);
            Assert.Equal("New Name", user.FullName);
            _mockUserManager.Verify(m => m.UpdateAsync(user), Times.Once);
        }
    }
}
