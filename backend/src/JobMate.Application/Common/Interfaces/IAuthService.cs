using System.Threading.Tasks;
using JobMate.Application.DTOs.Auth;
using JobMate.Application.Common.Models;

namespace JobMate.Application.Common.Interfaces
{
    public interface IAuthService
    {
        Task<ApiResponse<AuthResponse>> RegisterAsync(RegisterRequest request);
        Task<ApiResponse<AuthResponse>> LoginAsync(LoginRequest request);
        Task<ApiResponse<AuthResponse>> RefreshTokenAsync(string token, string refreshToken, string ipAddress);
        Task<ApiResponse<bool>> RevokeTokenAsync(string token, string ipAddress);
        Task<ApplicationUserDto?> GetCurrentUserAsync(string userId);
    }

    public class ApplicationUserDto
    {
        public string Id { get; set; } = string.Empty;
        public string FullName { get; set; } = string.Empty;
        public string Email { get; set; } = string.Empty;
        public List<string> Roles { get; set; } = new();
    }
}
