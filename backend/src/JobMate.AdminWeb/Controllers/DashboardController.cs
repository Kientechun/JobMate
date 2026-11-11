using JobMate.Infrastructure.Persistence;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using JobMate.Domain.Enums;

namespace JobMate.AdminWeb.Controllers
{
    [Authorize(Policy = "AdminOnly")]
    public class DashboardController : Controller
    {
        private readonly JobMateDbContext _context;

        public DashboardController(JobMateDbContext context)
        {
            _context = context;
        }

        public async Task<IActionResult> Index()
        {
            var stats = new AdminDashboardViewModel
            {
                TotalCandidates = await _context.Users.CountAsync(), // Filter by role later
                TotalRecruiters = 0, // Placeholder
                TotalCompanies = 15, // Placeholder
                PendingJobs = await _context.Jobs.CountAsync(j => j.Status == JobStatus.Pending),
                TotalApplications = await _context.JobApplications.CountAsync(),
                TotalJobs = await _context.Jobs.CountAsync()
            };

            return View(stats);
        }
    }

    public class AdminDashboardViewModel
    {
        public int TotalCandidates { get; set; }
        public int TotalRecruiters { get; set; }
        public int TotalCompanies { get; set; }
        public int PendingJobs { get; set; }
        public int TotalApplications { get; set; }
        public int TotalJobs { get; set; }
    }
}
