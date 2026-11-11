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
            Assert.True(result.IsSuccess);
            Assert.Equal(JobStatus.Published, job.Status);
            _mockContext.Verify(c => c.SaveChangesAsync(It.IsAny<CancellationToken>()), Times.Once);
        }

        [Fact]
        public async Task GetJobByIdAsync_ShouldReturnNull_WhenJobIsNotPublished()
        {
            // Arrange
            var jobId = 1;
            var job = new Job { Id = jobId, Status = JobStatus.Pending, IsDeleted = false };

            var data = new List<Job> { job }.AsQueryable();
            var mockSet = new Mock<DbSet<Job>>();
            mockSet.As<IQueryable<Job>>().Setup(m => m.Provider).Returns(data.Provider);
            mockSet.As<IQueryable<Job>>().Setup(m => m.Expression).Returns(data.Expression);
            mockSet.As<IQueryable<Job>>().Setup(m => m.ElementType).Returns(data.ElementType);
            mockSet.As<IQueryable<Job>>().Setup(m => m.GetEnumerator()).Returns(data.GetEnumerator());

            _mockContext.Setup(c => c.Jobs).Returns(mockSet.Object);

            // Act
            var result = await _jobService.GetJobByIdAsync(jobId);

            // Assert
            Assert.Null(result);
        }

        private Mock<DbSet<T>> CreateMockDbSet<T>(List<T> sourceList) where T : class
        {
            var queryable = sourceList.AsQueryable();
            var dbSet = new Mock<DbSet<T>>();
            dbSet.As<IQueryable<T>>().Setup(m => m.Provider).Returns(queryable.Provider);
            dbSet.As<IQueryable<T>>().Setup(m => m.Expression).Returns(queryable.Expression);
            dbSet.As<IQueryable<T>>().Setup(m => m.ElementType).Returns(queryable.ElementType);
            dbSet.As<IQueryable<T>>().Setup(m => m.GetEnumerator()).Returns(queryable.GetEnumerator());
            return dbSet;
        }
    }
}
