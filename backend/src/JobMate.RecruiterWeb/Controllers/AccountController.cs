using JobMate.Infrastructure.Identity;
using Microsoft.AspNetCore.Authentication;
using Microsoft.AspNetCore.Authentication.Cookies;
using Microsoft.AspNetCore.Identity;
using Microsoft.AspNetCore.Mvc;
using System.Security.Claims;

namespace JobMate.RecruiterWeb.Controllers
{
    public class AccountController : Controller
    {
        private readonly SignInManager<ApplicationUser> _signInManager;
        private readonly UserManager<ApplicationUser> _userManager;

        public AccountController(SignInManager<ApplicationUser> signInManager, UserManager<ApplicationUser> userManager)
        {
            _signInManager = signInManager;
            _userManager = userManager;
        }

        [HttpGet]
        public IActionResult Login(string? returnUrl = null)
        {
            ViewData["ReturnUrl"] = returnUrl;
            return View();
        }

        [HttpPost]
        [ValidateAntiForgeryToken]
        public async Task<IActionResult> Login(string email, string password, bool rememberMe, string? returnUrl = null)
        {
            ViewData["ReturnUrl"] = returnUrl;
            var result = await _signInManager.PasswordSignInAsync(email, password, rememberMe, lockoutOnFailure: false);
            if (result.Succeeded)
            {
                var user = await _userManager.FindByEmailAsync(email);
                var roles = await _userManager.GetRolesAsync(user!);

                if (!roles.Contains("Recruiter"))
                {
                    await _signInManager.SignOutAsync();
                    ModelState.AddModelError(string.Empty, "Only recruiters are allowed to access this area.");
                    return View();
                }

                return RedirectToLocal(returnUrl);
            }

            ModelState.AddModelError(string.Empty, "Invalid login attempt.");
            return View();
        }

        [HttpPost]
        [ValidateAntiForgeryToken]
        public async Task<IActionResult> Logout()
        {
            await _signInManager.SignOutAsync();
            return RedirectToAction("Login", "Account");
        }

        [HttpGet]
        public IActionResult AccessDenied()
        {
            return View();
        }

        private IActionResult RedirectToLocal(string? returnUrl)
        {
            if (Url.IsLocalUrl(returnUrl))
            {
                return Redirect(returnUrl);
            }
            else
            {
                return RedirectToAction(nameof(DashboardController.Index), "Dashboard");
            }
        }
    }
}
