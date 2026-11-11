using System.Net.Http.Json;
using System.Text;
using System.Text.Json;
using JobMate.Application.Common.Interfaces;
using JobMate.Application.DTOs.AI;
using JobMate.Domain.Entities;
using Microsoft.Extensions.Configuration;
using Microsoft.Extensions.Logging;

namespace JobMate.Infrastructure.Services
{
    public class AIService : IAIService
    {
        private readonly HttpClient _httpClient;
        private readonly string _apiKey;
        private readonly ILogger<AIService> _logger;

        public AIService(HttpClient httpClient, IConfiguration configuration, ILogger<AIService> logger)
        {
            _httpClient = httpClient;
            _apiKey = configuration["Gemini:ApiKey"] ?? throw new ArgumentNullException("Gemini:ApiKey not configured");
            _logger = logger;
        }

        public async Task<ResumeAnalysisDto> AnalyzeResumeAsync(Resume resume)
        {
            var resumeText = SerializeResumeForAI(resume);
            var prompt = $@"
                Analyze the following resume and provide a detailed report in JSON format.
                The JSON should have the following keys:
                - Overview: A brief summary of the profile.
                - Strengths: A list of key strengths.
                - Improvements: A list of areas for improvement.
                - HighlightSkills: A list of standout skills.
                - MissingSkills: A list of common skills for this profile that are missing.
                - Suggestions: Actionable tips to improve the CV.
                - SuitableRoles: A list of job titles this candidate is qualified for.

                Resume Content:
                {resumeText}
            ";

            var result = await CallGeminiAsync(prompt);
            try
            {
                // Simple cleanup if Gemini returns markdown code blocks
                var json = result.Trim();
                if (json.StartsWith("```json")) json = json.Substring(7);
                if (json.EndsWith("```")) json = json.Substring(0, json.Length - 3);

                return JsonSerializer.Deserialize<ResumeAnalysisDto>(json, new JsonSerializerOptions { PropertyNameCaseInsensitive = true })
                       ?? new ResumeAnalysisDto();
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "Failed to parse Gemini response for resume analysis");
                return new ResumeAnalysisDto { Overview = "Error analyzing resume. Please try again later." };
            }
        }

        public async Task<List<JobRecommendationDto>> GetJobRecommendationsAsync(Resume resume, List<Job> availableJobs)
        {
            var resumeText = SerializeResumeForAI(resume);
            var jobsText = new StringBuilder();
            foreach (var job in availableJobs.Take(10)) // Limit to top 10 for prompt size
            {
                jobsText.AppendLine($"ID: {job.Id}, Title: {job.Title}, Company: {job.CompanyName}, Description: {job.Description}");
            }

            var prompt = $@"
                Based on the candidate's resume, recommend the most suitable jobs from the provided list.
                Return a JSON array of objects, each with:
                - JobId: The ID of the job.
                - MatchScore: A score from 0 to 100.
                - Reason: A brief explanation of why this job fits.

                Candidate Resume:
                {resumeText}

                Available Jobs:
                {jobsText}
            ";

            var result = await CallGeminiAsync(prompt);
            try
            {
                var json = result.Trim();
                if (json.StartsWith("```json")) json = json.Substring(7);
                if (json.EndsWith("```")) json = json.Substring(0, json.Length - 3);

                var recommendations = JsonSerializer.Deserialize<List<JobRecommendationDto>>(json, new JsonSerializerOptions { PropertyNameCaseInsensitive = true })
                                     ?? new List<JobRecommendationDto>();

                // Enrich with job details
                foreach (var rec in recommendations)
                {
                    var job = availableJobs.FirstOrDefault(j => j.Id == rec.JobId);
                    if (job != null)
                    {
                        rec.JobTitle = job.Title;
                        rec.CompanyName = job.CompanyName;
                    }
                }

                return recommendations.OrderByDescending(r => r.MatchScore).ToList();
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "Failed to parse Gemini response for job recommendations");
                return new List<JobRecommendationDto>();
            }
        }

        private async Task<string> CallGeminiAsync(string prompt)
        {
            var url = $"https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key={_apiKey}";

            var requestBody = new
            {
                contents = new[]
                {
                    new { parts = new[] { new { text = prompt } } }
                },
                generationConfig = new
                {
                    response_mime_type = "application/json"
                }
            };

            var response = await _httpClient.PostAsJsonAsync(url, requestBody);
            response.EnsureSuccessStatusCode();

            var responseData = await response.Content.ReadFromJsonAsync<JsonElement>();
            return responseData.GetProperty("candidates")[0]
                               .GetProperty("content")
                               .GetProperty("parts")[0]
                               .GetProperty("text")
                               .GetString() ?? string.Empty;
        }

        private string SerializeResumeForAI(Resume resume)
        {
            var sb = new StringBuilder();
            sb.AppendLine($"Name: {resume.FullName}");
            sb.AppendLine($"Summary: {resume.Summary}");

            sb.AppendLine("Skills:");
            foreach (var s in resume.Skills) sb.AppendLine($"- {s.SkillName} ({s.Level})");

            sb.AppendLine("Experience:");
            foreach (var e in resume.Experiences) sb.AppendLine($"- {e.Position} at {e.CompanyName} ({e.StartDate} - {e.EndDate}): {e.Description}");

            sb.AppendLine("Education:");
            foreach (var ed in resume.Educations) sb.AppendLine($"- {ed.Degree} in {ed.FieldOfStudy} from {ed.SchoolName}");

            return sb.ToString();
        }
    }
}
