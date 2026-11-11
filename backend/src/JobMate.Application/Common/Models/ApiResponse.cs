using System.Collections.Generic;

namespace JobMate.Application.Common.Models
{
    public class ApiResponse<T>
    {
        public bool Succeeded { get; set; }
        public T Data { get; set; }
        public string Message { get; set; }
        public List<string> Errors { get; set; }

        public static ApiResponse<T> Success(T data, string message = null)
        {
            return new ApiResponse<T> { Succeeded = true, Data = data, Message = message };
        }

        public static ApiResponse<T> Failure(List<string> errors, string message = null)
        {
            return new ApiResponse<T> { Succeeded = false, Errors = errors, Message = message };
        }

        public static ApiResponse<T> Failure(string error, string message = null)
        {
            return new ApiResponse<T> { Succeeded = false, Errors = new List<string> { error }, Message = message };
        }
    }
}
