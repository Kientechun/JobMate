using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using JobMate.Application.Common.Interfaces;
using JobMate.Domain.Entities;
using Microsoft.EntityFrameworkCore;
using FirebaseAdmin.Messaging;
using Notification = JobMate.Domain.Entities.Notification;

namespace JobMate.Infrastructure.Services
{
    public class NotificationService : INotificationService
    {
        private readonly IJobMateDbContext _context;

        public NotificationService(IJobMateDbContext context)
        {
            _context = context;
        }

        public async Task SendNotificationAsync(string userId, string title, string message, string type, string? relatedId = null)
        {
            // 1. Save to Database
            var notification = new Notification
            {
                UserId = userId,
                Title = title,
                Message = message,
                Type = type,
                RelatedId = relatedId,
                IsRead = false,
                CreatedAt = DateTime.UtcNow
            };

            _context.Notifications.Add(notification);
            await _context.SaveChangesAsync();

            // 2. Send via FCM
            var tokens = await _context.DeviceTokens
                .Where(t => t.UserId == userId)
                .Select(t => t.Token)
                .ToListAsync();

            if (tokens.Any())
            {
                var fcmMessage = new MulticastMessage()
                {
                    Tokens = tokens,
                    Data = new Dictionary<string, string>()
                    {
                        { "type", type },
                        { "relatedId", relatedId ?? "" },
                        { "notificationId", notification.Id.ToString() }
                    },
                    Notification = new FirebaseAdmin.Messaging.Notification()
                    {
                        Title = title,
                        Body = message
                    }
                };

                try
                {
                    await FirebaseMessaging.DefaultInstance.SendMulticastAsync(fcmMessage);
                }
                catch (Exception)
                {
                    // Log error but don't fail the operation
                }
            }
        }

        public async Task<List<Notification>> GetUserNotificationsAsync(string userId)
        {
            return await _context.Notifications
                .Where(n => n.UserId == userId)
                .OrderByDescending(n => n.CreatedAt)
                .ToListAsync();
        }

        public async Task MarkAsReadAsync(int notificationId)
        {
            var notification = await _context.Notifications.FindAsync(notificationId);
            if (notification != null)
            {
                notification.IsRead = true;
                await _context.SaveChangesAsync();
            }
        }

        public async Task MarkAllAsReadAsync(string userId)
        {
            var notifications = await _context.Notifications
                .Where(n => n.UserId == userId && !n.IsRead)
                .ToListAsync();

            foreach (var n in notifications)
            {
                n.IsRead = true;
            }

            await _context.SaveChangesAsync();
        }

        public async Task DeleteNotificationAsync(int notificationId)
        {
            var notification = await _context.Notifications.FindAsync(notificationId);
            if (notification != null)
            {
                _context.Notifications.Remove(notification);
                await _context.SaveChangesAsync();
            }
        }

        public async Task RegisterDeviceTokenAsync(string userId, string token, string deviceType)
        {
            var existing = await _context.DeviceTokens
                .FirstOrDefaultAsync(t => t.UserId == userId && t.Token == token);

            if (existing == null)
            {
                _context.DeviceTokens.Add(new DeviceToken
                {
                    UserId = userId,
                    Token = token,
                    DeviceType = deviceType,
                    CreatedAt = DateTime.UtcNow,
                    LastUsedAt = DateTime.UtcNow
                });
            }
            else
            {
                existing.LastUsedAt = DateTime.UtcNow;
            }

            await _context.SaveChangesAsync();
        }

        public async Task UnregisterDeviceTokenAsync(string userId, string token)
        {
            var existing = await _context.DeviceTokens
                .FirstOrDefaultAsync(t => t.UserId == userId && t.Token == token);

            if (existing != null)
            {
                _context.DeviceTokens.Remove(existing);
                await _context.SaveChangesAsync();
            }
        }
    }
}
