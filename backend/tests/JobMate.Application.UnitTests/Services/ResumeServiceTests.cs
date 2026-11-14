using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using JobMate.Application.Common.Interfaces;
using JobMate.Application.DTOs.Resumes;
using JobMate.Domain.Entities;
using JobMate.Infrastructure.Persistence.Services;
using Microsoft.EntityFrameworkCore;
using Moq;
using Xunit;

namespace JobMate.Application.UnitTests.Services
{
    public class ResumeServiceTests
    {
        private readonly Mock<IJobMateDbContext> _mockContext;
        private readonly ResumeService _resumeService;

        public ResumeServiceTests()
        {
            _mockContext = new Mock<IJobMateDbContext>();
            _resumeService = new ResumeService(_mockContext.Object);
        }

        [Fact]
        public async Task GetResumeByIdAsync_ShouldReturnNull_WhenResumeBelongsToOtherUser()
        {
            // Arrange
            var resumeId = 1;
            var ownerId = "user1";
            var strangerId = "user2";
            var resume = new Resume { Id = resumeId, UserId = ownerId, IsDeleted = false };

            var mockSet = CreateMockDbSet(new List<Resume> { resume });
            _mockContext.Setup(c => c.Resumes).Returns(mockSet.Object);

            // Act
            var result = await _resumeService.GetResumeByIdAsync(resumeId, strangerId);

            // Assert
            Assert.Null(result);
        }

        [Fact]
        public async Task SetDefaultResumeAsync_ShouldUpdateIsDefault()
        {
            // Arrange
            var userId = "user1";
            var resume1 = new Resume { Id = 1, UserId = userId, IsDefault = true, IsDeleted = false };
            var resume2 = new Resume { Id = 2, UserId = userId, IsDefault = false, IsDeleted = false };

            var mockSet = CreateMockDbSet(new List<Resume> { resume1, resume2 });
            _mockContext.Setup(c => c.Resumes).Returns(mockSet.Object);

            // Act
            var result = await _resumeService.SetDefaultResumeAsync(2, userId);

            // Assert
            Assert.True(result);
            Assert.False(resume1.IsDefault);
            Assert.True(resume2.IsDefault);
            _mockContext.Verify(c => c.SaveChangesAsync(default), Times.Once);
        }

        private Mock<DbSet<T>> CreateMockDbSet<T>(List<T> sourceList) where T : class
        {
            return MockDbSetExtensions.CreateMockDbSet(sourceList);
        }
    }
}
