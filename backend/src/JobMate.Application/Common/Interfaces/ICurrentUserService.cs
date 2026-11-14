namespace JobMate.Application.Common.Interfaces
{
    public interface ICurrentUserService
    {
        string? UserId { get; }
        int? CompanyId { get; }
        bool IsInRole(string role);
    }
}
