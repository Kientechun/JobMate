using System.Collections.Generic;
using JobMate.Domain.Common;

namespace JobMate.Domain.Entities
{
    public class Resume : BaseEntity
    {
        public string Title { get; set; }
        public string FullName { get; set; }
        public string Email { get; set; }
        public string PhoneNumber { get; set; }
        public string Address { get; set; }
        public string Summary { get; set; }
        public bool IsDefault { get; set; }
        public string FileUrl { get; set; } // If uploaded PDF
        public string UserId { get; set; }

        public virtual ICollection<ResumeExperience> Experiences { get; set; } = new List<ResumeExperience>();
        public virtual ICollection<ResumeEducation> Educations { get; set; } = new List<ResumeEducation>();
        public virtual ICollection<ResumeSkill> Skills { get; set; } = new List<ResumeSkill>();
        public virtual ICollection<ResumeProject> Projects { get; set; } = new List<ResumeProject>();
        public virtual ICollection<ResumeCertificate> Certificates { get; set; } = new List<ResumeCertificate>();
        public virtual ICollection<ResumeLanguage> Languages { get; set; } = new List<ResumeLanguage>();
    }

    public class ResumeExperience : BaseEntity
    {
        public int ResumeId { get; set; }
        public string CompanyName { get; set; }
        public string Position { get; set; }
        public string StartDate { get; set; }
        public string EndDate { get; set; }
        public string Description { get; set; }
    }

    public class ResumeEducation : BaseEntity
    {
        public int ResumeId { get; set; }
        public string SchoolName { get; set; }
        public string Degree { get; set; }
        public string FieldOfStudy { get; set; }
        public string StartDate { get; set; }
        public string EndDate { get; set; }
        public string Description { get; set; }
    }

    public class ResumeSkill : BaseEntity
    {
        public int ResumeId { get; set; }
        public string SkillName { get; set; }
        public string Level { get; set; } // e.g., Beginner, Intermediate, Expert
    }

    public class ResumeProject : BaseEntity
    {
        public int ResumeId { get; set; }
        public string ProjectName { get; set; }
        public string Description { get; set; }
        public string Link { get; set; }
    }

    public class ResumeCertificate : BaseEntity
    {
        public int ResumeId { get; set; }
        public string CertificateName { get; set; }
        public string IssuingOrganization { get; set; }
        public string IssueDate { get; set; }
        public string ExpiryDate { get; set; }
    }

    public class ResumeLanguage : BaseEntity
    {
        public int ResumeId { get; set; }
        public string Language { get; set; }
        public string Level { get; set; } // e.g., Native, Professional, Limited
    }
}
