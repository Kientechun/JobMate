using JobMate.Infrastructure.Identity;
using Microsoft.AspNetCore.Identity;
using Microsoft.AspNetCore.Mvc;

namespace JobMate.AdminWeb.Controllers
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
            var result = await _signInManager.PasswordSignInAsync(email, password, rememberMe, lockoutOnFailure: false);
            if (result.Succeeded)
            {
                var user = await _userManager.FindByEmailAsync(email);
                var roles = await _userManager.GetRolesAsync(user!);

                if (!roles.Contains("Admin"))
                {
                    await _signInManager.SignOutAsync();
                    ModelState.AddModelError(string.Empty, "Forbidden: Only administrators can enter.");
                    return View();
                }

                return RedirectToLocal(returnUrl);
            }

            ModelState.AddModelError(string.Empty, "Invalid credentials.");
            return View();
        }

        [HttpPost]
        public async Task<IActionResult> Logout()
        {
            await _signInManager.SignOutAsync();
            return RedirectToAction("Login");
        }

        public IActionResult AccessDenied() => View();

        private IActionResult RedirectToLocal(string? returnUrl)
        {
            if (Url.IsLocalUrl(returnUrl)) return Redirect(returnUrl);
            return RedirectToAction("Index", "Dashboard");
        }
    }
}
