using System.Collections.Generic;

namespace JobMate.Application.Common.Interfaces
{
    public interface IJwtTokenGenerator
    {
        string GenerateToken(string userId, string userName, IEnumerable<string> roles);
        string GenerateRefreshToken();
    }
}
