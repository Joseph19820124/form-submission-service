package com.example.dto;

import com.example.entity.FormSubmissionEntity.SubmissionStatus;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

// Request DTO for creating/updating form submissions
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FormSubmissionRequestDto {
    
    @NotBlank(message = "Name is required")
    @Size(max = 100, message = "Name must not exceed 100 characters")
    private String name;
    
    @Email(message = "Invalid email format")
    @NotBlank(message = "Email is required")
    private String email;
    
    @NotBlank(message = "Address is required")
    @Size(max = 500, message = "Address must not exceed 500 characters")
    private String address;
    
    @Size(max = 20, message = "Phone number must not exceed 20 characters")
    private String phoneNumber;
    
    @Size(max = 1000, message = "Comments must not exceed 1000 characters")
    private String comments;
    
    private String submittedByIp;
}

// Response DTO for returning form submission data
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FormSubmissionResponseDto {
    
    private Long id;
    private String name;
    private String email;
    private String address;
    private String phoneNumber;
    private String comments;
    private SubmissionStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String submittedByIp;
}

// DTO for updating only the status
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StatusUpdateDto {
    
    @NotNull(message = "Status is required")
    private SubmissionStatus status;
}

// DTO for search/filter requests
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubmissionSearchDto {
    
    private String name;
    private String email;
    private String address;
    private String phoneNumber;
    private SubmissionStatus status;
    private LocalDateTime fromDate;
    private LocalDateTime toDate;
    
    // Pagination parameters
    private int page = 0;
    private int size = 20;
    private String sortBy = "createdAt";
    private String sortDirection = "desc";
}

// DTO for bulk operations
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BulkUpdateDto {
    
    @NotEmpty(message = "Submission IDs are required")
    private java.util.List<Long> submissionIds;
    
    @NotNull(message = "Status is required")
    private SubmissionStatus status;
}

// DTO for statistics response
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubmissionStatsDto {
    
    private long totalSubmissions;
    private long pendingSubmissions;
    private long approvedSubmissions;
    private long rejectedSubmissions;
    private long processingSubmissions;
    private double approvalRate;
    private double rejectionRate;
}