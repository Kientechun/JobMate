using System;
using System.ComponentModel.DataAnnotations;

namespace JobMate.Application.DTOs.Jobs
{
    public class CreateJobRequest
    {
        [Required]
        [MaxLength(200)]
        public string Title { get; set; } = string.Empty;

        [Required]
        public string Description { get; set; } = string.Empty;

        [Required]
        [MaxLength(200)]
        public string CompanyName { get; set; } = string.Empty;

        [Required]
        public string Location { get; set; } = string.Empty;

        [Range(0, double.MaxValue)]
        public decimal SalaryRange { get; set; }

        [Required]
        public string JobType { get; set; } = string.Empty;

        [Required]
        public string Experience { get; set; } = string.Empty;

        [Required]
        public DateTime ExpiryDate { get; set; }

        [Required]
        public int CompanyId { get; set; }

        public int? CategoryId { get; set; }
    }
}
