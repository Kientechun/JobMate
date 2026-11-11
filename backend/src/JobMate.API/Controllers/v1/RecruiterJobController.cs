using System.Threading.Tasks;
using JobMate.Application.Common.Interfaces;
using JobMate.Application.Common.Models;
using JobMate.Application.DTOs.Jobs;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;

namespace JobMate.API.Controllers.v1
{
    [Authorize(Roles = "Recruiter,Admin")]
    [ApiController]
    [Route("api/v1/recruiter/jobs")]
    public class RecruiterJobController : ControllerBase
    {
        private readonly IJobService _jobService;
        private readonly ICurrentUserService _currentUserService;

        public RecruiterJobController(IJobService jobService, ICurrentUserService currentUserService)
        {
            _jobService = jobService;
            _currentUserService = currentUserService;
        }

        [HttpGet]
        public async Task<ActionResult<ApiResponse<PaginatedList<JobDto>>>> GetRecruiterJobs([FromQuery] string companyId, [FromQuery] int pageIndex = 1, [FromQuery] int pageSize = 10)
        {
            // Note: In a real app, we should verify that the current user belongs to the companyId.
            var result = await _jobService.GetRecruiterJobsAsync(companyId, pageIndex, pageSize);
            return Ok(ApiResponse<PaginatedList<JobDto>>.Success(result));
        }

        [HttpPost]
        public async Task<ActionResult<ApiResponse<int>>> CreateJob([FromBody] CreateJobRequest request)
        {
            var userId = _currentUserService.UserId;
            if (string.IsNullOrEmpty(userId)) return Unauthorized();

            var result = await _jobService.CreateJobAsync(request, userId);
            return Ok(result);
        }

        [HttpGet("{id}")]
        public async Task<ActionResult<ApiResponse<JobDto>>> GetJob(int id, [FromQuery] string companyId)
        {
            var job = await _jobService.GetRecruiterJobByIdAsync(id, companyId);
            if (job == null) return NotFound(ApiResponse<JobDto>.Failure("Job not found or access denied."));
            return Ok(ApiResponse<JobDto>.Success(job));
        }

        [HttpPut("{id}")]
        public async Task<ActionResult<ApiResponse<bool>>> UpdateJob(int id, [FromBody] UpdateJobRequest request, [FromQuery] string companyId)
        {
            var userId = _currentUserService.UserId;
            if (string.IsNullOrEmpty(userId)) return Unauthorized();

            var result = await _jobService.UpdateJobAsync(id, request, userId, companyId);
            return Ok(result);
        }

        [HttpPatch("{id}/close")]
        public async Task<ActionResult<ApiResponse<bool>>> CloseJob(int id, [FromQuery] string companyId)
        {
            var userId = _currentUserService.UserId;
            if (string.IsNullOrEmpty(userId)) return Unauthorized();

            var result = await _jobService.CloseJobAsync(id, userId, companyId);
            return Ok(result);
        }
    }
}
