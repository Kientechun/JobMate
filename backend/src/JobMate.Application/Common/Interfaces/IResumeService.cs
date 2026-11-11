using System.Collections.Generic;
using System.Threading.Tasks;
using JobMate.Application.DTOs.Resumes;

namespace JobMate.Application.Common.Interfaces
{
    public interface IResumeService
    {
        Task<IEnumerable<ResumeDto>> GetMyResumesAsync(string userId);
        Task<ResumeDto> GetResumeByIdAsync(int id, string userId);
        Task<ResumeDto> CreateResumeAsync(CreateResumeRequest request, string userId);
        Task<ResumeDto> UpdateResumeAsync(int id, CreateResumeRequest request, string userId);
        Task<bool> DeleteResumeAsync(int id, string userId);
        Task<bool> SetDefaultResumeAsync(int id, string userId);
        Task<string> UploadResumeFileAsync(object file, string userId); // Use object or specific DTO to avoid IFormFile in Application layer
    }
}
