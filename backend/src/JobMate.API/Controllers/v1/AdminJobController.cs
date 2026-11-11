using System.Threading.Tasks;
using JobMate.Application.Common.Interfaces;
using JobMate.Application.Common.Models;
using JobMate.Application.DTOs.Jobs;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;

namespace JobMate.API.Controllers.v1
{
    [Authorize(Roles = "Admin")]
    [ApiController]
    [Route("api/v1/admin/jobs")]
    public class AdminJobController : ControllerBase
    {
        private readonly IJobService _jobService;

        public AdminJobController(IJobService jobService)
        {
            _jobService = jobService;
        }

        [HttpGet("pending")]
        public async Task<ActionResult<ApiResponse<PaginatedList<JobDto>>>> GetPendingJobs([FromQuery] int pageIndex = 1, [FromQuery] int pageSize = 10)
        {
            var result = await _jobService.GetPendingJobsAsync(pageIndex, pageSize);
            return Ok(ApiResponse<PaginatedList<JobDto>>.Success(result));
        }

        [HttpPatch("{id}/approve")]
        public async Task<ActionResult<ApiResponse<bool>>> ApproveJob(int id)
        {
            var result = await _jobService.ApproveJobAsync(id);
            return Ok(result);
        }

        [HttpPatch("{id}/reject")]
        public async Task<ActionResult<ApiResponse<bool>>> RejectJob(int id)
        {
            var result = await _jobService.RejectJobAsync(id);
            return Ok(result);
        }
    }
}
