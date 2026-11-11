using System.Reflection;
using FluentValidation;
using Microsoft.Extensions.DependencyInjection;

namespace JobMate.Application
{
    public static class DependencyInjection
    {
        public static IServiceCollection AddApplication(this IServiceCollection services)
        {
            services.AddValidatorsFromAssembly(Assembly.GetExecutingAssembly());

            // Register other application services here

            // No changes needed if services are in Infrastructure,
            // but usually IApplicationService is registered in Infrastructure's DependencyInjection
            return services;
        }
    }
}
