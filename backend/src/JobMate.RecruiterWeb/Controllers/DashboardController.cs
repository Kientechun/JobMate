using JobMate.Infrastructure.Persistence;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using JobMate.Domain.Enums;
using System.Security.Claims;
using JobMate.Infrastructure.Identity;
using Microsoft.AspNetCore.Identity;

namespace JobMate.RecruiterWeb.Controllers
{
    [Authorize(Policy = "RecruiterOnly")]
    public class DashboardController : Controller
    {
        private readonly JobMateDbContext _context;
        private readonly UserManager<ApplicationUser> _userManager;

        public DashboardController(JobMateDbContext context, UserManager<ApplicationUser> userManager)
        {
            _context = context;
            _userManager = userManager;
        }

        public async Task<IActionResult> Index()
        {
            var userId = _userManager.GetUserId(User);
            // In a real scenario, we'd link Recruiter to a Company.
            // For now, let's assume we filter by CompanyId if it's stored in the user or related table.
            // Assuming Job.CompanyId matches some identifier for the Recruiter's company.

            // This is a simplified version.
            var jobs = _context.Jobs.AsQueryable(); // Filter by company logic here

            var stats = new DashboardViewModel
            {
                TotalJobs = await jobs.CountAsync(),
                ActiveJobs = await jobs.CountAsync(j => j.Status == JobStatus.Published),
                PendingJobs = await jobs.CountAsync(j => j.Status == JobStatus.Pending),
                TotalApplications = await _context.JobApplications.CountAsync(a => jobs.Any(j => j.Id == a.JobId)),
                NewApplications = await _context.JobApplications.CountAsync(a => jobs.Any(j => j.Id == a.JobId) && a.Status == ApplicationStatus.Pending)
            };

            return View(stats);
        }
    }

    public class DashboardViewModel
    {
        public int TotalJobs { get; set; }
        public int ActiveJobs { get; set; }
        public int PendingJobs { get; set; }
        public int TotalApplications { get; set; }
        public int NewApplications { get; set; }
    }
}
