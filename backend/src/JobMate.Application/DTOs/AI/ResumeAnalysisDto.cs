namespace JobMate.Application.DTOs.AI
{
    public class ResumeAnalysisDto
    {
        public string Overview { get; set; } = string.Empty;
        public List<string> Strengths { get; set; } = new List<string>();
        public List<string> Improvements { get; set; } = new List<string>();
        public List<string> HighlightSkills { get; set; } = new List<string>();
        public List<string> MissingSkills { get; set; } = new List<string>();
        public List<string> Suggestions { get; set; } = new List<string>();
        public List<string> SuitableRoles { get; set; } = new List<string>();
    }
}
