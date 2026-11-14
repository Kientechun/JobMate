using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading;
using System.Threading.Tasks;
using JobMate.Application.Common.Interfaces;
using JobMate.Application.DTOs.Applications;
using JobMate.Domain.Entities;
using JobMate.Domain.Enums;
using JobMate.Infrastructure.Persistence.Services;
using Microsoft.EntityFrameworkCore;
using Moq;
using Xunit;

namespace JobMate.Application.UnitTests.Services
{
    public class ApplicationServiceTests
    {
        private readonly Mock<IJobMateDbContext> _mockContext;
        private readonly Mock<INotificationService> _mockNotificationService;
        private readonly ApplicationService _applicationService;

        public ApplicationServiceTests()
        {
            _mockContext = new Mock<IJobMateDbContext>();
            _mockNotificationService = new Mock<INotificationService>();
            _applicationService = new ApplicationService(_mockContext.Object, _mockNotificationService.Object);
        }

        [Fact]
        public async Task ApplyJobAsync_ShouldThrowException_WhenJobNotFound()
        {
            // Arrange
            var request = new CreateApplicationRequest { JobId = 1, ResumeId = 1 };
            var userId = "user1";

            var mockJobSet = CreateMockDbSet(new List<Job>());
            _mockContext.Setup(c => c.Jobs).Returns(mockJobSet.Object);

            // Act & Assert
            await Assert.ThrowsAsync<Exception>(() => _applicationService.ApplyJobAsync(request, userId));
        }

        [Fact]
        public async Task ApplyJobAsync_ShouldSucceed_WhenDataIsValid()
        {
            // Arrange
            var userId = "user1";
            var request = new CreateApplicationRequest { JobId = 1, ResumeId = 1, CoverLetter = "Hello" };

            var job = new Job { Id = 1, Status = JobStatus.Published, ExpiryDate = DateTime.UtcNow.AddDays(1) };
            var resume = new Resume { Id = 1, UserId = userId, FullName = "Test Candidate", Email = "test@test.com", FileUrl = "http://cv.com" };

            var mockJobSet = CreateMockDbSet(new List<Job> { job });
            var mockResumeSet = CreateMockDbSet(new List<Resume> { resume });
            var mockAppSet = CreateMockDbSet(new List<JobApplication>());

            _mockContext.Setup(c => c.Jobs).Returns(mockJobSet.Object);
            _mockContext.Setup(c => c.Resumes).Returns(mockResumeSet.Object);
            _mockContext.Setup(c => c.JobApplications).Returns(mockAppSet.Object);

            // Act
            var result = await _applicationService.ApplyJobAsync(request, userId);

            // Assert
            Assert.NotNull(result);
            Assert.Equal(ApplicationStatus.Pending, result.Status);
            _mockContext.Verify(c => c.JobApplications.Add(It.IsAny<JobApplication>()), Times.Once);
            _mockContext.Verify(c => c.SaveChangesAsync(It.IsAny<CancellationToken>()), Times.Once);
        }

        [Fact]
        public async Task WithdrawApplicationAsync_ShouldUpdateIsDeleted()
        {
            // Arrange
            var applicationId = 1;
            var userId = "user1";
            var application = new JobApplication { Id = applicationId, UserId = userId, Status = ApplicationStatus.Pending };

            var mockAppSet = CreateMockDbSet(new List<JobApplication> { application });
            _mockContext.Setup(c => c.JobApplications).Returns(mockAppSet.Object);

            // Act
            var result = await _applicationService.WithdrawApplicationAsync(applicationId, userId);

            // Assert
            Assert.True(result);
            Assert.True(application.IsDeleted);
        }

        private Mock<DbSet<T>> CreateMockDbSet<T>(List<T> sourceList) where T : class
        {
            return MockDbSetExtensions.CreateMockDbSet(sourceList);
        }
    }
}
