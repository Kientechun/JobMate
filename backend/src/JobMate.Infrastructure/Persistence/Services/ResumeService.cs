using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Threading.Tasks;
using JobMate.Application.Common.Interfaces;
using JobMate.Application.DTOs.Resumes;
using JobMate.Domain.Entities;
using Microsoft.AspNetCore.Http;
using Microsoft.EntityFrameworkCore;

namespace JobMate.Infrastructure.Persistence.Services
{
    public class ResumeService : IResumeService
    {
        private readonly IJobMateDbContext _context;

        public ResumeService(IJobMateDbContext context)
        {
            _context = context;
        }

        public async Task<IEnumerable<ResumeDto>> GetMyResumesAsync(string userId)
        {
            return await _context.Resumes
                .Where(r => r.UserId == userId && !r.IsDeleted)
                .Select(r => MapToDto(r))
                .ToListAsync();
        }

        public async Task<ResumeDto> GetResumeByIdAsync(int id, string userId)
        {
            var resume = await _context.Resumes
                .Include(r => r.Experiences)
                .Include(r => r.Educations)
                .Include(r => r.Skills)
                .Include(r => r.Projects)
                .Include(r => r.Certificates)
                .Include(r => r.Languages)
                .FirstOrDefaultAsync(r => r.Id == id && r.UserId == userId && !r.IsDeleted);

            return resume != null ? MapToDto(resume) : null;
        }

        public async Task<ResumeDto> CreateResumeAsync(CreateResumeRequest request, string userId)
        {
            var resume = new Resume
            {
                Title = request.Title,
                FullName = request.FullName,
                Email = request.Email,
                PhoneNumber = request.PhoneNumber,
                Address = request.Address,
                Summary = request.Summary,
                FileUrl = request.FileUrl,
                UserId = userId,
                CreatedAt = DateTime.UtcNow,
                CreatedBy = userId
            };

            if (request.Experiences != null)
                resume.Experiences = request.Experiences.Select(e => new ResumeExperience { CompanyName = e.CompanyName, Position = e.Position, StartDate = e.StartDate, EndDate = e.EndDate, Description = e.Description }).ToList();

            if (request.Educations != null)
                resume.Educations = request.Educations.Select(e => new ResumeEducation { SchoolName = e.SchoolName, Degree = e.Degree, FieldOfStudy = e.FieldOfStudy, StartDate = e.StartDate, EndDate = e.EndDate, Description = e.Description }).ToList();

            if (request.Skills != null)
                resume.Skills = request.Skills.Select(s => new ResumeSkill { SkillName = s.SkillName, Level = s.Level }).ToList();

            if (request.Projects != null)
                resume.Projects = request.Projects.Select(p => new ResumeProject { ProjectName = p.ProjectName, Description = p.Description, Link = p.Link }).ToList();

            if (request.Certificates != null)
                resume.Certificates = request.Certificates.Select(c => new ResumeCertificate { CertificateName = c.CertificateName, IssuingOrganization = c.IssuingOrganization, IssueDate = c.IssueDate, ExpiryDate = c.ExpiryDate }).ToList();

            if (request.Languages != null)
                resume.Languages = request.Languages.Select(l => new ResumeLanguage { Language = l.Language, Level = l.Level }).ToList();

            // If it's the first resume, make it default
            var hasAny = await _context.Resumes.AnyAsync(r => r.UserId == userId && !r.IsDeleted);
            if (!hasAny) resume.IsDefault = true;

            _context.Resumes.Add(resume);
            await _context.SaveChangesAsync();

            return MapToDto(resume);
        }

        public async Task<ResumeDto> UpdateResumeAsync(int id, CreateResumeRequest request, string userId)
        {
            var resume = await _context.Resumes
                .Include(r => r.Experiences)
                .Include(r => r.Educations)
                .Include(r => r.Skills)
                .Include(r => r.Projects)
                .Include(r => r.Certificates)
                .Include(r => r.Languages)
                .FirstOrDefaultAsync(r => r.Id == id && r.UserId == userId && !r.IsDeleted);

            if (resume == null) return null;

            resume.Title = request.Title;
            resume.FullName = request.FullName;
            resume.Email = request.Email;
            resume.PhoneNumber = request.PhoneNumber;
            resume.Address = request.Address;
            resume.Summary = request.Summary;
            resume.FileUrl = request.FileUrl;
            resume.LastModifiedAt = DateTime.UtcNow;
            resume.LastModifiedBy = userId;

            // Simple update: remove old and add new for child collections
            _context.ResumeExperiences.RemoveRange(resume.Experiences);
            _context.ResumeEducations.RemoveRange(resume.Educations);
            _context.ResumeSkills.RemoveRange(resume.Skills);
            _context.ResumeProjects.RemoveRange(resume.Projects);
            _context.ResumeCertificates.RemoveRange(resume.Certificates);
            _context.ResumeLanguages.RemoveRange(resume.Languages);

            if (request.Experiences != null)
                resume.Experiences = request.Experiences.Select(e => new ResumeExperience { CompanyName = e.CompanyName, Position = e.Position, StartDate = e.StartDate, EndDate = e.EndDate, Description = e.Description }).ToList();

            if (request.Educations != null)
                resume.Educations = request.Educations.Select(e => new ResumeEducation { SchoolName = e.SchoolName, Degree = e.Degree, FieldOfStudy = e.FieldOfStudy, StartDate = e.StartDate, EndDate = e.EndDate, Description = e.Description }).ToList();

            if (request.Skills != null)
                resume.Skills = request.Skills.Select(s => new ResumeSkill { SkillName = s.SkillName, Level = s.Level }).ToList();

            if (request.Projects != null)
                resume.Projects = request.Projects.Select(p => new ResumeProject { ProjectName = p.ProjectName, Description = p.Description, Link = p.Link }).ToList();

            if (request.Certificates != null)
                resume.Certificates = request.Certificates.Select(c => new ResumeCertificate { CertificateName = c.CertificateName, IssuingOrganization = c.IssuingOrganization, IssueDate = c.IssueDate, ExpiryDate = c.ExpiryDate }).ToList();

            if (request.Languages != null)
                resume.Languages = request.Languages.Select(l => new ResumeLanguage { Language = l.Language, Level = l.Level }).ToList();

            await _context.SaveChangesAsync();
            return MapToDto(resume);
        }

        public async Task<bool> DeleteResumeAsync(int id, string userId)
        {
            var resume = await _context.Resumes.FirstOrDefaultAsync(r => r.Id == id && r.UserId == userId);
            if (resume == null) return false;

            resume.IsDeleted = true;
            await _context.SaveChangesAsync();
            return true;
        }

        public async Task<bool> SetDefaultResumeAsync(int id, string userId)
        {
            var resume = await _context.Resumes.FirstOrDefaultAsync(r => r.Id == id && r.UserId == userId && !r.IsDeleted);
            if (resume == null) return false;

            var defaultResumes = await _context.Resumes.Where(r => r.UserId == userId && r.IsDefault && !r.IsDeleted).ToListAsync();
            foreach (var r in defaultResumes)
            {
                r.IsDefault = false;
            }

            resume.IsDefault = true;
            await _context.SaveChangesAsync();
            return true;
        }

        public async Task<string> UploadResumeFileAsync(object file, string userId)
        {
            var formFile = file as IFormFile;
            if (formFile == null || formFile.Length == 0) return null;

            var extension = Path.GetExtension(formFile.FileName).ToLower();
            if (extension != ".pdf") throw new Exception("Only PDF files are allowed.");
            if (formFile.Length > 5 * 1024 * 1024) throw new Exception("File size cannot exceed 5MB.");

            var fileName = $"{Guid.NewGuid()}{extension}";
            var path = Path.Combine(Directory.GetCurrentDirectory(), "wwwroot", "uploads", "resumes");

            if (!Directory.Exists(path))
                Directory.CreateDirectory(path);

            var filePath = Path.Combine(path, fileName);
            using (var stream = new FileStream(filePath, FileMode.Create))
            {
                await formFile.CopyToAsync(stream);
            }

            return $"/uploads/resumes/{fileName}";
        }

        private static ResumeDto MapToDto(Resume r)
        {
            return new ResumeDto
            {
                Id = r.Id,
                Title = r.Title,
                FullName = r.FullName,
                Email = r.Email,
                PhoneNumber = r.PhoneNumber,
                Address = r.Address,
                Summary = r.Summary,
                IsDefault = r.IsDefault,
                FileUrl = r.FileUrl,
                CreatedAt = r.CreatedAt,
                Experiences = r.Experiences?.Select(e => new ExperienceDto { Id = e.Id, CompanyName = e.CompanyName, Position = e.Position, StartDate = e.StartDate, EndDate = e.EndDate, Description = e.Description }).ToList(),
                Educations = r.Educations?.Select(e => new EducationDto { Id = e.Id, SchoolName = e.SchoolName, Degree = e.Degree, FieldOfStudy = e.FieldOfStudy, StartDate = e.StartDate, EndDate = e.EndDate, Description = e.Description }).ToList(),
                Skills = r.Skills?.Select(s => new SkillDto { Id = s.Id, SkillName = s.SkillName, Level = s.Level }).ToList(),
                Projects = r.Projects?.Select(p => new ProjectDto { Id = p.Id, ProjectName = p.ProjectName, Description = p.Description, Link = p.Link }).ToList(),
                Certificates = r.Certificates?.Select(c => new CertificateDto { Id = c.Id, CertificateName = c.CertificateName, IssuingOrganization = c.IssuingOrganization, IssueDate = c.IssueDate, ExpiryDate = c.ExpiryDate }).ToList(),
                Languages = r.Languages?.Select(l => new LanguageDto { Id = l.Id, Language = l.Language, Level = l.Level }).ToList()
            };
        }
    }
}
