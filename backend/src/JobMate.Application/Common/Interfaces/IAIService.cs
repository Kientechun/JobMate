using JobMate.Application.DTOs.AI;
using JobMate.Domain.Entities;

namespace JobMate.Application.Common.Interfaces
{
    public interface IAIService
    {
        Task<ResumeAnalysisDto> AnalyzeResumeAsync(Resume resume);
        Task<List<JobRecommendationDto>> GetJobRecommendationsAsync(Resume resume, List<Job> availableJobs);
    }
}
