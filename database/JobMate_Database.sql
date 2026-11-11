-- JobMate Database Creation Script for Microsoft SQL Server
-- Project: JobMate - Job & Internship Platform
-- Author: Senior Database Architect

CREATE DATABASE JobMateDB;
GO

USE JobMateDB;
GO

-----------------------------------------------------------
-- 1. ACCOUNT AND PERMISSION MODULE
-----------------------------------------------------------

CREATE TABLE Roles (
    RoleID INT PRIMARY KEY IDENTITY(1,1),
    RoleName NVARCHAR(50) NOT NULL UNIQUE,
    Description NVARCHAR(255)
);

CREATE TABLE Users (
    UserID INT PRIMARY KEY IDENTITY(1,1),
    Email NVARCHAR(100) NOT NULL UNIQUE,
    PasswordHash NVARCHAR(MAX) NOT NULL,
    FullName NVARCHAR(100) NOT NULL,
    RoleID INT NOT NULL,
    PhoneNumber NVARCHAR(20),
    AvatarUrl NVARCHAR(500),
    CreatedAt DATETIME2 DEFAULT GETDATE(),
    UpdatedAt DATETIME2 DEFAULT GETDATE(),
    IsActive BIT DEFAULT 1,
    FOREIGN KEY (RoleID) REFERENCES Roles(RoleID)
);

CREATE TABLE StudentProfiles (
    StudentID INT PRIMARY KEY IDENTITY(1,1),
    UserID INT NOT NULL UNIQUE,
    University NVARCHAR(200),
    Major NVARCHAR(200),
    GPA FLOAT,
    DateOfBirth DATE,
    Gender NVARCHAR(20),
    Address NVARCHAR(500),
    Bio NVARCHAR(MAX),
    FOREIGN KEY (UserID) REFERENCES Users(UserID)
);

CREATE TABLE Companies (
    CompanyID INT PRIMARY KEY IDENTITY(1,1),
    RecruiterID INT NOT NULL UNIQUE, -- Linked to User with Recruiter role
    CompanyName NVARCHAR(200) NOT NULL,
    Website NVARCHAR(200),
    Industry NVARCHAR(100),
    Location NVARCHAR(500),
    Description NVARCHAR(MAX),
    LogoUrl NVARCHAR(500),
    ContactEmail NVARCHAR(100),
    IsVerified BIT DEFAULT 0,
    FOREIGN KEY (RecruiterID) REFERENCES Users(UserID)
);

-----------------------------------------------------------
-- 2. JOBS MODULE
-----------------------------------------------------------

CREATE TABLE Categories (
    CategoryID INT PRIMARY KEY IDENTITY(1,1),
    CategoryName NVARCHAR(100) NOT NULL UNIQUE,
    Description NVARCHAR(255)
);

CREATE TABLE Skills (
    SkillID INT PRIMARY KEY IDENTITY(1,1),
    SkillName NVARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE Jobs (
    JobID INT PRIMARY KEY IDENTITY(1,1),
    CompanyID INT NOT NULL,
    CategoryID INT NOT NULL,
    Title NVARCHAR(200) NOT NULL,
    Description NVARCHAR(MAX) NOT NULL,
    Requirements NVARCHAR(MAX) NOT NULL,
    Benefits NVARCHAR(MAX),
    Location NVARCHAR(255),
    SalaryRange NVARCHAR(100),
    JobType NVARCHAR(50), -- Full-time, Internship, Part-time
    WorkMode NVARCHAR(50), -- Remote, On-site, Hybrid
    Status NVARCHAR(50) DEFAULT 'Draft', -- Draft, Pending, Published, Rejected, Closed, Expired
    PostedDate DATETIME2 DEFAULT GETDATE(),
    ClosingDate DATETIME2,
    CreatedAt DATETIME2 DEFAULT GETDATE(),
    UpdatedAt DATETIME2 DEFAULT GETDATE(),
    FOREIGN KEY (CompanyID) REFERENCES Companies(CompanyID),
    FOREIGN KEY (CategoryID) REFERENCES Categories(CategoryID),
    CONSTRAINT CHK_JobStatus CHECK (Status IN ('Draft', 'Pending', 'Published', 'Rejected', 'Closed', 'Expired'))
);

CREATE TABLE JobSkills (
    JobID INT NOT NULL,
    SkillID INT NOT NULL,
    ImportanceLevel INT DEFAULT 1, -- 1: Nice to have, 2: Important, 3: Required
    PRIMARY KEY (JobID, SkillID),
    FOREIGN KEY (JobID) REFERENCES Jobs(JobID),
    FOREIGN KEY (SkillID) REFERENCES Skills(SkillID)
);

-----------------------------------------------------------
-- 3. CANDIDATE RESUME MODULE
-----------------------------------------------------------

CREATE TABLE Resumes (
    ResumeID INT PRIMARY KEY IDENTITY(1,1),
    StudentID INT NOT NULL,
    Title NVARCHAR(200) NOT NULL,
    FileUrl NVARCHAR(500) NOT NULL,
    ThumbnailUrl NVARCHAR(500),
    IsMain BIT DEFAULT 0,
    CreatedAt DATETIME2 DEFAULT GETDATE(),
    UpdatedAt DATETIME2 DEFAULT GETDATE(),
    FOREIGN KEY (StudentID) REFERENCES StudentProfiles(StudentID)
);

CREATE TABLE ResumeSkills (
    ResumeID INT NOT NULL,
    SkillID INT NOT NULL,
    PRIMARY KEY (ResumeID, SkillID),
    FOREIGN KEY (ResumeID) REFERENCES Resumes(ResumeID),
    FOREIGN KEY (SkillID) REFERENCES Skills(SkillID)
);

CREATE TABLE StudentSkills (
    StudentID INT NOT NULL,
    SkillID INT NOT NULL,
    YearsOfExperience INT DEFAULT 0,
    PRIMARY KEY (StudentID, SkillID),
    FOREIGN KEY (StudentID) REFERENCES StudentProfiles(StudentID),
    FOREIGN KEY (SkillID) REFERENCES Skills(SkillID)
);

-----------------------------------------------------------
-- 4. APPLICATION MODULE
-----------------------------------------------------------

CREATE TABLE Applications (
    ApplicationID INT PRIMARY KEY IDENTITY(1,1),
    JobID INT NOT NULL,
    StudentID INT NOT NULL,
    ResumeID INT NOT NULL,
    CoverLetter NVARCHAR(MAX),
    Status NVARCHAR(50) DEFAULT 'Submitted', -- Submitted, Reviewing, Shortlisted, Interviewing, Accepted, Rejected, Withdrawn
    AppliedDate DATETIME2 DEFAULT GETDATE(),
    UpdatedAt DATETIME2 DEFAULT GETDATE(),
    FOREIGN KEY (JobID) REFERENCES Jobs(JobID),
    FOREIGN KEY (StudentID) REFERENCES StudentProfiles(StudentID),
    FOREIGN KEY (ResumeID) REFERENCES Resumes(ResumeID),
    CONSTRAINT CHK_AppStatus CHECK (Status IN ('Submitted', 'Reviewing', 'Shortlisted', 'Interviewing', 'Accepted', 'Rejected', 'Withdrawn'))
);

CREATE TABLE ApplicationHistories (
    HistoryID INT PRIMARY KEY IDENTITY(1,1),
    ApplicationID INT NOT NULL,
    OldStatus NVARCHAR(50),
    NewStatus NVARCHAR(50) NOT NULL,
    ChangedAt DATETIME2 DEFAULT GETDATE(),
    ChangedBy INT, -- UserID
    Comment NVARCHAR(MAX),
    FOREIGN KEY (ApplicationID) REFERENCES Applications(ApplicationID),
    FOREIGN KEY (ChangedBy) REFERENCES Users(UserID)
);

CREATE TABLE SavedJobs (
    StudentID INT NOT NULL,
    JobID INT NOT NULL,
    SavedAt DATETIME2 DEFAULT GETDATE(),
    PRIMARY KEY (StudentID, JobID),
    FOREIGN KEY (StudentID) REFERENCES StudentProfiles(StudentID),
    FOREIGN KEY (JobID) REFERENCES Jobs(JobID)
);

CREATE TABLE Interviews (
    InterviewID INT PRIMARY KEY IDENTITY(1,1),
    ApplicationID INT NOT NULL,
    InterviewDate DATETIME2 NOT NULL,
    Location NVARCHAR(255), -- Link for online or address for offline
    Type NVARCHAR(50), -- Online, Offline
    Status NVARCHAR(50) DEFAULT 'Scheduled', -- Scheduled, Completed, Cancelled
    Notes NVARCHAR(MAX),
    FOREIGN KEY (ApplicationID) REFERENCES Applications(ApplicationID)
);

CREATE TABLE InterviewEvaluations (
    EvaluationID INT PRIMARY KEY IDENTITY(1,1),
    InterviewID INT NOT NULL UNIQUE,
    Score INT CHECK (Score BETWEEN 1 AND 10),
    Strengths NVARCHAR(MAX),
    Weaknesses NVARCHAR(MAX),
    FinalRecommendation NVARCHAR(MAX),
    CreatedAt DATETIME2 DEFAULT GETDATE(),
    FOREIGN KEY (InterviewID) REFERENCES Interviews(InterviewID)
);

-----------------------------------------------------------
-- 5. SYSTEM MODULE
-----------------------------------------------------------

CREATE TABLE Notifications (
    NotificationID INT PRIMARY KEY IDENTITY(1,1),
    UserID INT NOT NULL,
    Title NVARCHAR(200) NOT NULL,
    Message NVARCHAR(MAX) NOT NULL,
    Type NVARCHAR(50), -- ApplicationUpdate, NewJob, InterviewReminder
    IsRead BIT DEFAULT 0,
    CreatedAt DATETIME2 DEFAULT GETDATE(),
    FOREIGN KEY (UserID) REFERENCES Users(UserID)
);

CREATE TABLE AIResumeAnalyses (
    AnalysisID INT PRIMARY KEY IDENTITY(1,1),
    ResumeID INT NOT NULL UNIQUE,
    Summary NVARCHAR(MAX),
    MatchingScore FLOAT, -- General score for job market
    SuggestedKeywords NVARCHAR(MAX),
    CreatedAt DATETIME2 DEFAULT GETDATE(),
    FOREIGN KEY (ResumeID) REFERENCES Resumes(ResumeID)
);

CREATE TABLE AIJobRecommendations (
    RecommendationID INT PRIMARY KEY IDENTITY(1,1),
    StudentID INT NOT NULL,
    JobID INT NOT NULL,
    MatchReason NVARCHAR(MAX),
    Score FLOAT,
    IsDismissed BIT DEFAULT 0,
    CreatedAt DATETIME2 DEFAULT GETDATE(),
    FOREIGN KEY (StudentID) REFERENCES StudentProfiles(StudentID),
    FOREIGN KEY (JobID) REFERENCES Jobs(JobID)
);

-----------------------------------------------------------
-- INDEXES FOR PERFORMANCE
-----------------------------------------------------------
CREATE INDEX IX_Jobs_Status ON Jobs(Status);
CREATE INDEX IX_Jobs_CompanyID ON Jobs(CompanyID);
CREATE INDEX IX_Applications_JobID ON Applications(JobID);
CREATE INDEX IX_Applications_StudentID ON Applications(StudentID);
CREATE INDEX IX_Notifications_UserID_Read ON Notifications(UserID, IsRead);
