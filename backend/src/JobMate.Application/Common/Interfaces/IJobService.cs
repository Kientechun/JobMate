using System.Collections.Generic;
using System.Threading.Tasks;
using JobMate.Application.Common.Models;
using JobMate.Application.DTOs.Jobs;
using JobMate.Domain.Enums;

namespace JobMate.Application.Common.Interfaces
{
    public interface IJobService
    {
        // Candidate methods
        Task<PaginatedList<JobDto>> GetJobsAsync(JobSearchFilters filters, int pageIndex, int pageSize);
        Task<JobDto?> GetJobByIdAsync(int id);
        Task<IEnumerable<JobDto>> GetFeaturedJobsAsync(int count);
        Task<IEnumerable<CategoryDto>> GetCategoriesAsync();
        Task<PaginatedList<JobDto>> GetSavedJobsAsync(string userId, int pageIndex, int pageSize);
        Task<ApiResponse<bool>> SaveJobAsync(int jobId, string userId);
        Task<ApiResponse<bool>> UnsaveJobAsync(int jobId, string userId);

        // Recruiter methods
        Task<PaginatedList<JobDto>> GetRecruiterJobsAsync(int companyId, int pageIndex, int pageSize);
        Task<ApiResponse<int>> CreateJobAsync(CreateJobRequest request, string userId);
        Task<ApiResponse<bool>> UpdateJobAsync(int id, UpdateJobRequest request, string userId, int companyId);
        Task<ApiResponse<bool>> CloseJobAsync(int id, string userId, int companyId);
        Task<JobDto?> GetRecruiterJobByIdAsync(int id, int companyId);

        // Admin methods
        Task<PaginatedList<JobDto>> GetPendingJobsAsync(int pageIndex, int pageSize);
        Task<ApiResponse<bool>> ApproveJobAsync(int id);
        Task<ApiResponse<bool>> RejectJobAsync(int id);
    }

    public class CategoryDto
    {
        public int Id { get; set; }
        public string Name { get; set; } = string.Empty;
        public string Description { get; set; } = string.Empty;
        public string Icon { get; set; } = string.Empty;
    }

    public class JobSearchFilters
    {
        public string? Keyword { get; set; }
        public string? Category { get; set; }
        public string? Location { get; set; }
        public decimal? MinSalary { get; set; }
        public decimal? MaxSalary { get; set; }
        public string? JobType { get; set; }
        public string? Experience { get; set; }
    }
}
