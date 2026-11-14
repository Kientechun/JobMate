using JobMate.Domain.Entities;
using JobMate.Domain.Enums;
using JobMate.Infrastructure.Persistence;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using System.Security.Claims;
using JobMate.Infrastructure.Identity;
using Microsoft.AspNetCore.Identity;

namespace JobMate.RecruiterWeb.Controllers
{
    [Authorize(Policy = "RecruiterOnly")]
    public class JobsController : Controller
    {
        private readonly JobMateDbContext _context;
        private readonly UserManager<ApplicationUser> _userManager;

        public JobsController(JobMateDbContext context, UserManager<ApplicationUser> userManager)
        {
            _context = context;
            _userManager = userManager;
        }

        public async Task<IActionResult> Index()
        {
            var user = await _userManager.GetUserAsync(User);
            if (user?.CompanyId == null)
            {
                return RedirectToAction("Index", "Dashboard");
            }

            var jobs = await _context.Jobs
                .Where(j => j.CompanyId == user.CompanyId)
                .Include(j => j.Category)
                .Include(j => j.Applications)
                .OrderByDescending(j => j.CreatedAt)
                .ToListAsync();

            return View(jobs);
        }

        public IActionResult Create()
        {
            return View();
        }

        [HttpPost]
        [ValidateAntiForgeryToken]
        public async Task<IActionResult> Create(Job job)
        {
            var user = await _userManager.GetUserAsync(User);
            if (user?.CompanyId == null) return Unauthorized();

            if (ModelState.IsValid)
            {
                job.CreatedAt = DateTime.UtcNow;
                job.Status = JobStatus.Pending;
                job.CompanyId = user.CompanyId.Value;

                _context.Add(job);
                await _context.SaveChangesAsync();
                return RedirectToAction(nameof(Index));
            }
            return View(job);
        }

        public async Task<IActionResult> Edit(int id)
        {
            var user = await _userManager.GetUserAsync(User);
            var job = await _context.Jobs.FindAsync(id);

            if (job == null) return NotFound();
            if (user?.CompanyId == null || job.CompanyId != user.CompanyId) return Forbid();

            return View(job);
        }

        [HttpPost]
        [ValidateAntiForgeryToken]
        public async Task<IActionResult> Edit(int id, Job job)
        {
            if (id != job.Id) return NotFound();

            var user = await _userManager.GetUserAsync(User);
            var existingJob = await _context.Jobs.AsNoTracking().FirstOrDefaultAsync(j => j.Id == id);

            if (existingJob == null) return NotFound();
            if (user?.CompanyId == null || existingJob.CompanyId != user.CompanyId) return Forbid();

            if (ModelState.IsValid)
            {
                try
                {
                    job.CompanyId = user.CompanyId.Value;
                    job.CreatedAt = existingJob.CreatedAt; // Preserve original creation date

                    _context.Update(job);
                    await _context.SaveChangesAsync();
                }
                catch (DbUpdateConcurrencyException)
                {
                    if (!JobExists(job.Id)) return NotFound();
                    else throw;
                }
                return RedirectToAction(nameof(Index));
            }
            return View(job);
        }

        [HttpPost]
        [ValidateAntiForgeryToken]
        public async Task<IActionResult> Delete(int id)
        {
            var user = await _userManager.GetUserAsync(User);
            var job = await _context.Jobs.FindAsync(id);

            if (job != null)
            {
                if (user?.CompanyId == null || job.CompanyId != user.CompanyId) return Forbid();

                _context.Jobs.Remove(job);
                await _context.SaveChangesAsync();
            }
            return RedirectToAction(nameof(Index));
        }

        private bool JobExists(int id)
        {
            return _context.Jobs.Any(e => e.Id == id);
        }
    }
}
