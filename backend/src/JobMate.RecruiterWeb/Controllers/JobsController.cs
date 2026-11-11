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
            var userId = _userManager.GetUserId(User);
            // Filtering by CompanyId is needed here.
            // Mocking company context for now.
            var jobs = await _context.Jobs
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
            if (ModelState.IsValid)
            {
                job.CreatedAt = DateTime.UtcNow;
                job.Status = JobStatus.Pending; // Send for approval by default or Draft

                // Set CompanyId from Recruiter context
                // job.CompanyId = ...

                _context.Add(job);
                await _context.SaveChangesAsync();
                return RedirectToAction(nameof(Index));
            }
            return View(job);
        }

        public async Task<IActionResult> Edit(int id)
        {
            var job = await _context.Jobs.FindAsync(id);
            if (job == null) return NotFound();

            // Check ownership

            return View(job);
        }

        [HttpPost]
        [ValidateAntiForgeryToken]
        public async Task<IActionResult> Edit(int id, Job job)
        {
            if (id != job.Id) return NotFound();

            if (ModelState.IsValid)
            {
                try
                {
                    var existingJob = await _context.Jobs.FindAsync(id);
                    if (existingJob == null) return NotFound();

                    // Check ownership

                    existingJob.Title = job.Title;
                    existingJob.Description = job.Description;
                    existingJob.Location = job.Location;
                    existingJob.SalaryRange = job.SalaryRange;
                    existingJob.JobType = job.JobType;
                    existingJob.Experience = job.Experience;
                    existingJob.ExpiryDate = job.ExpiryDate;
                    existingJob.Status = job.Status;

                    _context.Update(existingJob);
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
            var job = await _context.Jobs.FindAsync(id);
            if (job != null)
            {
                // Check ownership
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
