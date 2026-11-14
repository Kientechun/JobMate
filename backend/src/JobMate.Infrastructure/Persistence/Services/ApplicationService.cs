using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using JobMate.Application.Common.Interfaces;
using JobMate.Application.DTOs.Applications;
using JobMate.Application.DTOs.Jobs;
using JobMate.Domain.Entities;
using JobMate.Domain.Enums;
using Microsoft.EntityFrameworkCore;

namespace JobMate.Infrastructure.Persistence.Services
{
    public class ApplicationService : IApplicationService
    {
        private readonly IJobMateDbContext _context;
        private readonly INotificationService _notificationService;

        public ApplicationService(IJobMateDbContext context, INotificationService notificationService)
        {
            _context = context;
            _notificationService = notificationService;
        }

        public async Task<ApplicationDto> ApplyJobAsync(CreateApplicationRequest request, string userId)
        {
            // Rules
            var job = await _context.Jobs.FirstOrDefaultAsync(j => j.Id == request.JobId && j.Status == JobStatus.Published && j.ExpiryDate > DateTime.UtcNow && !j.IsDeleted);
            if (job == null) throw new Exception("Job not found or not available for applications.");

            var resume = await _context.Resumes.FirstOrDefaultAsync(r => r.Id == request.ResumeId && r.UserId == userId && !r.IsDeleted);
            if (resume == null) throw new Exception("Resume not found.");

            var existing = await _context.JobApplications.AnyAsync(a => a.JobId == request.JobId && a.UserId == userId && a.Status != ApplicationStatus.Rejected && !a.IsDeleted);
            if (existing) throw new Exception("You have already applied for this job.");

            var application = new JobApplication
            {
                JobId = request.JobId,
                ResumeId = request.ResumeId,
                UserId = userId,
                CandidateName = resume.FullName,
                CandidateEmail = resume.Email,
                ResumeUrl = resume.FileUrl,
                CoverLetter = request.CoverLetter,
                Status = ApplicationStatus.Pending,
                CreatedAt = DateTime.UtcNow,
                CreatedBy = userId
            };

            application.Histories.Add(new ApplicationHistory
            {
                Status = ApplicationStatus.Pending,
                Note = "Application submitted.",
                CreatedAt = DateTime.UtcNow,
                CreatedBy = userId
            });

            _context.JobApplications.Add(application);
            await _context.SaveChangesAsync();

            return MapToDto(application);
        }

        public async Task<IEnumerable<ApplicationDto>> GetMyApplicationsAsync(string userId)
        {
            return await _context.JobApplications
                .Include(a => a.Job)
                .Where(a => a.UserId == userId && !a.IsDeleted)
                .OrderByDescending(a => a.CreatedAt)
                .Select(a => MapToDto(a))
                .ToListAsync();
        }

        public async Task<ApplicationDto> GetApplicationByIdAsync(int id, string userId)
        {
            var application = await _context.JobApplications
                .Include(a => a.Job)
                .Include(a => a.Histories)
                .FirstOrDefaultAsync(a => a.Id == id && a.UserId == userId && !a.IsDeleted);

            return application != null ? MapToDto(application) : null;
        }

        public async Task<IEnumerable<ApplicationHistoryDto>> GetApplicationHistoryAsync(int id, string userId)
        {
            var application = await _context.JobApplications
                .Include(a => a.Histories)
                .FirstOrDefaultAsync(a => a.Id == id && a.UserId == userId && !a.IsDeleted);

            if (application == null) return null;

            return application.Histories
                .OrderByDescending(h => h.CreatedAt)
                .Select(h => new ApplicationHistoryDto
                {
                    Id = h.Id,
                    Status = h.Status,
                    Note = h.Note,
                    CreatedAt = h.CreatedAt
                });
        }

        public async Task<bool> WithdrawApplicationAsync(int id, string userId)
        {
            var application = await _context.JobApplications
                .FirstOrDefaultAsync(a => a.Id == id && a.UserId == userId && !a.IsDeleted);

            if (application == null) return false;

            // Only withdraw if still pending or under review
            if (application.Status != ApplicationStatus.Pending && application.Status != ApplicationStatus.UnderReview)
                throw new Exception("Cannot withdraw application at this stage.");

            application.IsDeleted = true; // Or add a Withdrawn status
            application.LastModifiedAt = DateTime.UtcNow;
            application.LastModifiedBy = userId;

            await _context.SaveChangesAsync();
            return true;
        }

        public async Task<IEnumerable<ApplicationDto>> GetApplicationsByJobAsync(int jobId, string recruiterId)
        {
            var job = await _context.Jobs.FirstOrDefaultAsync(j => j.Id == jobId && j.CompanyId != null); // Recruiter needs to be tied to company
            // In a real app, verify recruiterId belongs to job.CompanyId

            return await _context.JobApplications
                .Where(a => a.JobId == jobId && !a.IsDeleted)
                .OrderByDescending(a => a.CreatedAt)
                .Select(a => MapToDto(a))
                .ToListAsync();
        }

        public async Task<bool> UpdateApplicationStatusAsync(int id, UpdateApplicationStatusRequest request, string recruiterId)
        {
            var application = await _context.JobApplications
                .Include(a => a.Job)
                .Include(a => a.Histories)
                .FirstOrDefaultAsync(a => a.Id == id && !a.IsDeleted);

            if (application == null) return false;

            application.Status = request.Status;
            application.LastModifiedAt = DateTime.UtcNow;
            application.LastModifiedBy = recruiterId;

            application.Histories.Add(new ApplicationHistory
            {
                Status = request.Status,
                Note = request.Note,
                CreatedAt = DateTime.UtcNow,
                CreatedBy = recruiterId
            });

            await _context.SaveChangesAsync();

            // Send notification to Candidate
            await _notificationService.SendNotificationAsync(
                application.UserId,
                "Application Status Updated",
                $"Your application for {application.Job.Title} is now {request.Status}.",
                "StatusUpdate",
                application.Id.ToString()
            );

            return true;
        }

        private static ApplicationDto MapToDto(JobApplication a)
        {
            return new ApplicationDto
            {
                Id = a.Id,
                JobId = a.JobId,
                Job = a.Job != null ? new JobDto { Id = a.Job.Id, Title = a.Job.Title, CompanyName = a.Job.CompanyName } : null,
                ResumeId = a.ResumeId,
                CandidateName = a.CandidateName,
                CandidateEmail = a.CandidateEmail,
                ResumeUrl = a.ResumeUrl,
                CoverLetter = a.CoverLetter,
                Status = a.Status,
                CreatedAt = a.CreatedAt,
                Histories = a.Histories?.Select(h => new ApplicationHistoryDto { Id = h.Id, Status = h.Status, Note = h.Note, CreatedAt = h.CreatedAt }).ToList()
            };
        }
    }
}
