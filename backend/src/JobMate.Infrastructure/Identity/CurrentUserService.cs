using System.Security.Claims;
using JobMate.Application.Common.Interfaces;
using Microsoft.AspNetCore.Http;

namespace JobMate.Infrastructure.Identity
{
    public class CurrentUserService : ICurrentUserService
    {
        private readonly IHttpContextAccessor _httpContextAccessor;

        public CurrentUserService(IHttpContextAccessor httpContextAccessor)
        {
            _httpContextAccessor = httpContextAccessor;
        }

        public string? UserId => _httpContextAccessor.HttpContext?.User?.FindFirstValue(ClaimTypes.NameIdentifier);

        public int? CompanyId
        {
            get
            {
                var companyIdStr = _httpContextAccessor.HttpContext?.User?.FindFirstValue("CompanyId");
                return int.TryParse(companyIdStr, out var companyId) ? companyId : null;
            }
        }

        public bool IsInRole(string role) => _httpContextAccessor.HttpContext?.User?.IsInRole(role) ?? false;
    }
}
