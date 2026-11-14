using System.Collections.Generic;
using System.Security.Claims;
using System.Threading.Tasks;
using JobMate.Application.Common.Interfaces;
using JobMate.Application.Common.Models;
using JobMate.Application.DTOs.Resumes;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;

namespace JobMate.API.Controllers.v1
{
    [Authorize]
    [ApiController]
    [Route("api/v1/[controller]")]
    public class ResumesController : ControllerBase
    {
        private readonly IResumeService _resumeService;

        public ResumesController(IResumeService resumeService)
        {
            _resumeService = resumeService;
        }

        private string UserId => User.FindFirstValue(ClaimTypes.NameIdentifier)!;

        [HttpGet]
        public async Task<ActionResult<ApiResponse<IEnumerable<ResumeDto>>>> GetMyResumes()
        {
            var resumes = await _resumeService.GetMyResumesAsync(UserId);
            return Ok(ApiResponse<IEnumerable<ResumeDto>>.Success(resumes));
        }

        [HttpGet("{id}")]
        public async Task<ActionResult<ApiResponse<ResumeDto>>> GetResume(int id)
        {
            var resume = await _resumeService.GetResumeByIdAsync(id, UserId);
            if (resume == null) return NotFound(ApiResponse<ResumeDto>.Failure("Resume not found."));
            return Ok(ApiResponse<ResumeDto>.Success(resume));
        }

        [HttpPost]
        public async Task<ActionResult<ApiResponse<ResumeDto>>> Create(CreateResumeRequest request)
        {
            var resume = await _resumeService.CreateResumeAsync(request, UserId);
            return Ok(ApiResponse<ResumeDto>.Success(resume, "Resume created successfully."));
        }

        [HttpPut("{id}")]
        public async Task<ActionResult<ApiResponse<ResumeDto>>> Update(int id, CreateResumeRequest request)
        {
            var resume = await _resumeService.UpdateResumeAsync(id, request, UserId);
            if (resume == null) return NotFound(ApiResponse<ResumeDto>.Failure("Resume not found."));
            return Ok(ApiResponse<ResumeDto>.Success(resume, "Resume updated successfully."));
        }

        [HttpDelete("{id}")]
        public async Task<ActionResult<ApiResponse<bool>>> Delete(int id)
        {
            var result = await _resumeService.DeleteResumeAsync(id, UserId);
            if (!result) return NotFound(ApiResponse<bool>.Failure("Resume not found."));
            return Ok(ApiResponse<bool>.Success(true, "Resume deleted successfully."));
        }

        [HttpPatch("{id}/default")]
        public async Task<ActionResult<ApiResponse<bool>>> SetDefault(int id)
        {
            var result = await _resumeService.SetDefaultResumeAsync(id, UserId);
            if (!result) return NotFound(ApiResponse<bool>.Failure("Resume not found."));
            return Ok(ApiResponse<bool>.Success(true, "Default resume updated."));
        }

        [HttpPost("upload")]
        public async Task<ActionResult<ApiResponse<string>>> Upload(IFormFile file)
        {
            try
            {
                var url = await _resumeService.UploadResumeFileAsync(file, UserId);
                return Ok(ApiResponse<string>.Success(url, "File uploaded successfully."));
            }
            catch (System.Exception ex)
            {
                return BadRequest(ApiResponse<string>.Failure(ex.Message));
            }
        }
    }
}
