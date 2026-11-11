using JobMate.Domain.Entities;
using Microsoft.EntityFrameworkCore;
using Microsoft.EntityFrameworkCore.Metadata.Builders;

namespace JobMate.Infrastructure.Persistence.Configurations
{
    public class JobConfiguration : IEntityTypeConfiguration<Job>
    {
        public void Configure(EntityTypeBuilder<Job> builder)
        {
            builder.HasKey(j => j.Id);
            builder.Property(j => j.Title).IsRequired().HasMaxLength(200);
            builder.Property(j => j.CompanyName).IsRequired().HasMaxLength(200);
            builder.Property(j => j.SalaryRange).HasColumnType("decimal(18,2)");
        }
    }
}
