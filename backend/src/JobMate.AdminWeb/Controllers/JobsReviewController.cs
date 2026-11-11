using JobMate.Domain.Enums;
using JobMate.Infrastructure.Persistence;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;

namespace JobMate.AdminWeb.Controllers
{
    [Authorize(Policy = "AdminOnly")]
    public class JobsReviewController : Controller
    {
        private readonly JobMateDbContext _context;

        public JobsReviewController(JobMateDbContext context)
        {
            _context = context;
        }

        public async Task<IActionResult> Index()
        {
            var pendingJobs = await _context.Jobs
                .Include(j => j.Category)
                .Where(j => j.Status == JobStatus.Pending)
                .OrderByDescending(j => j.CreatedAt)
                .ToListAsync();

            return View(pendingJobs);
        }

        [HttpPost]
        [ValidateAntiForgeryToken]
        public async Task<IActionResult> Approve(int id)
        {
            var job = await _context.Jobs.FindAsync(id);
            if (job == null) return NotFound();

            job.Status = JobStatus.Published;
            await _context.SaveChangesAsync();

            return RedirectToAction(nameof(Index));
        }

        [HttpPost]
        [ValidateAntiForgeryToken]
        public async Task<IActionResult> Reject(int id, string reason)
        {
            var job = await _context.Jobs.FindAsync(id);
            if (job == null) return NotFound();

            job.Status = JobStatus.Rejected;
            // job.RejectionReason = reason; // Add if exists in entity
            await _context.SaveChangesAsync();

            return RedirectToAction(nameof(Index));
        }
    }
}
