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
            var query = _context.JobApplications
                .Include(a => a.Job)
                .Include(a => a.Resume)
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
            var application = await _context.JobApplications.FindAsync(id);
            if (application == null) return NotFound();

            // Update status
            application.Status = status;

            // Add to history (assuming ApplicationHistory entity exists)
            // application.History.Add(new ApplicationHistory { ... });

            await _context.SaveChangesAsync();

            return RedirectToAction(nameof(Index), new { jobId = application.JobId });
        }

        public async Task<IActionResult> Details(int id)
        {
            var application = await _context.JobApplications
                .Include(a => a.Job)
                .Include(a => a.Resume)
                .FirstOrDefaultAsync(a => a.Id == id);

            if (application == null) return NotFound();

            return View(application);
        }
    }
}
