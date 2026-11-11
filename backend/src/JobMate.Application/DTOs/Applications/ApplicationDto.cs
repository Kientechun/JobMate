using System;
using System.Collections.Generic;
using JobMate.Domain.Enums;
using JobMate.Application.DTOs.Jobs;

namespace JobMate.Application.DTOs.Applications
{
    public class ApplicationDto
    {
        public int Id { get; set; }
        public int JobId { get; set; }
        public JobDto Job { get; set; }
        public int? ResumeId { get; set; }
        public string CandidateName { get; set; }
        public string CandidateEmail { get; set; }
        public string ResumeUrl { get; set; }
        public string CoverLetter { get; set; }
        public ApplicationStatus Status { get; set; }
        public DateTime CreatedAt { get; set; }
        public List<ApplicationHistoryDto> Histories { get; set; }
    }

    public class ApplicationHistoryDto
    {
        public int Id { get; set; }
        public ApplicationStatus Status { get; set; }
        public string Note { get; set; }
        public DateTime CreatedAt { get; set; }
    }

    public class CreateApplicationRequest
    {
        public int JobId { get; set; }
        public int ResumeId { get; set; }
        public string CoverLetter { get; set; }
    }

    public class UpdateApplicationStatusRequest
    {
        public ApplicationStatus Status { get; set; }
        public string Note { get; set; }
    }
}
