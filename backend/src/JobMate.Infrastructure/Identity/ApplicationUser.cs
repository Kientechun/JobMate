using System.Collections.Generic;
using JobMate.Domain.Entities;
using Microsoft.AspNetCore.Identity;

namespace JobMate.Infrastructure.Identity
{
    public class ApplicationUser : IdentityUser
    {
        public string FullName { get; set; } = string.Empty;
        public int? CompanyId { get; set; }
        public virtual Company? Company { get; set; }
        public List<RefreshToken> RefreshTokens { get; set; } = new();
    }
}
