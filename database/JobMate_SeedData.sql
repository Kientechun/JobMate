USE JobMateDB;
GO

-----------------------------------------------------------
-- 1. ROLES
-----------------------------------------------------------
INSERT INTO Roles (RoleName, Description) VALUES
('Admin', 'System administrator with full access'),
('Recruiter', 'Company representative who posts jobs'),
('Candidate', 'Job seeker/Student');

-----------------------------------------------------------
-- 2. USERS (PasswordHash is dummy here)
-----------------------------------------------------------
-- Admin
INSERT INTO Users (Email, PasswordHash, FullName, RoleID, IsActive)
VALUES ('admin@jobmate.com', 'hashed_password_123', 'System Admin', 1, 1);

-- Recruiters
INSERT INTO Users (Email, PasswordHash, FullName, RoleID, IsActive) VALUES
('hr@techcorp.com', 'hashed_password_456', 'Nguyễn HR', 2, 1),
('recruiter@softsystem.vn', 'hashed_password_789', 'Trần Tuyển Dụng', 2, 1);

-- Candidates
INSERT INTO Users (Email, PasswordHash, FullName, RoleID, IsActive) VALUES
('student1@gmail.com', 'hashed_password_aaa', 'Nguyễn Văn A', 3, 1),
('student2@gmail.com', 'hashed_password_bbb', 'Lê Thị B', 3, 1),
('student3@gmail.com', 'hashed_password_ccc', 'Phạm Hoàng C', 3, 1);

-----------------------------------------------------------
-- 3. COMPANIES
-----------------------------------------------------------
INSERT INTO Companies (RecruiterID, CompanyName, Website, Industry, Location, Description, IsVerified) VALUES
(2, 'TechCorp Solutions', 'https://techcorp.com', 'IT Software', 'Hà Nội', 'Leading tech company in Vietnam', 1),
(3, 'SoftSystem VN', 'https://softsystem.vn', 'IT Outsourcing', 'TP. Hồ Chí Minh', 'Global outsourcing provider', 1);

-----------------------------------------------------------
-- 4. STUDENT PROFILES
-----------------------------------------------------------
INSERT INTO StudentProfiles (UserID, University, Major, GPA, DateOfBirth, Gender, Address, Bio) VALUES
(4, 'Đại học Bách Khoa', 'Công nghệ thông tin', 3.6, '2002-05-15', 'Male', 'Hai Bà Trưng, Hà Nội', 'Đam mê lập trình Android'),
(5, 'Đại học Quốc Gia', 'Khoa học máy tính', 3.2, '2003-08-20', 'Female', 'Quận 1, TP. HCM', 'Thích làm việc với dữ liệu'),
(6, 'Đại học FPT', 'Kỹ thuật phần mềm', 3.8, '2002-12-10', 'Male', 'Đà Nẵng', 'Fullstack developer intern');

-----------------------------------------------------------
-- 5. CATEGORIES & SKILLS
-----------------------------------------------------------
INSERT INTO Categories (CategoryName, Description) VALUES
('Mobile Development', 'Android, iOS, Flutter, React Native'),
('Web Development', 'Frontend, Backend, Fullstack'),
('Data Science', 'AI, Machine Learning, Big Data'),
('UI/UX Design', 'Figma, Adobe XD');

INSERT INTO Skills (SkillName) VALUES
('Kotlin'), ('Java'), ('Android SDK'), ('Swift'),
('ASP.NET Core'), ('SQL Server'), ('C#'),
('React'), ('Angular'), ('Node.js'),
('Python'), ('Machine Learning'), ('Figma');

-----------------------------------------------------------
-- 6. JOBS
-----------------------------------------------------------
INSERT INTO Jobs (CompanyID, CategoryID, Title, Description, Requirements, Benefits, Location, SalaryRange, JobType, WorkMode, Status) VALUES
(1, 1, 'Android Developer Intern', 'Tham gia phát triển ứng dụng di động', 'Biết Kotlin cơ bản', 'Hỗ trợ 5tr/tháng', 'Hà Nội', '5.000.000 VND', 'Internship', 'On-site', 'Published'),
(1, 2, '.NET Backend Developer', 'Xây dựng hệ thống API cho JobMate', 'Kinh nghiệm ASP.NET Core', 'Lương cạnh tranh, bảo hiểm', 'Remote', '15 - 25 triệu', 'Full-time', 'Remote', 'Published'),
(2, 1, 'iOS Developer (Junior)', 'Phát triển App trên nền tảng iOS', 'Biết Swift và UIKit', 'Thưởng dự án', 'TP. HCM', '10 - 20 triệu', 'Full-time', 'Hybrid', 'Published');

-----------------------------------------------------------
-- 7. JOB SKILLS
-----------------------------------------------------------
INSERT INTO JobSkills (JobID, SkillID, ImportanceLevel) VALUES
(1, 1, 3), (1, 2, 2), (1, 3, 3), -- Android Job: Kotlin, Java, SDK
(2, 5, 3), (2, 6, 3), (2, 7, 3), -- Backend Job: ASP.NET, SQL, C#
(3, 4, 3); -- iOS Job: Swift

-----------------------------------------------------------
-- 8. RESUMES
-----------------------------------------------------------
INSERT INTO Resumes (StudentID, Title, FileUrl, IsMain) VALUES
(1, 'CV Android - Nguyễn Văn A', 'https://jobmate.storage/cvs/a1.pdf', 1),
(1, 'CV Design - Nguyễn Văn A', 'https://jobmate.storage/cvs/a2.pdf', 0),
(2, 'CV Data Science - Lê Thị B', 'https://jobmate.storage/cvs/b1.pdf', 1);

-----------------------------------------------------------
-- 9. APPLICATIONS
-----------------------------------------------------------
INSERT INTO Applications (JobID, StudentID, ResumeID, CoverLetter, Status) VALUES
(1, 1, 1, 'Tôi rất mong muốn được thực tập tại công ty', 'Reviewing'),
(2, 3, 3, 'Tôi có kinh nghiệm làm việc với .NET', 'Submitted');

-----------------------------------------------------------
-- 10. APPLICATION HISTORIES
-----------------------------------------------------------
INSERT INTO ApplicationHistories (ApplicationID, OldStatus, NewStatus, ChangedBy, Comment) VALUES
(1, 'Submitted', 'Reviewing', 2, 'Đang xem xét hồ sơ ứng viên A');

-----------------------------------------------------------
-- 11. INTERVIEWS
-----------------------------------------------------------
INSERT INTO Interviews (ApplicationID, InterviewDate, Location, Type, Status) VALUES
(1, '2026-11-20 09:00:00', 'https://meet.google.com/abc-xyz', 'Online', 'Scheduled');
