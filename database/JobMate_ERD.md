# JobMate Entity Relationship Diagram (ERD)

Mô tả quan hệ giữa các thực thể trong hệ thống JobMate.

```mermaid
erDiagram
    ROLES ||--o{ USERS : "belongs to"
    USERS ||--o| STUDENT_PROFILES : "is a student"
    USERS ||--o| COMPANIES : "manages/belongs to"
    
    COMPANIES ||--o{ JOBS : "posts"
    CATEGORIES ||--o{ JOBS : "classifies"
    JOBS ||--o{ JOB_SKILLS : "requires"
    SKILLS ||--o{ JOB_SKILLS : "is used in"
    
    STUDENT_PROFILES ||--o{ RESUMES : "has"
    RESUMES ||--o{ RESUME_SKILLS : "highlights"
    SKILLS ||--o{ RESUME_SKILLS : "is listed in"
    
    STUDENT_PROFILES ||--o{ STUDENT_SKILLS : "possesses"
    SKILLS ||--o{ STUDENT_SKILLS : "belongs to student"
    
    STUDENT_PROFILES ||--o{ APPLICATIONS : "applies"
    JOBS ||--o{ APPLICATIONS : "receives"
    RESUMES ||--o{ APPLICATIONS : "used in"
    
    APPLICATIONS ||--o{ APPLICATION_HISTORIES : "tracks"
    APPLICATIONS ||--o{ INTERVIEWS : "schedules"
    INTERVIEWS ||--o| INTERVIEW_EVALUATIONS : "results in"
    
    STUDENT_PROFILES ||--o{ SAVED_JOBS : "saves"
    JOBS ||--o{ SAVED_JOBS : "is saved by"
    
    USERS ||--o{ NOTIFICATIONS : "receives"
    
    RESUMES ||--o| AI_RESUME_ANALYSES : "analyzed by"
    STUDENT_PROFILES ||--o{ AI_JOB_RECOMMENDATIONS : "suggested to"
    JOBS ||--o{ AI_JOB_RECOMMENDATIONS : "suggested"
```

## Giải thích quan hệ chính:
1.  **Users & Roles**: Phân quyền hệ thống (Admin, Recruiter, Candidate).
2.  **StudentProfiles & Companies**: Thông tin chi tiết mở rộng cho User tùy theo Role.
3.  **Jobs & Skills**: Một công việc yêu cầu nhiều kỹ năng (n-n qua JobSkills).
4.  **Resumes**: Ứng viên (Student) có thể có nhiều bản CV.
5.  **Applications**: Trung tâm của quy trình, kết nối Candidate, Job và Resume.
6.  **ApplicationHistories**: Lưu lại mọi bước chuyển trạng thái (Submitted -> Reviewing -> ...).
7.  **AI Components**: Hỗ trợ phân tích CV và gợi ý việc làm dựa trên profile ứng viên.
