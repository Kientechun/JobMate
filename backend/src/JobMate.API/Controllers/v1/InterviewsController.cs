using System;
using System.Collections.Generic;
using System.Linq;
using System.Security.Claims;
using System.Threading.Tasks;
using JobMate.Application.Common.Interfaces;
using JobMate.Application.DTOs.Interviews;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;

namespace JobMate.Api.Controllers.v1
{
    [Authorize]
    [ApiController]
    [Route("api/interviews")]
    public class InterviewsController : ControllerBase
    {
        private readonly IInterviewService _interviewService;

        public InterviewsController(IInterviewService interviewService)
        {
            _interviewService = interviewService;
        }

        [HttpGet]
        public async Task<ActionResult<IEnumerable<InterviewDto>>> GetMyInterviews()
        {
            var userId = User.FindFirstValue(ClaimTypes.NameIdentifier);
            if (string.IsNullOrEmpty(userId)) return Unauthorized();

            var interviews = await _interviewService.GetUserInterviewsAsync(userId);
            return Ok(interviews.Select(i => new InterviewDto
            {
                Id = i.Id,
                JobApplicationId = i.JobApplicationId,
                JobTitle = i.JobApplication.Job.Title,
                CompanyName = i.JobApplication.Job.CompanyName,
                ScheduledAt = i.ScheduledAt,
                Location = i.Location,
                Type = i.Type,
                Notes = i.Notes,
                Status = i.Status,
                Result = i.Result
            }));
        }

        [HttpGet("{id}")]
        public async Task<ActionResult<InterviewDto>> GetInterview(int id)
        {
            var interview = await _interviewService.GetInterviewByIdAsync(id);
            if (interview == null) return NotFound();

            // Security check: Only candidate or recruiter of the job can see it
            var userId = User.FindFirstValue(ClaimTypes.NameIdentifier);
            // Add proper role/ownership check here if needed

            return Ok(new InterviewDto
            {
                Id = interview.Id,
                JobApplicationId = interview.JobApplicationId,
                JobTitle = interview.JobApplication.Job.Title,
                CompanyName = interview.JobApplication.Job.CompanyName,
                ScheduledAt = interview.ScheduledAt,
                Location = interview.Location,
                Type = interview.Type,
                Notes = interview.Notes,
                Status = interview.Status,
                Result = interview.Result
            });
        }

        [Authorize(Roles = "Recruiter")]
        [HttpPost]
        public async Task<ActionResult<InterviewDto>> ScheduleInterview([FromBody] ScheduleInterviewRequest request)
        {
            var interview = await _interviewService.ScheduleInterviewAsync(
                request.JobApplicationId,
                request.ScheduledAt,
                request.Location,
                request.Type,
                request.Notes
            );

            return CreatedAtAction(nameof(GetInterview), new { id = interview.Id }, new InterviewDto
            {
                Id = interview.Id,
                JobApplicationId = interview.JobApplicationId,
                ScheduledAt = interview.ScheduledAt,
                Location = interview.Location,
                Type = interview.Type,
                Notes = interview.Notes,
                Status = interview.Status
            });
        }

        [Authorize(Roles = "Recruiter")]
        [HttpPut("{id}")]
        public async Task<IActionResult> UpdateInterview(int id, [FromBody] UpdateInterviewRequest request)
        {
            await _interviewService.UpdateInterviewAsync(
                id,
                request.ScheduledAt,
                request.Location,
                request.Type,
                request.Notes,
                request.Status
            );
            return NoContent();
        }

        [Authorize(Roles = "Recruiter")]
        [HttpPatch("{id}/result")]
        public async Task<IActionResult> UpdateResult(int id, [FromBody] UpdateInterviewResultRequest request)
        {
            await _interviewService.UpdateInterviewResultAsync(id, request.Result);
            return NoContent();
        }

        [Authorize(Roles = "Recruiter")]
        [HttpDelete("{id}")]
        public async Task<IActionResult> CancelInterview(int id)
        {
            await _interviewService.CancelInterviewAsync(id);
            return NoContent();
        }
    }

    public class ScheduleInterviewRequest
    {
        public int JobApplicationId { get; set; }
        public DateTime ScheduledAt { get; set; }
        public string Location { get; set; } = string.Empty;
        public string Type { get; set; } = string.Empty;
        public string Notes { get; set; } = string.Empty;
    }

    public class UpdateInterviewRequest
    {
        public DateTime ScheduledAt { get; set; }
        public string Location { get; set; } = string.Empty;
        public string Type { get; set; } = string.Empty;
        public string Notes { get; set; } = string.Empty;
        public string Status { get; set; } = string.Empty;
    }

    public class UpdateInterviewResultRequest
    {
        public string Result { get; set; } = string.Empty;
    }
}
