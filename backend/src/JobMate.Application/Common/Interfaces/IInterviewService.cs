using JobMate.Domain.Entities;

namespace JobMate.Application.Common.Interfaces
{
    public interface IInterviewService
    {
        Task<Interview> ScheduleInterviewAsync(int applicationId, DateTime scheduledAt, string location, string type, string notes);
        Task<Interview?> GetInterviewByIdAsync(int id);
        Task<List<Interview>> GetUserInterviewsAsync(string userId);
        Task<List<Interview>> GetJobInterviewsAsync(int jobId);
        Task UpdateInterviewAsync(int id, DateTime scheduledAt, string location, string type, string notes, string status);
        Task UpdateInterviewResultAsync(int id, string result);
        Task CancelInterviewAsync(int id);
    }
}
