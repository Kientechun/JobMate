using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading;
using System.Threading.Tasks;
using JobMate.Application.Common.Interfaces;
using JobMate.Application.DTOs.Jobs;
using JobMate.Domain.Entities;
using JobMate.Domain.Enums;
using JobMate.Infrastructure.Persistence.Services;
using Microsoft.EntityFrameworkCore;
using Moq;
using Xunit;

namespace JobMate.Application.UnitTests.Services
{
    public class JobServiceTests
    {
        private readonly Mock<IJobMateDbContext> _mockContext;
        private readonly JobService _jobService;

        public JobServiceTests()
        {
            _mockContext = new Mock<IJobMateDbContext>();
            _jobService = new JobService(_mockContext.Object);
        }

        [Fact]
        public async Task ApproveJobAsync_ShouldUpdateStatusToPublished()
        {
            // Arrange
            var jobId = 1;
            var job = new Job { Id = jobId, Status = JobStatus.Pending, IsDeleted = false };

            var mockSet = CreateMockDbSet(new List<Job> { job });
            _mockContext.Setup(c => c.Jobs).Returns(mockSet.Object);
            _mockContext.Setup(c => c.Jobs.FindAsync(jobId)).ReturnsAsync(job);

            // Act
            var result = await _jobService.ApproveJobAsync(jobId);

            // Assert
            Assert.True(result.Succeeded);
            Assert.Equal(JobStatus.Published, job.Status);
            _mockContext.Verify(c => c.SaveChangesAsync(It.IsAny<CancellationToken>()), Times.Once);
        }

        [Fact]
        public async Task CreateJobAsync_ShouldSetStatusToPending()
        {
            // Arrange
            var request = new CreateJobRequest
            {
                Title = "New Developer",
                Description = "A new job",
                CompanyName = "Tech Corp",
                Location = "Remote",
                SalaryRange = 1000,
                JobType = "FullTime",
                Experience = "Senior",
                ExpiryDate = DateTime.UtcNow.AddDays(30)
            };
            var userId = "recruiter1";

            _mockContext.Setup(c => c.Jobs.Add(It.IsAny<Job>()));

            // Act
            var result = await _jobService.CreateJobAsync(request, userId);

            // Assert
            Assert.True(result.Succeeded);
            _mockContext.Verify(c => c.Jobs.Add(It.Is<Job>(j => j.Status == JobStatus.Pending)), Times.Once);
            _mockContext.Verify(c => c.SaveChangesAsync(It.IsAny<CancellationToken>()), Times.Once);
        }

        [Fact]
        public async Task UpdateJobAsync_ShouldSucceed_WhenCompanyMatches()
        {
            // Arrange
            var jobId = 1;
            var companyId = 10;
            var userId = "recruiter1";
            var job = new Job { Id = jobId, CompanyId = companyId, IsDeleted = false };
            var request = new UpdateJobRequest { Title = "Updated Title", CompanyName = "Tech Corp", Location = "Remote" };

            _mockContext.Setup(c => c.Jobs.FindAsync(jobId)).ReturnsAsync(job);

            // Act
            var result = await _jobService.UpdateJobAsync(jobId, request, userId, companyId);

            // Assert
            Assert.True(result.Succeeded);
            Assert.Equal("Updated Title", job.Title);
            Assert.Equal(JobStatus.Pending, job.Status);
        }

        [Fact]
        public async Task UpdateJobAsync_ShouldFail_WhenCompanyDoesNotMatch()
        {
            // Arrange
            var jobId = 1;
            var jobCompanyId = 10;
            var recruiterCompanyId = 20;
            var userId = "recruiter1";
            var job = new Job { Id = jobId, CompanyId = jobCompanyId, IsDeleted = false };
            var request = new UpdateJobRequest();

            _mockContext.Setup(c => c.Jobs.FindAsync(jobId)).ReturnsAsync(job);

            // Act
            var result = await _jobService.UpdateJobAsync(jobId, request, userId, recruiterCompanyId);

            // Assert
            Assert.False(result.Succeeded);
            Assert.Contains("You do not have permission to update this job.", result.Errors);
        }

        [Fact]
        public async Task CloseJobAsync_ShouldSucceed_WhenCompanyMatches()
        {
            // Arrange
            var jobId = 1;
            var companyId = 10;
            var userId = "recruiter1";
            var job = new Job { Id = jobId, CompanyId = companyId, IsDeleted = false, Status = JobStatus.Published };

            _mockContext.Setup(c => c.Jobs.FindAsync(jobId)).ReturnsAsync(job);

            // Act
            var result = await _jobService.CloseJobAsync(jobId, userId, companyId);

            // Assert
            Assert.True(result.Succeeded);
            Assert.Equal(JobStatus.Closed, job.Status);
        }

        private Mock<DbSet<T>> CreateMockDbSet<T>(List<T> sourceList) where T : class
        {
            return MockDbSetExtensions.CreateMockDbSet(sourceList);
        }
    }
}
