using System.ComponentModel.DataAnnotations;

namespace JobMate.Application.DTOs.Auth
{
    public class RefreshRequest
    {
        [Required]
        public string Token { get; set; } = string.Empty;

        [Required]
        public string RefreshToken { get; set; } = string.Empty;
    }
}
