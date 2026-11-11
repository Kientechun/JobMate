using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using JobMate.Application.Common.Interfaces;
using JobMate.Application.Common.Models;
using JobMate.Application.DTOs.Jobs;
using JobMate.Domain.Entities;
using JobMate.Domain.Enums;
using Microsoft.EntityFrameworkCore;

namespace JobMate.Infrastructure.Persistence.Services
{
    public class JobService : IJobService
    {
        private readonly IJobMateDbContext _context;

        public JobService(IJobMateDbContext context)
        {
            _context = context;
        }

        public async Task<PaginatedList<JobDto>> GetJobsAsync(JobSearchFilters filters, int pageIndex, int pageSize)
        {
            var query = _context.Jobs
                .Where(j => j.Status == JobStatus.Published && j.ExpiryDate > DateTime.UtcNow && !j.IsDeleted)
                .AsQueryable();

            if (!string.IsNullOrEmpty(filters.Keyword))
            {
                query = query.Where(j => j.Title.Contains(filters.Keyword) || j.Description.Contains(filters.Keyword));
            }

            if (!string.IsNullOrEmpty(filters.Location))
            {
                query = query.Where(j => j.Location.Contains(filters.Location));
            }

            if (filters.MinSalary.HasValue)
            {
                query = query.Where(j => j.SalaryRange >= filters.MinSalary.Value);
            }

            if (!string.IsNullOrEmpty(filters.JobType))
            {
                query = query.Where(j => j.JobType == filters.JobType);
            }

            if (!string.IsNullOrEmpty(filters.Experience))
            {
                query = query.Where(j => j.Experience == filters.Experience);
            }

            if (!string.IsNullOrEmpty(filters.Category))
            {
                query = query.Where(j => j.Category != null && j.Category.Name == filters.Category);
            }

            var result = await PaginatedList<Job>.CreateAsync(query.OrderByDescending(j => j.CreatedAt), pageIndex, pageSize);

            return new PaginatedList<JobDto>(
                result.Items.Select(MapToDto).ToList(),
                result.TotalCount,
                result.PageIndex,
                pageSize);
        }

        public async Task<JobDto?> GetJobByIdAsync(int id)
        {
            var job = await _context.Jobs
                .FirstOrDefaultAsync(j => j.Id == id && j.Status == JobStatus.Published && !j.IsDeleted);

            return job != null ? MapToDto(job) : null;
        }

        public async Task<IEnumerable<JobDto>> GetFeaturedJobsAsync(int count)
        {
            return await _context.Jobs
                .Where(j => j.Status == JobStatus.Published && j.ExpiryDate > DateTime.UtcNow && !j.IsDeleted)
                .OrderByDescending(j => j.CreatedAt)
                .Take(count)
                .Select(j => MapToDto(j))
                .ToListAsync();
        }

        public async Task<IEnumerable<CategoryDto>> GetCategoriesAsync()
        {
            return await _context.Categories
                .Select(c => new CategoryDto
                {
                    Id = c.Id,
                    Name = c.Name,
                    Description = c.Description,
                    Icon = c.Icon
                }).ToListAsync();
        }

        public async Task<PaginatedList<JobDto>> GetSavedJobsAsync(string userId, int pageIndex, int pageSize)
        {
            var query = _context.SavedJobs
                .Where(s => s.UserId == userId && !s.IsDeleted)
                .Select(s => s.Job)
                .Where(j => !j.IsDeleted)
                .AsQueryable();

            var result = await PaginatedList<Job>.CreateAsync(query.OrderByDescending(j => j.CreatedAt), pageIndex, pageSize);

            return new PaginatedList<JobDto>(
                result.Items.Select(MapToDto).ToList(),
                result.TotalCount,
                result.PageIndex,
                pageSize);
        }

        public async Task<ApiResponse<bool>> SaveJobAsync(int jobId, string userId)
        {
            var exists = await _context.SavedJobs.AnyAsync(s => s.UserId == userId && s.JobId == jobId);
            if (exists) return ApiResponse<bool>.Success(true, "Job already saved.");

            var savedJob = new SavedJob
            {
                UserId = userId,
                JobId = jobId,
                CreatedAt = DateTime.UtcNow,
                CreatedBy = userId
            };

            _context.SavedJobs.Add(savedJob);
            await _context.SaveChangesAsync();

            return ApiResponse<bool>.Success(true, "Job saved successfully.");
        }

        public async Task<ApiResponse<bool>> UnsaveJobAsync(int jobId, string userId)
        {
            var savedJob = await _context.SavedJobs.FirstOrDefaultAsync(s => s.UserId == userId && s.JobId == jobId);
            if (savedJob == null) return ApiResponse<bool>.Failure("Saved job not found.");

            _context.SavedJobs.Remove(savedJob);
            await _context.SaveChangesAsync();

            return ApiResponse<bool>.Success(true, "Job removed from saved list.");
        }

        public async Task<PaginatedList<JobDto>> GetRecruiterJobsAsync(string companyId, int pageIndex, int pageSize)
        {
            var query = _context.Jobs
                .Where(j => j.CompanyId == companyId && !j.IsDeleted)
                .AsQueryable();

            var result = await PaginatedList<Job>.CreateAsync(query.OrderByDescending(j => j.CreatedAt), pageIndex, pageSize);

            return new PaginatedList<JobDto>(
                result.Items.Select(MapToDto).ToList(),
                result.TotalCount,
                result.PageIndex,
                pageSize);
        }

        public async Task<ApiResponse<int>> CreateJobAsync(CreateJobRequest request, string userId)
        {
            var job = new Job
            {
                Title = request.Title,
                Description = request.Description,
                CompanyName = request.CompanyName,
                Location = request.Location,
                SalaryRange = request.SalaryRange,
                JobType = request.JobType,
                Experience = request.Experience,
                ExpiryDate = request.ExpiryDate,
                CompanyId = request.CompanyId,
                CategoryId = request.CategoryId,
                Status = JobStatus.Pending,
                CreatedBy = userId,
                CreatedAt = DateTime.UtcNow,
                LastModifiedBy = userId
            };

            _context.Jobs.Add(job);
            await _context.SaveChangesAsync();

            return ApiResponse<int>.Success(job.Id, "Job created successfully and pending approval.");
        }

        public async Task<ApiResponse<bool>> UpdateJobAsync(int id, UpdateJobRequest request, string userId, string companyId)
        {
            var job = await _context.Jobs.FindAsync(id);

            if (job == null || job.IsDeleted)
                return ApiResponse<bool>.Failure("Job not found.");

            if (job.CompanyId != companyId)
                return ApiResponse<bool>.Failure("You do not have permission to update this job.");

            job.Title = request.Title;
            job.Description = request.Description;
            job.CompanyName = request.CompanyName;
            job.Location = request.Location;
            job.SalaryRange = request.SalaryRange;
            job.JobType = request.JobType;
            job.ExpiryDate = request.ExpiryDate;
            job.Status = JobStatus.Pending;
            job.LastModifiedBy = userId;
            job.LastModifiedAt = DateTime.UtcNow;

            await _context.SaveChangesAsync();
            return ApiResponse<bool>.Success(true, "Job updated and pending re-approval.");
        }

        public async Task<ApiResponse<bool>> CloseJobAsync(int id, string userId, string companyId)
        {
            var job = await _context.Jobs.FindAsync(id);

            if (job == null || job.IsDeleted)
                return ApiResponse<bool>.Failure("Job not found.");

            if (job.CompanyId != companyId)
                return ApiResponse<bool>.Failure("You do not have permission to close this job.");

            job.Status = JobStatus.Closed;
            job.LastModifiedBy = userId;
            job.LastModifiedAt = DateTime.UtcNow;

            await _context.SaveChangesAsync();
            return ApiResponse<bool>.Success(true, "Job closed.");
        }

        public async Task<JobDto?> GetRecruiterJobByIdAsync(int id, string companyId)
        {
            var job = await _context.Jobs
                .FirstOrDefaultAsync(j => j.Id == id && j.CompanyId == companyId && !j.IsDeleted);

            return job != null ? MapToDto(job) : null;
        }

        public async Task<PaginatedList<JobDto>> GetPendingJobsAsync(int pageIndex, int pageSize)
        {
            var query = _context.Jobs
                .Where(j => j.Status == JobStatus.Pending && !j.IsDeleted)
                .AsQueryable();

            var result = await PaginatedList<Job>.CreateAsync(query.OrderBy(j => j.CreatedAt), pageIndex, pageSize);

            return new PaginatedList<JobDto>(
                result.Items.Select(MapToDto).ToList(),
                result.TotalCount,
                result.PageIndex,
                pageSize);
        }

        public async Task<ApiResponse<bool>> ApproveJobAsync(int id)
        {
            var job = await _context.Jobs.FindAsync(id);
            if (job == null || job.IsDeleted) return ApiResponse<bool>.Failure("Job not found.");

            job.Status = JobStatus.Published;
            await _context.SaveChangesAsync();
            return ApiResponse<bool>.Success(true, "Job approved.");
        }

        public async Task<ApiResponse<bool>> RejectJobAsync(int id)
        {
            var job = await _context.Jobs.FindAsync(id);
            if (job == null || job.IsDeleted) return ApiResponse<bool>.Failure("Job not found.");

            job.Status = JobStatus.Rejected;
            await _context.SaveChangesAsync();
            return ApiResponse<bool>.Success(true, "Job rejected.");
        }

        private static JobDto MapToDto(Job job)
        {
            return new JobDto
            {
                Id = job.Id,
                Title = job.Title,
                Description = job.Description,
                CompanyName = job.CompanyName,
                Location = job.Location,
                SalaryRange = job.SalaryRange,
                JobType = job.JobType,
                Experience = job.Experience,
                ExpiryDate = job.ExpiryDate,
                Status = job.Status,
                CompanyId = job.CompanyId,
                CreatedAt = job.CreatedAt
            };
        }
    }
}
