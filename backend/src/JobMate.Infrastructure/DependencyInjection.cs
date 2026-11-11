using JobMate.Application.Common.Interfaces;
using JobMate.Infrastructure.Identity;
using JobMate.Infrastructure.Persistence;
using JobMate.Infrastructure.Persistence.Services;
using JobMate.Infrastructure.Services;
using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Identity;
using Microsoft.EntityFrameworkCore;
using Microsoft.Extensions.Configuration;
using Microsoft.Extensions.DependencyInjection;

namespace JobMate.Infrastructure
{
    public static class DependencyInjection
    {
        public static IServiceCollection AddInfrastructure(this IServiceCollection services, IConfiguration configuration)
        {
            services.AddDbContext<JobMateDbContext>(options =>
                options.UseSqlServer(
                    configuration.GetConnectionString("DefaultConnection"),
                    b => b.MigrationsAssembly(typeof(JobMateDbContext).Assembly.FullName)));

            services.AddScoped<IJobMateDbContext>(provider => provider.GetRequiredService<JobMateDbContext>());

            services.AddIdentity<ApplicationUser, IdentityRole>()
                .AddEntityFrameworkStores<JobMateDbContext>()
                .AddDefaultTokenProviders();

            services.AddSingleton<IHttpContextAccessor, HttpContextAccessor>();
            services.AddScoped<ICurrentUserService, CurrentUserService>();
            services.AddScoped<IJwtTokenGenerator, JwtTokenGenerator>();
            services.AddScoped<IAuthService, AuthService>();
            services.AddScoped<IJobService, JobService>();
            services.AddScoped<IResumeService, ResumeService>();
            services.AddScoped<IApplicationService, ApplicationService>();

            services.AddHttpClient<IAIService, AIService>();

            return services;
        }
    }
}
