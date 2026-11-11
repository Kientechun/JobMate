using JobMate.Domain.Common;

namespace JobMate.Domain.Entities
{
    public class ResumeAnalysis : BaseEntity
    {
        public int ResumeId { get; set; }
        public virtual Resume Resume { get; set; } = null!;

        public string Overview { get; set; } = string.Empty;
        public string StrengthsJson { get; set; } = "[]";
        public string ImprovementsJson { get; set; } = "[]";
        public string HighlightSkillsJson { get; set; } = "[]";
        public string MissingSkillsJson { get; set; } = "[]";
        public string SuggestionsJson { get; set; } = "[]";
        public string SuitableRolesJson { get; set; } = "[]";

        public string UserId { get; set; } = string.Empty;
    }
}
