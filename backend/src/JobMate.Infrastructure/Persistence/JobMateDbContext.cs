using System.Reflection;
using System.Threading;
using System.Threading.Tasks;
using JobMate.Application.Common.Interfaces;
using JobMate.Domain.Entities;
using JobMate.Infrastructure.Identity;
using Microsoft.AspNetCore.Identity.EntityFrameworkCore;
using Microsoft.EntityFrameworkCore;

namespace JobMate.Infrastructure.Persistence
{
    public class JobMateDbContext : IdentityDbContext<ApplicationUser>, IJobMateDbContext
    {
        public JobMateDbContext(DbContextOptions<JobMateDbContext> options)
            : base(options)
        {
        }

        public DbSet<Job> Jobs => Set<Job>();
        public DbSet<JobApplication> JobApplications => Set<JobApplication>();
        public DbSet<ApplicationHistory> ApplicationHistories => Set<ApplicationHistory>();
        public DbSet<Resume> Resumes => Set<Resume>();
        public DbSet<ResumeAnalysis> ResumeAnalyses => Set<ResumeAnalysis>();
        public DbSet<ResumeExperience> ResumeExperiences => Set<ResumeExperience>();
        public DbSet<ResumeEducation> ResumeEducations => Set<ResumeEducation>();
        public DbSet<ResumeSkill> ResumeSkills => Set<ResumeSkill>();
        public DbSet<ResumeProject> ResumeProjects => Set<ResumeProject>();
        public DbSet<ResumeCertificate> ResumeCertificates => Set<ResumeCertificate>();
        public DbSet<ResumeLanguage> ResumeLanguages => Set<ResumeLanguage>();
        public DbSet<RefreshToken> RefreshTokens => Set<RefreshToken>();
        public DbSet<Category> Categories => Set<Category>();
        public DbSet<SavedJob> SavedJobs => Set<SavedJob>();

        protected override void OnModelCreating(ModelBuilder builder)
        {
            builder.ApplyConfigurationsFromAssembly(Assembly.GetExecutingAssembly());
            base.OnModelCreating(builder);

            builder.Entity<RefreshToken>(entity =>
            {
                entity.HasKey(e => e.Id);
                entity.Property(e => e.Token).IsRequired();
                entity.HasIndex(e => e.Token).IsUnique();
            });

            builder.Entity<Job>(entity =>
            {
                entity.Property(e => e.Title).IsRequired().HasMaxLength(200);
                entity.Property(e => e.CompanyName).IsRequired().HasMaxLength(200);
            });

            builder.Entity<Category>(entity =>
            {
                entity.Property(e => e.Name).IsRequired().HasMaxLength(100);
            });

            builder.Entity<SavedJob>(entity =>
            {
                entity.HasIndex(e => new { e.UserId, e.JobId }).IsUnique();
            });
        }

        public override async Task<int> SaveChangesAsync(CancellationToken cancellationToken = default)
        {
            // Logic for auditing can be added here
            return await base.SaveChangesAsync(cancellationToken);
        }
    }
}
