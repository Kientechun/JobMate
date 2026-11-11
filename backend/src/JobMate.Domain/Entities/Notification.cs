using System;

namespace JobMate.Domain.Entities
{
    public class Notification
    {
        public int Id { get; set; }
        public string UserId { get; set; } = string.Empty;
        public string Title { get; set; } = string.Empty;
        public string Message { get; set; } = string.Empty;
        public string Type { get; set; } = string.Empty; // StatusUpdate, InterviewSchedule, JobSuggestion
        public string? RelatedId { get; set; } // E.g., JobApplicationId or InterviewId
        public bool IsRead { get; set; }
        public DateTime CreatedAt { get; set; } = DateTime.UtcNow;
    }
}
