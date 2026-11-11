# JobMate Business Rules & Data Logic

Tài liệu quy định các quy tắc nghiệp vụ áp dụng cho Cơ sở dữ liệu JobMate.

## 1. Quản lý Tài khoản & Phân quyền
*   **Email Unique**: Một địa chỉ email chỉ được đăng ký cho một tài khoản duy nhất trên toàn hệ thống.
*   **Role Constraint**:
    *   `Candidate`: Chỉ có thể sở hữu `StudentProfile` và tạo `Resumes`.
    *   `Recruiter`: Phải thuộc về một `Company`. Một Recruiter chỉ có thể quản lý Jobs và Applications của công ty mình.
    *   `Admin`: Có quyền duyệt Company (`IsVerified`) và quản lý Categories/Skills.
*   **Soft Deletion**: Thay vì xóa User, hãy cập nhật `IsActive = 0` để giữ tính toàn vẹn cho các dữ liệu lịch sử ứng tuyển.

## 2. Quy trình Tuyển dụng (Job Workflow)
*   **Trạng thái Job**:
    1.  `Draft`: Nhà tuyển dụng đang soạn thảo.
    2.  `Pending`: Đang chờ Admin phê duyệt nội dung.
    3.  `Published`: Đang hiển thị cho ứng viên ứng tuyển.
    4.  `Rejected`: Admin từ chối tin đăng (lý do lưu trong logs).
    5.  `Closed`: Nhà tuyển dụng đóng tin thủ công khi đủ người.
    6.  `Expired`: Hệ thống tự động chuyển khi qua ngày `ClosingDate`.
*   **Job Validation**: `ClosingDate` phải lớn hơn hoặc bằng ngày hiện tại (`PostedDate`).

## 3. Quy trình Ứng tuyển (Application Workflow)
*   **Trạng thái Hồ sơ**:
    *   `Submitted`: Ứng viên vừa nộp.
    *   `Reviewing`: Nhà tuyển dụng đang xem xét.
    *   `Shortlisted`: Hồ sơ tiềm năng, chuẩn bị phỏng vấn.
    *   `Interviewing`: Đang trong quá trình phỏng vấn.
    *   `Accepted`: Gửi offer/Chấp nhận vào làm.
    *   `Rejected`: Từ chối hồ sơ.
    *   `Withdrawn`: Ứng viên tự rút hồ sơ.
*   **Audit Trail**: Mọi thay đổi trạng thái trong bảng `Applications` phải được ghi log vào `ApplicationHistories` kèm theo `UserID` của người thực hiện và lý do (nếu có).

## 4. Bảo mật dữ liệu
*   **Password**: Tuyệt đối không lưu mật khẩu văn bản thuần. Phải sử dụng `PasswordHash` với thuật toán như Argon2 hoặc BCrypt.
*   **Candidate Privacy**: Thông tin cá nhân như số điện thoại, địa chỉ chỉ được hiển thị cho Recruiter khi trạng thái ứng tuyển là `Reviewing` hoặc cao hơn.

## 5. Logic AI & Gợi ý
*   **Matching Score**: Được tính toán dựa trên sự giao thoa giữa `JobSkills` và `ResumeSkills` kết hợp với `Major` và `GPA`.
*   **Recommendation**: Hệ thống ưu tiên gợi ý các công việc có `Status = 'Published'` và phù hợp với kỹ năng của ứng viên.
