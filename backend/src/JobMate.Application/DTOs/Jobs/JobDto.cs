using System;
using JobMate.Domain.Enums;

namespace JobMate.Application.DTOs.Jobs
{
    public class JobDto
    {
        public int Id { get; set; }
        public string Title { get; set; } = string.Empty;
        public string Description { get; set; } = string.Empty;
        public string CompanyName { get; set; } = string.Empty;
        public string Location { get; set; } = string.Empty;
        public decimal SalaryRange { get; set; }
        public string JobType { get; set; } = string.Empty;
        public string Experience { get; set; } = string.Empty;
        public DateTime ExpiryDate { get; set; }
        public JobStatus Status { get; set; }
        public string CompanyId { get; set; } = string.Empty;
        public DateTime CreatedAt { get; set; }
    }
}
