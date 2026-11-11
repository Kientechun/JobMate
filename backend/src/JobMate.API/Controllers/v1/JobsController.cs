using System.Threading.Tasks;
using JobMate.Application.Common.Interfaces;
using JobMate.Application.Common.Models;
using JobMate.Application.DTOs.Jobs;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;

namespace JobMate.API.Controllers.v1
{
    [ApiController]
    [Route("api/v1/[controller]")]
    public class JobsController : ControllerBase
    {
        private readonly IJobService _jobService;
        private readonly ICurrentUserService _currentUserService;

        public JobsController(IJobService jobService, ICurrentUserService currentUserService)
        {
            _jobService = jobService;
            _currentUserService = currentUserService;
        }

        [HttpGet]
        public async Task<ActionResult<ApiResponse<PaginatedList<JobDto>>>> GetJobs([FromQuery] JobSearchFilters filters, [FromQuery] int pageIndex = 1, [FromQuery] int pageSize = 10)
        {
            var result = await _jobService.GetJobsAsync(filters, pageIndex, pageSize);
            return Ok(ApiResponse<PaginatedList<JobDto>>.Success(result));
        }

        [HttpGet("{id}")]
        public async Task<ActionResult<ApiResponse<JobDto>>> GetJob(int id)
        {
            var job = await _jobService.GetJobByIdAsync(id);
            if (job == null) return NotFound(ApiResponse<JobDto>.Failure("Job not found."));
            return Ok(ApiResponse<JobDto>.Success(job));
        }

        [HttpGet("featured")]
        public async Task<ActionResult<ApiResponse<IEnumerable<JobDto>>>> GetFeatured([FromQuery] int count = 5)
        {
            var result = await _jobService.GetFeaturedJobsAsync(count);
            return Ok(ApiResponse<IEnumerable<JobDto>>.Success(result));
        }

        [HttpGet("categories")]
        public async Task<ActionResult<ApiResponse<IEnumerable<CategoryDto>>>> GetCategories()
        {
            var result = await _jobService.GetCategoriesAsync();
            return Ok(ApiResponse<IEnumerable<CategoryDto>>.Success(result));
        }

        [Authorize]
        [HttpGet("saved")]
        public async Task<ActionResult<ApiResponse<PaginatedList<JobDto>>>> GetSavedJobs([FromQuery] int pageIndex = 1, [FromQuery] int pageSize = 10)
        {
            var userId = _currentUserService.UserId;
            if (string.IsNullOrEmpty(userId)) return Unauthorized();

            var result = await _jobService.GetSavedJobsAsync(userId, pageIndex, pageSize);
            return Ok(ApiResponse<PaginatedList<JobDto>>.Success(result));
        }

        [Authorize]
        [HttpPost("{id}/save")]
        public async Task<ActionResult<ApiResponse<bool>>> SaveJob(int id)
        {
            var userId = _currentUserService.UserId;
            if (string.IsNullOrEmpty(userId)) return Unauthorized();

            var result = await _jobService.SaveJobAsync(id, userId);
            return Ok(result);
        }

        [Authorize]
        [HttpDelete("{id}/save")]
        public async Task<ActionResult<ApiResponse<bool>>> UnsaveJob(int id)
        {
            var userId = _currentUserService.UserId;
            if (string.IsNullOrEmpty(userId)) return Unauthorized();

            var result = await _jobService.UnsaveJobAsync(id, userId);
            return Ok(result);
        }
    }
}
