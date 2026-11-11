using System;

namespace JobMate.Domain.Entities
{
    public class Interview
    {
        public int Id { get; set; }
        public int JobApplicationId { get; set; }
        public virtual JobApplication JobApplication { get; set; } = null!;
        public DateTime ScheduledAt { get; set; }
        public string Location { get; set; } = string.Empty; // Address or Meeting Link
        public string Type { get; set; } = string.Empty; // Online, In-Person
        public string Notes { get; set; } = string.Empty;
        public string Status { get; set; } = "Scheduled"; // Scheduled, Completed, Cancelled
        public string? Result { get; set; }
        public DateTime CreatedAt { get; set; } = DateTime.UtcNow;
        public DateTime UpdatedAt { get; set; } = DateTime.UtcNow;
    }
}
