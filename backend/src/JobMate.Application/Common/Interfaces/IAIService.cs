using JobMate.Application.DTOs.AI;
using JobMate.Domain.Entities;

namespace JobMate.Application.Common.Interfaces
{
    public interface IAIService
    {
        Task<ResumeAnalysisDto> AnalyzeResumeAsync(Resume resume);
        Task<List<JobRecommendationDto>> GetJobRecommendationsAsync(Resume resume, List<Job> availableJobs);
    }

    public class JobRecommendationDto
    {
        public int JobId { get; set; }
        public string JobTitle { get; set; } = string.Empty;
        public string CompanyName { get; set; } = string.Empty;
        public double MatchScore { get; set; }
        public string Reason { get; set; } = string.Empty;
    }
}
