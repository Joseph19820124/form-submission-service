package com.example.service;

import com.example.entity.FormSubmissionEntity;
import com.example.entity.FormSubmissionEntity.SubmissionStatus;
import com.example.repository.FormSubmissionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class FormSubmissionService {
    
    private final FormSubmissionRepository formSubmissionRepository;
    private final GoogleSheetsService googleSheetsService;
    
    /**
     * Create a new form submission
     */
    public FormSubmissionEntity createSubmission(FormSubmissionEntity submission) {
        // Check if email already exists
        if (formSubmissionRepository.existsByEmail(submission.getEmail())) {
            throw new IllegalArgumentException("Email already exists: " + submission.getEmail());
        }
        
        // Set default status if not provided
        if (submission.getStatus() == null) {
            submission.setStatus(SubmissionStatus.PENDING);
        }
        
        log.info("Creating new form submission for email: {}", submission.getEmail());
        FormSubmissionEntity savedSubmission = formSubmissionRepository.save(submission);
        
        // 异步写入到 Google Sheets
        try {
            googleSheetsService.writeSubmissionToSheet(savedSubmission);
        } catch (Exception e) {
            log.warn("Failed to write to Google Sheets, but submission was saved to database", e);
        }
        
        return savedSubmission;
    }
    
    /**
     * Get submission by ID
     */
    @Transactional(readOnly = true)
    public Optional<FormSubmissionEntity> getSubmissionById(Long id) {
        return formSubmissionRepository.findById(id);
    }
    
    /**
     * Get submission by email
     */
    @Transactional(readOnly = true)
    public Optional<FormSubmissionEntity> getSubmissionByEmail(String email) {
        return formSubmissionRepository.findByEmail(email);
    }
    
    /**
     * Get all submissions with pagination
     */
    @Transactional(readOnly = true)
    public Page<FormSubmissionEntity> getAllSubmissions(Pageable pageable) {
        return formSubmissionRepository.findAll(pageable);
    }
    
    /**
     * Get submissions by status
     */
    @Transactional(readOnly = true)
    public Page<FormSubmissionEntity> getSubmissionsByStatus(SubmissionStatus status, Pageable pageable) {
        return formSubmissionRepository.findByStatus(status, pageable);
    }
    
    /**
     * Update submission status
     */
    public FormSubmissionEntity updateSubmissionStatus(Long id, SubmissionStatus status) {
        FormSubmissionEntity submission = formSubmissionRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Submission not found with id: " + id));
        
        submission.setStatus(status);
        log.info("Updated submission {} status to {}", id, status);
        FormSubmissionEntity updatedSubmission = formSubmissionRepository.save(submission);
        
        // 同步更新 Google Sheets 中的状态
        try {
            googleSheetsService.updateSubmissionStatus(id, status.toString());
        } catch (Exception e) {
            log.warn("Failed to update status in Google Sheets for submission {}", id, e);
        }
        
        return updatedSubmission;
    }
    
    /**
     * Update existing submission
     */
    public FormSubmissionEntity updateSubmission(Long id, FormSubmissionEntity updatedSubmission) {
        FormSubmissionEntity existingSubmission = formSubmissionRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Submission not found with id: " + id));
        
        // Update fields (customize based on your requirements)
        existingSubmission.setName(updatedSubmission.getName());
        existingSubmission.setAddress(updatedSubmission.getAddress());
        existingSubmission.setPhoneNumber(updatedSubmission.getPhoneNumber());
        existingSubmission.setComments(updatedSubmission.getComments());
        
        log.info("Updated submission with id: {}", id);
        return formSubmissionRepository.save(existingSubmission);
    }
    
    /**
     * Delete submission
     */
    public void deleteSubmission(Long id) {
        if (!formSubmissionRepository.existsById(id)) {
            throw new IllegalArgumentException("Submission not found with id: " + id);
        }
        
        formSubmissionRepository.deleteById(id);
        log.info("Deleted submission with id: {}", id);
    }
    
    /**
     * Search submissions by name
     */
    @Transactional(readOnly = true)
    public List<FormSubmissionEntity> searchSubmissionsByName(String name) {
        return formSubmissionRepository.findByNameContainingIgnoreCase(name);
    }
    
    /**
     * Search submissions by address
     */
    @Transactional(readOnly = true)
    public List<FormSubmissionEntity> searchSubmissionsByAddress(String address) {
        return formSubmissionRepository.findByAddressContainingIgnoreCase(address);
    }
    
    /**
     * Find submissions by phone number
     */
    @Transactional(readOnly = true)
    public List<FormSubmissionEntity> findSubmissionsByPhoneNumber(String phoneNumber) {
        return formSubmissionRepository.findByPhoneNumber(phoneNumber);
    }
    
    /**
     * Get submissions within date range
     */
    @Transactional(readOnly = true)
    public List<FormSubmissionEntity> getSubmissionsBetweenDates(LocalDateTime startDate, LocalDateTime endDate) {
        return formSubmissionRepository.findSubmissionsBetweenDates(startDate, endDate);
    }
    
    /**
     * Get recent submissions (last N days)
     */
    @Transactional(readOnly = true)
    public List<FormSubmissionEntity> getRecentSubmissions(int days) {
        LocalDateTime sinceDate = LocalDateTime.now().minusDays(days);
        return formSubmissionRepository.findRecentSubmissions(sinceDate);
    }
    
    /**
     * Get submission statistics
     */
    @Transactional(readOnly = true)
    public SubmissionStats getSubmissionStats() {
        long totalSubmissions = formSubmissionRepository.count();
        long pendingSubmissions = formSubmissionRepository.countByStatus(SubmissionStatus.PENDING);
        long approvedSubmissions = formSubmissionRepository.countByStatus(SubmissionStatus.APPROVED);
        long rejectedSubmissions = formSubmissionRepository.countByStatus(SubmissionStatus.REJECTED);
        
        return new SubmissionStats(totalSubmissions, pendingSubmissions, approvedSubmissions, rejectedSubmissions);
    }
    
    /**
     * Bulk update status
     */
    public void bulkUpdateStatus(List<Long> submissionIds, SubmissionStatus status) {
        List<FormSubmissionEntity> submissions = formSubmissionRepository.findAllById(submissionIds);
        submissions.forEach(submission -> submission.setStatus(status));
        formSubmissionRepository.saveAll(submissions);
        
        log.info("Bulk updated {} submissions to status {}", submissions.size(), status);
        
        // 批量更新 Google Sheets 中的状态
        try {
            for (Long id : submissionIds) {
                googleSheetsService.updateSubmissionStatus(id, status.toString());
            }
        } catch (Exception e) {
            log.warn("Failed to bulk update status in Google Sheets", e);
        }
    }
    
    /**
     * 同步所有数据到 Google Sheets
     */
    @Transactional(readOnly = true)
    public void syncAllDataToGoogleSheets() {
        List<FormSubmissionEntity> allSubmissions = formSubmissionRepository.findAll();
        googleSheetsService.syncAllDataToSheets(allSubmissions);
        log.info("Synced {} submissions to Google Sheets", allSubmissions.size());
    }
    
    // Inner class for statistics
    public record SubmissionStats(
        long totalSubmissions,
        long pendingSubmissions,
        long approvedSubmissions,
        long rejectedSubmissions
    ) {}
}