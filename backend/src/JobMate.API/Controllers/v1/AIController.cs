using JobMate.Application.Common.Interfaces;
using JobMate.Domain.Entities;
using JobMate.Domain.Enums;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using System.Text.Json;

namespace JobMate.API.Controllers.v1
{
    [Authorize]
    [ApiController]
    [Route("api/v1/ai")]
    public class AIController : ControllerBase
    {
        private readonly IAIService _aiService;
        private readonly IJobMateDbContext _context;
        private readonly ICurrentUserService _currentUserService;

        public AIController(IAIService aiService, IJobMateDbContext context, ICurrentUserService currentUserService)
        {
            _aiService = aiService;
            _context = context;
            _currentUserService = currentUserService;
        }

        [HttpPost("resumes/{id}/analyze")]
        public async Task<IActionResult> AnalyzeResume(int id)
        {
            var resume = await _context.Resumes
                .Include(r => r.Experiences)
                .Include(r => r.Educations)
                .Include(r => r.Skills)
                .FirstOrDefaultAsync(r => r.Id == id);

            if (resume == null) return NotFound();
            if (resume.UserId != _currentUserService.UserId) return Forbid();

            var analysisResult = await _aiService.AnalyzeResumeAsync(resume);

            // Save or Update analysis in DB
            var existingAnalysis = await _context.ResumeAnalyses.FirstOrDefaultAsync(a => a.ResumeId == id);
            if (existingAnalysis == null)
            {
                existingAnalysis = new ResumeAnalysis
                {
                    ResumeId = id,
                    UserId = _currentUserService.UserId!,
                    CreatedAt = DateTime.UtcNow
                };
                _context.ResumeAnalyses.Add(existingAnalysis);
            }

            existingAnalysis.Overview = analysisResult.Overview;
            existingAnalysis.StrengthsJson = JsonSerializer.Serialize(analysisResult.Strengths);
            existingAnalysis.ImprovementsJson = JsonSerializer.Serialize(analysisResult.Improvements);
            existingAnalysis.HighlightSkillsJson = JsonSerializer.Serialize(analysisResult.HighlightSkills);
            existingAnalysis.MissingSkillsJson = JsonSerializer.Serialize(analysisResult.MissingSkills);
            existingAnalysis.SuggestionsJson = JsonSerializer.Serialize(analysisResult.Suggestions);
            existingAnalysis.SuitableRolesJson = JsonSerializer.Serialize(analysisResult.SuitableRoles);
            existingAnalysis.LastModifiedAt = DateTime.UtcNow;

            await _context.SaveChangesAsync();

            return Ok(analysisResult);
        }

        [HttpGet("resumes/{id}/analysis")]
        public async Task<IActionResult> GetResumeAnalysis(int id)
        {
            var analysis = await _context.ResumeAnalyses.FirstOrDefaultAsync(a => a.ResumeId == id);
            if (analysis == null) return NotFound("No analysis found for this resume. Please trigger analysis first.");
            if (analysis.UserId != _currentUserService.UserId) return Forbid();

            var dto = new JobMate.Application.DTOs.AI.ResumeAnalysisDto
            {
                Overview = analysis.Overview,
                Strengths = JsonSerializer.Deserialize<List<string>>(analysis.StrengthsJson) ?? new(),
                Improvements = JsonSerializer.Deserialize<List<string>>(analysis.ImprovementsJson) ?? new(),
                HighlightSkills = JsonSerializer.Deserialize<List<string>>(analysis.HighlightSkillsJson) ?? new(),
                MissingSkills = JsonSerializer.Deserialize<List<string>>(analysis.MissingSkillsJson) ?? new(),
                Suggestions = JsonSerializer.Deserialize<List<string>>(analysis.SuggestionsJson) ?? new(),
                SuitableRoles = JsonSerializer.Deserialize<List<string>>(analysis.SuitableRolesJson) ?? new()
            };

            return Ok(dto);
        }

        [HttpGet("jobs/recommendations")]
        public async Task<IActionResult> GetRecommendations([FromQuery] int? resumeId)
        {
            Resume? resume;
            if (resumeId.HasValue)
            {
                resume = await _context.Resumes
                    .Include(r => r.Experiences)
                    .Include(r => r.Educations)
                    .Include(r => r.Skills)
                    .FirstOrDefaultAsync(r => r.Id == resumeId.Value);
            }
            else
            {
                resume = await _context.Resumes
                    .Include(r => r.Experiences)
                    .Include(r => r.Educations)
                    .Include(r => r.Skills)
                    .FirstOrDefaultAsync(r => r.UserId == _currentUserService.UserId && r.IsDefault);
            }

            if (resume == null) return BadRequest("Please provide a resume ID or set a default resume.");
            if (resume.UserId != _currentUserService.UserId) return Forbid();

            var availableJobs = await _context.Jobs
                .Where(j => j.Status == JobStatus.Published && j.ExpiryDate > DateTime.UtcNow)
                .OrderByDescending(j => j.CreatedAt)
                .Take(50) // Candidate jobs for AI to rank
                .ToListAsync();

            var recommendations = await _aiService.GetJobRecommendationsAsync(resume, availableJobs);

            return Ok(recommendations);
        }
    }
}
