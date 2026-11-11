using JobMate.Infrastructure.Identity;
using JobMate.Infrastructure.Persistence;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Identity;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;

namespace JobMate.AdminWeb.Controllers
{
    [Authorize(Policy = "AdminOnly")]
    public class UsersController : Controller
    {
        private readonly UserManager<ApplicationUser> _userManager;

        public UsersController(UserManager<ApplicationUser> userManager)
        {
            _userManager = userManager;
        }

        public async Task<IActionResult> Index()
        {
            var users = await _userManager.Users.ToListAsync();
            return View(users);
        }

        [HttpPost]
        public async Task<IActionResult> Lock(string id)
        {
            var user = await _userManager.FindByIdAsync(id);
            if (user != null)
            {
                await _userManager.SetLockoutEndDateAsync(user, DateTimeOffset.MaxValue);
            }
            return RedirectToAction(nameof(Index));
        }

        [HttpPost]
        public async Task<IActionResult> Unlock(string id)
        {
            var user = await _userManager.FindByIdAsync(id);
            if (user != null)
            {
                await _userManager.SetLockoutEndDateAsync(user, null);
            }
            return RedirectToAction(nameof(Index));
        }
    }
}
