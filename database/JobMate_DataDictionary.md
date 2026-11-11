# JobMate Data Dictionary

Tài liệu chi tiết về các bảng và cột trong cơ sở dữ liệu JobMate.

## 1. Bảng Users
Lưu trữ thông tin tài khoản đăng nhập cho tất cả các vai trò.

| Cột | Kiểu dữ liệu | Ràng buộc | Mô tả |
| :--- | :--- | :--- | :--- |
| UserID | INT | PK, IDENTITY | Khóa chính tự tăng |
| Email | NVARCHAR(100) | UNIQUE, NOT NULL | Địa chỉ email dùng để đăng nhập |
| PasswordHash | NVARCHAR(MAX) | NOT NULL | Mật khẩu đã được mã hóa (BCrypt/Identity) |
| FullName | NVARCHAR(100) | NOT NULL | Họ và tên hiển thị |
| RoleID | INT | FK | Tham chiếu đến bảng Roles |
| IsActive | BIT | DEFAULT 1 | Trạng thái tài khoản (1: Hoạt động, 0: Khóa) |

## 2. Bảng Jobs
Lưu trữ thông tin tin tuyển dụng.

| Cột | Kiểu dữ liệu | Ràng buộc | Mô tả |
| :--- | :--- | :--- | :--- |
| JobID | INT | PK, IDENTITY | Khóa chính |
| CompanyID | INT | FK, NOT NULL | Công ty đăng tin |
| CategoryID | INT | FK, NOT NULL | Danh mục ngành nghề |
| Title | NVARCHAR(200) | NOT NULL | Tiêu đề công việc |
| Status | NVARCHAR(50) | CHECK | Draft, Pending, Published, Rejected, Closed, Expired |
| SalaryRange | NVARCHAR(100) | NULL | Mức lương (Ví dụ: 10-15tr hoặc Thỏa thuận) |
| PostedDate | DATETIME2 | DEFAULT GETDATE() | Ngày tin được duyệt đăng |

## 3. Bảng Applications
Lưu trữ thông tin hồ sơ ứng tuyển.

| Cột | Kiểu dữ liệu | Ràng buộc | Mô tả |
| :--- | :--- | :--- | :--- |
| ApplicationID | INT | PK, IDENTITY | Khóa chính |
| JobID | INT | FK, NOT NULL | Công việc ứng tuyển |
| StudentID | INT | FK, NOT NULL | Sinh viên ứng tuyển |
| ResumeID | INT | FK, NOT NULL | Bản CV được sử dụng để ứng tuyển |
| Status | NVARCHAR(50) | CHECK | Submitted, Reviewing, Shortlisted, Interviewing, Accepted, Rejected, Withdrawn |

## 4. Bảng StudentProfiles & Companies
*   **StudentProfiles**: Mở rộng thông tin cho User có Role = 'Candidate' (GPA, University, Major).
*   **Companies**: Lưu trữ thông tin doanh nghiệp, mỗi Recruiter đại diện cho một Company.
