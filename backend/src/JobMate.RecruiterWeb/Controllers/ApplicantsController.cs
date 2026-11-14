using JobMate.Domain.Enums;
using JobMate.Infrastructure.Persistence;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using JobMate.Infrastructure.Identity;
using Microsoft.AspNetCore.Identity;

namespace JobMate.RecruiterWeb.Controllers
{
    [Authorize(Policy = "RecruiterOnly")]
    public class ApplicantsController : Controller
    {
        private readonly JobMateDbContext _context;
        private readonly UserManager<ApplicationUser> _userManager;

        public ApplicantsController(JobMateDbContext context, UserManager<ApplicationUser> userManager)
        {
            _context = context;
            _userManager = userManager;
        }

        public async Task<IActionResult> Index(int? jobId, ApplicationStatus? status)
        {
            var user = await _userManager.GetUserAsync(User);
            if (user?.CompanyId == null) return Forbid();

            var query = _context.JobApplications
                .Include(a => a.Job)
                .Include(a => a.Resume)
                .Where(a => a.Job.CompanyId == user.CompanyId)
                .AsQueryable();

            if (jobId.HasValue)
                query = query.Where(a => a.JobId == jobId.Value);

            if (status.HasValue)
                query = query.Where(a => a.Status == status.Value);

            var applications = await query
                .OrderByDescending(a => a.CreatedAt)
                .ToListAsync();

            return View(applications);
        }

        [HttpPost]
        [ValidateAntiForgeryToken]
        public async Task<IActionResult> UpdateStatus(int id, ApplicationStatus status, string notes)
        {
            var user = await _userManager.GetUserAsync(User);
            var application = await _context.JobApplications
                .Include(a => a.Job)
                .FirstOrDefaultAsync(a => a.Id == id);

            if (application == null) return NotFound();
            if (user?.CompanyId == null || application.Job.CompanyId != user.CompanyId) return Forbid();

            application.Status = status;
            await _context.SaveChangesAsync();

            return RedirectToAction(nameof(Index), new { jobId = application.JobId });
        }

        public async Task<IActionResult> Details(int id)
        {
            var user = await _userManager.GetUserAsync(User);
            var application = await _context.JobApplications
                .Include(a => a.Job)
                .Include(a => a.Resume)
                .FirstOrDefaultAsync(a => a.Id == id);

            if (application == null) return NotFound();
            if (user?.CompanyId == null || application.Job.CompanyId != user.CompanyId) return Forbid();

            return View(application);
        }
    }
}
