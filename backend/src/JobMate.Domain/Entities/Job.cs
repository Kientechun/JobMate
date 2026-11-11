using System;
using System.Collections.Generic;
using JobMate.Domain.Common;
using JobMate.Domain.Enums;

namespace JobMate.Domain.Entities
{
    public class Job : BaseEntity
    {
        public string Title { get; set; } = string.Empty;
        public string Description { get; set; } = string.Empty;
        public string CompanyName { get; set; } = string.Empty;
        public string Location { get; set; } = string.Empty;
        public decimal SalaryRange { get; set; }
        public string JobType { get; set; } = string.Empty;
        public string Experience { get; set; } = string.Empty;
        public DateTime ExpiryDate { get; set; }
        public JobStatus Status { get; set; } = JobStatus.Draft;
        public string CompanyId { get; set; } = string.Empty;
        public int? CategoryId { get; set; }
        public virtual Category? Category { get; set; }

        public virtual ICollection<JobApplication> Applications { get; set; } = new List<JobApplication>();
    }
}
