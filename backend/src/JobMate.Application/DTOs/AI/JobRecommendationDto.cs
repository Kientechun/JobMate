namespace JobMate.Application.DTOs.AI
{
    public class JobRecommendationDto
    {
        public int JobId { get; set; }
        public string JobTitle { get; set; } = string.Empty;
        public string CompanyName { get; set; } = string.Empty;
        public double MatchScore { get; set; }
        public string Reason { get; set; } = string.Empty;
    }
}
