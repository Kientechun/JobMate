using JobMate.Domain.Common;

namespace JobMate.Domain.Entities
{
    public class SavedJob : BaseEntity
    {
        public string UserId { get; set; } = string.Empty;
        public int JobId { get; set; }
        public virtual Job Job { get; set; } = null!;
    }
}
