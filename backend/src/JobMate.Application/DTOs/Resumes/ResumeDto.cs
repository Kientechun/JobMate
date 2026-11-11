using System;
using System.Collections.Generic;

namespace JobMate.Application.DTOs.Resumes
{
    public class ResumeDto
    {
        public int Id { get; set; }
        public string Title { get; set; }
        public string FullName { get; set; }
        public string Email { get; set; }
        public string PhoneNumber { get; set; }
        public string Address { get; set; }
        public string Summary { get; set; }
        public bool IsDefault { get; set; }
        public string FileUrl { get; set; }
        public DateTime CreatedAt { get; set; }

        public List<ExperienceDto> Experiences { get; set; }
        public List<EducationDto> Educations { get; set; }
        public List<SkillDto> Skills { get; set; }
        public List<ProjectDto> Projects { get; set; }
        public List<CertificateDto> Certificates { get; set; }
        public List<LanguageDto> Languages { get; set; }
    }

    public class ExperienceDto
    {
        public int Id { get; set; }
        public string CompanyName { get; set; }
        public string Position { get; set; }
        public string StartDate { get; set; }
        public string EndDate { get; set; }
        public string Description { get; set; }
    }

    public class EducationDto
    {
        public int Id { get; set; }
        public string SchoolName { get; set; }
        public string Degree { get; set; }
        public string FieldOfStudy { get; set; }
        public string StartDate { get; set; }
        public string EndDate { get; set; }
        public string Description { get; set; }
    }

    public class SkillDto
    {
        public int Id { get; set; }
        public string SkillName { get; set; }
        public string Level { get; set; }
    }

    public class ProjectDto
    {
        public int Id { get; set; }
        public string ProjectName { get; set; }
        public string Description { get; set; }
        public string Link { get; set; }
    }

    public class CertificateDto
    {
        public int Id { get; set; }
        public string CertificateName { get; set; }
        public string IssuingOrganization { get; set; }
        public string IssueDate { get; set; }
        public string ExpiryDate { get; set; }
    }

    public class LanguageDto
    {
        public int Id { get; set; }
        public string Language { get; set; }
        public string Level { get; set; }
    }

    public class CreateResumeRequest
    {
        public string Title { get; set; }
        public string FullName { get; set; }
        public string Email { get; set; }
        public string PhoneNumber { get; set; }
        public string Address { get; set; }
        public string Summary { get; set; }

        public List<ExperienceDto> Experiences { get; set; }
        public List<EducationDto> Educations { get; set; }
        public List<SkillDto> Skills { get; set; }
        public List<ProjectDto> Projects { get; set; }
        public List<CertificateDto> Certificates { get; set; }
        public List<LanguageDto> Languages { get; set; }
    }
}
