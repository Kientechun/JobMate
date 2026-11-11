using JobMate.Domain.Common;
using JobMate.Domain.Enums;

namespace JobMate.Domain.Entities
{
    public class ApplicationHistory : BaseEntity
    {
        public int JobApplicationId { get; set; }
        public virtual JobApplication JobApplication { get; set; }
        public ApplicationStatus Status { get; set; }
        public string Note { get; set; }
    }
}
