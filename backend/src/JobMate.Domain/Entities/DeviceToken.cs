using System;

namespace JobMate.Domain.Entities
{
    public class DeviceToken
    {
        public int Id { get; set; }
        public string UserId { get; set; } = string.Empty;
        public string Token { get; set; } = string.Empty;
        public string DeviceType { get; set; } = string.Empty; // Android, iOS, Web
        public DateTime CreatedAt { get; set; } = DateTime.UtcNow;
        public DateTime LastUsedAt { get; set; } = DateTime.UtcNow;
    }
}
