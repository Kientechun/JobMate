using System.Collections.Generic;
using System.Threading.Tasks;
using JobMate.Application.DTOs.Applications;
using JobMate.Domain.Enums;

namespace JobMate.Application.Common.Interfaces
{
    public interface IApplicationService
    {
        Task<ApplicationDto> ApplyJobAsync(CreateApplicationRequest request, string userId);
        Task<IEnumerable<ApplicationDto>> GetMyApplicationsAsync(string userId);
        Task<ApplicationDto> GetApplicationByIdAsync(int id, string userId);
        Task<IEnumerable<ApplicationHistoryDto>> GetApplicationHistoryAsync(int id, string userId);
        Task<bool> WithdrawApplicationAsync(int id, string userId);

        // Recruiter actions
        Task<IEnumerable<ApplicationDto>> GetApplicationsByJobAsync(int jobId, string recruiterId);
        Task<bool> UpdateApplicationStatusAsync(int id, UpdateApplicationStatusRequest request, string recruiterId);
    }
}
