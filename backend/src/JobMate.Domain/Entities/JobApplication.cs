using JobMate.Domain.Common;
using JobMate.Domain.Enums;

namespace JobMate.Domain.Entities
{
    public class JobApplication : BaseEntity
    {
        public int JobId { get; set; }
        public virtual Job Job { get; set; }

        public int? ResumeId { get; set; }
        public virtual Resume Resume { get; set; }

        public string CandidateName { get; set; }
        public string CandidateEmail { get; set; }
        public string ResumeUrl { get; set; } // Can store snapshot or link to Resume.FileUrl
        public string CoverLetter { get; set; }
        public string UserId { get; set; }

        public ApplicationStatus Status { get; set; } = ApplicationStatus.Pending;

        public virtual ICollection<ApplicationHistory> Histories { get; set; } = new List<ApplicationHistory>();
    }
}
