using System.Threading;
using System.Threading.Tasks;
using Microsoft.EntityFrameworkCore;
using JobMate.Domain.Entities;

namespace JobMate.Application.Common.Interfaces
{
    public interface IJobMateDbContext
    {
        DbSet<Job> Jobs { get; }
        DbSet<JobApplication> JobApplications { get; }
        DbSet<ApplicationHistory> ApplicationHistories { get; }
        DbSet<Resume> Resumes { get; }
        DbSet<ResumeAnalysis> ResumeAnalyses { get; }
        DbSet<ResumeExperience> ResumeExperiences { get; }
        DbSet<ResumeEducation> ResumeEducations { get; }
        DbSet<ResumeSkill> ResumeSkills { get; }
        DbSet<ResumeProject> ResumeProjects { get; }
        DbSet<ResumeCertificate> ResumeCertificates { get; }
        DbSet<ResumeLanguage> ResumeLanguages { get; }
        DbSet<RefreshToken> RefreshTokens { get; }
        DbSet<Category> Categories { get; }
        DbSet<SavedJob> SavedJobs { get; }
        DbSet<DeviceToken> DeviceTokens { get; }
        DbSet<Notification> Notifications { get; }
        DbSet<Interview> Interviews { get; }

        Task<int> SaveChangesAsync(CancellationToken cancellationToken = default);
    }
}
