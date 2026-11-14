using JobMate.Domain.Entities;

namespace JobMate.Application.Common.Interfaces
{
    public interface INotificationService
    {
        Task SendNotificationAsync(string userId, string title, string message, string type, string? relatedId = null);
        Task<List<Notification>> GetUserNotificationsAsync(string userId);
        Task MarkAsReadAsync(int notificationId);
        Task MarkAllAsReadAsync(string userId);
        Task DeleteNotificationAsync(int notificationId);

        // Device Token management
        Task RegisterDeviceTokenAsync(string userId, string token, string deviceType);
        Task UnregisterDeviceTokenAsync(string userId, string token);
    }
}
