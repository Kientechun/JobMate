using System.Collections.Generic;
using System.Security.Claims;
using System.Threading.Tasks;
using JobMate.Application.Common.Interfaces;
using JobMate.Application.Common.Models;
using JobMate.Application.DTOs.Applications;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;

namespace JobMate.API.Controllers.v1
{
    [Authorize]
    [ApiController]
    [Route("api/v1/[controller]")]
    public class ApplicationsController : ControllerBase
    {
        private readonly IApplicationService _applicationService;

        public ApplicationsController(IApplicationService applicationService)
        {
            _applicationService = applicationService;
        }

        private string UserId => User.FindFirstValue(ClaimTypes.NameIdentifier)!;

        [HttpPost]
        public async Task<ActionResult<ApiResponse<ApplicationDto>>> Apply(CreateApplicationRequest request)
        {
            try
            {
                var application = await _applicationService.ApplyJobAsync(request, UserId);
                return Ok(ApiResponse<ApplicationDto>.Success(application, "Applied successfully."));
            }
            catch (System.Exception ex)
            {
                return BadRequest(ApiResponse<ApplicationDto>.Failure(ex.Message));
            }
        }

        [HttpGet("my")]
        public async Task<ActionResult<ApiResponse<IEnumerable<ApplicationDto>>>> GetMyApplications()
        {
            var applications = await _applicationService.GetMyApplicationsAsync(UserId);
            return Ok(ApiResponse<IEnumerable<ApplicationDto>>.Success(applications));
        }

        [HttpGet("{id}")]
        public async Task<ActionResult<ApiResponse<ApplicationDto>>> GetApplication(int id)
        {
            var application = await _applicationService.GetApplicationByIdAsync(id, UserId);
            if (application == null) return NotFound(ApiResponse<ApplicationDto>.Failure("Application not found."));
            return Ok(ApiResponse<ApplicationDto>.Success(application));
        }

        [HttpGet("{id}/history")]
        public async Task<ActionResult<ApiResponse<IEnumerable<ApplicationHistoryDto>>>> GetHistory(int id)
        {
            var history = await _applicationService.GetApplicationHistoryAsync(id, UserId);
            if (history == null) return NotFound(ApiResponse<IEnumerable<ApplicationHistoryDto>>.Failure("Application not found."));
            return Ok(ApiResponse<IEnumerable<ApplicationHistoryDto>>.Success(history));
        }

        [HttpPatch("{id}/withdraw")]
        public async Task<ActionResult<ApiResponse<bool>>> Withdraw(int id)
        {
            try
            {
                var result = await _applicationService.WithdrawApplicationAsync(id, UserId);
                if (!result) return NotFound(ApiResponse<bool>.Failure("Application not found."));
                return Ok(ApiResponse<bool>.Success(true, "Application withdrawn successfully."));
            }
            catch (System.Exception ex)
            {
                return BadRequest(ApiResponse<bool>.Failure(ex.Message));
            }
        }

        // Recruiter endpoints (in a real app, these might be in RecruiterApplicationsController)
        [HttpGet("job/{jobId}")]
        [Authorize(Roles = "Recruiter")]
        public async Task<ActionResult<ApiResponse<IEnumerable<ApplicationDto>>>> GetJobApplications(int jobId)
        {
            var applications = await _applicationService.GetApplicationsByJobAsync(jobId, UserId);
            return Ok(ApiResponse<IEnumerable<ApplicationDto>>.Success(applications));
        }

        [HttpPatch("{id}/status")]
        [Authorize(Roles = "Recruiter")]
        public async Task<ActionResult<ApiResponse<bool>>> UpdateStatus(int id, UpdateApplicationStatusRequest request)
        {
            var result = await _applicationService.UpdateApplicationStatusAsync(id, request, UserId);
            if (!result) return NotFound(ApiResponse<bool>.Failure("Application not found."));
            return Ok(ApiResponse<bool>.Success(true, "Application status updated."));
        }
    }
}
