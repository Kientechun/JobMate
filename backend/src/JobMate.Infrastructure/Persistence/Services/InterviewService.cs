using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using JobMate.Application.Common.Interfaces;
using JobMate.Domain.Entities;
using Microsoft.EntityFrameworkCore;

namespace JobMate.Infrastructure.Persistence.Services
{
    public class InterviewService : IInterviewService
    {
        private readonly IJobMateDbContext _context;
        private readonly INotificationService _notificationService;

        public InterviewService(IJobMateDbContext context, INotificationService notificationService)
        {
            _context = context;
            _notificationService = notificationService;
        }

        public async Task<Interview> ScheduleInterviewAsync(int applicationId, DateTime scheduledAt, string location, string type, string notes)
        {
            var application = await _context.JobApplications
                .Include(a => a.Job)
                .FirstOrDefaultAsync(a => a.Id == applicationId);

            if (application == null) throw new Exception("Application not found");

            var interview = new Interview
            {
                JobApplicationId = applicationId,
                ScheduledAt = scheduledAt,
                Location = location,
                Type = type,
                Notes = notes,
                Status = "Scheduled",
                CreatedAt = DateTime.UtcNow,
                UpdatedAt = DateTime.UtcNow
            };

            _context.Interviews.Add(interview);
            await _context.SaveChangesAsync();

            // Send notification to Candidate
            await _notificationService.SendNotificationAsync(
                application.UserId,
                "New Interview Scheduled",
                $"You have an interview for {application.Job.Title} at {scheduledAt:f}",
                "InterviewSchedule",
                interview.Id.ToString()
            );

            return interview;
        }

        public async Task<Interview?> GetInterviewByIdAsync(int id)
        {
            return await _context.Interviews
                .Include(i => i.JobApplication)
                .ThenInclude(a => a.Job)
                .FirstOrDefaultAsync(i => i.Id == id);
        }

        public async Task<List<Interview>> GetUserInterviewsAsync(string userId)
        {
            return await _context.Interviews
                .Include(i => i.JobApplication)
                .ThenInclude(a => a.Job)
                .Where(i => i.JobApplication.UserId == userId)
                .OrderByDescending(i => i.ScheduledAt)
                .ToListAsync();
        }

        public async Task<List<Interview>> GetJobInterviewsAsync(int jobId)
        {
            return await _context.Interviews
                .Include(i => i.JobApplication)
                .Where(i => i.JobApplication.JobId == jobId)
                .OrderByDescending(i => i.ScheduledAt)
                .ToListAsync();
        }

        public async Task UpdateInterviewAsync(int id, DateTime scheduledAt, string location, string type, string notes, string status)
        {
            var interview = await _context.Interviews
                .Include(i => i.JobApplication)
                .ThenInclude(a => a.Job)
                .FirstOrDefaultAsync(i => i.Id == id);

            if (interview == null) throw new Exception("Interview not found");

            interview.ScheduledAt = scheduledAt;
            interview.Location = location;
            interview.Type = type;
            interview.Notes = notes;
            interview.Status = status;
            interview.UpdatedAt = DateTime.UtcNow;

            await _context.SaveChangesAsync();

            // Notify Candidate of changes
            await _notificationService.SendNotificationAsync(
                interview.JobApplication.UserId,
                "Interview Updated",
                $"Your interview for {interview.JobApplication.Job.Title} has been updated.",
                "InterviewSchedule",
                interview.Id.ToString()
            );
        }

        public async Task UpdateInterviewResultAsync(int id, string result)
        {
            var interview = await _context.Interviews.FindAsync(id);
            if (interview == null) throw new Exception("Interview not found");

            interview.Result = result;
            interview.Status = "Completed";
            interview.UpdatedAt = DateTime.UtcNow;

            await _context.SaveChangesAsync();
        }

        public async Task CancelInterviewAsync(int id)
        {
            var interview = await _context.Interviews
                .Include(i => i.JobApplication)
                .ThenInclude(a => a.Job)
                .FirstOrDefaultAsync(i => i.Id == id);

            if (interview == null) throw new Exception("Interview not found");

            interview.Status = "Cancelled";
            interview.UpdatedAt = DateTime.UtcNow;

            await _context.SaveChangesAsync();

            await _notificationService.SendNotificationAsync(
                interview.JobApplication.UserId,
                "Interview Cancelled",
                $"Your interview for {interview.JobApplication.Job.Title} has been cancelled.",
                "InterviewSchedule",
                interview.Id.ToString()
            );
        }
    }
}
