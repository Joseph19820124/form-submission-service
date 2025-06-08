package com.example.controller;

import com.example.dto.*;
import com.example.entity.FormSubmissionEntity;
import com.example.entity.FormSubmissionEntity.SubmissionStatus;
import com.example.mapper.FormSubmissionMapper;
import com.example.service.FormSubmissionService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/form-submissions")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*") // Configure as needed for your frontend
public class FormSubmissionController {
    
    private final FormSubmissionService formSubmissionService;
    private final FormSubmissionMapper mapper;
    
    /**
     * Submit a new form
     */
    @PostMapping
    public ResponseEntity<FormSubmissionResponseDto> submitForm(
            @Valid @RequestBody FormSubmissionRequestDto requestDto,
            HttpServletRequest request) {
        
        try {
            // Get client IP address
            String clientIp = getClientIpAddress(request);
            requestDto.setSubmittedByIp(clientIp);
            
            FormSubmissionEntity entity = mapper.toEntity(requestDto);
            FormSubmissionEntity savedEntity = formSubmissionService.createSubmission(entity);
            FormSubmissionResponseDto responseDto = mapper.toResponseDto(savedEntity);
            
            log.info("Form submitted successfully with ID: {}", savedEntity.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
            
        } catch (IllegalArgumentException e) {
            log.warn("Form submission failed: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Unexpected error during form submission", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Get submission by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<FormSubmissionResponseDto> getSubmission(@PathVariable Long id) {
        return formSubmissionService.getSubmissionById(id)
            .map(mapper::toResponseDto)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * Get all submissions with pagination
     */
    @GetMapping
    public ResponseEntity<Page<FormSubmissionResponseDto>> getAllSubmissions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection) {
        
        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<FormSubmissionEntity> submissions = formSubmissionService.getAllSubmissions(pageable);
        Page<FormSubmissionResponseDto> responseDtos = submissions.map(mapper::toResponseDto);
        
        return ResponseEntity.ok(responseDtos);
    }
    
    /**
     * Get submissions by status
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<Page<FormSubmissionResponseDto>> getSubmissionsByStatus(
            @PathVariable SubmissionStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<FormSubmissionEntity> submissions = formSubmissionService.getSubmissionsByStatus(status, pageable);
        Page<FormSubmissionResponseDto> responseDtos = submissions.map(mapper::toResponseDto);
        
        return ResponseEntity.ok(responseDtos);
    }
    
    /**
     * Update submission
     */
    @PutMapping("/{id}")
    public ResponseEntity<FormSubmissionResponseDto> updateSubmission(
            @PathVariable Long id,
            @Valid @RequestBody FormSubmissionRequestDto requestDto) {
        
        try {
            FormSubmissionEntity entity = mapper.toEntity(requestDto);
            FormSubmissionEntity updatedEntity = formSubmissionService.updateSubmission(id, entity);
            FormSubmissionResponseDto responseDto = mapper.toResponseDto(updatedEntity);
            
            return ResponseEntity.ok(responseDto);
            
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Error updating submission with ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Update submission status only
     */
    @PatchMapping("/{id}/status")
    public ResponseEntity<FormSubmissionResponseDto> updateSubmissionStatus(
            @PathVariable Long id,
            @Valid @RequestBody StatusUpdateDto statusDto) {
        
        try {
            FormSubmissionEntity updatedEntity = formSubmissionService.updateSubmissionStatus(id, statusDto.getStatus());
            FormSubmissionResponseDto responseDto = mapper.toResponseDto(updatedEntity);
            
            return ResponseEntity.ok(responseDto);
            
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Error updating submission status for ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Delete submission
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSubmission(@PathVariable Long id) {
        try {
            formSubmissionService.deleteSubmission(id);
            return ResponseEntity.noContent().build();
            
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Error deleting submission with ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Search submissions by name
     */
    @GetMapping("/search/name")
    public ResponseEntity<List<FormSubmissionResponseDto>> searchSubmissionsByName(
            @RequestParam String name) {
        
        List<FormSubmissionEntity> submissions = formSubmissionService.searchSubmissionsByName(name);
        List<FormSubmissionResponseDto> responseDtos = submissions.stream()
            .map(mapper::toResponseDto)
            .toList();
        
        return ResponseEntity.ok(responseDtos);
    }
    
    /**
     * Search submissions by address
     */
    @GetMapping("/search/address")
    public ResponseEntity<List<FormSubmissionResponseDto>> searchSubmissionsByAddress(
            @RequestParam String address) {
        
        List<FormSubmissionEntity> submissions = formSubmissionService.searchSubmissionsByAddress(address);
        List<FormSubmissionResponseDto> responseDtos = submissions.stream()
            .map(mapper::toResponseDto)
            .toList();
        
        return ResponseEntity.ok(responseDtos);
    }
    
    /**
     * Find submissions by phone number
     */
    @GetMapping("/search/phone")
    public ResponseEntity<List<FormSubmissionResponseDto>> searchSubmissionsByPhone(
            @RequestParam String phoneNumber) {
        
        List<FormSubmissionEntity> submissions = formSubmissionService.findSubmissionsByPhoneNumber(phoneNumber);
        List<FormSubmissionResponseDto> responseDtos = submissions.stream()
            .map(mapper::toResponseDto)
            .toList();
        
        return ResponseEntity.ok(responseDtos);
    }
    
    /**
     * Get submission statistics
     */
    @GetMapping("/stats")
    public ResponseEntity<SubmissionStatsDto> getSubmissionStats() {
        var stats = formSubmissionService.getSubmissionStats();
        SubmissionStatsDto statsDto = mapper.toStatsDto(stats);
        return ResponseEntity.ok(statsDto);
    }
    
    /**
     * Bulk update submission status
     */
    @PatchMapping("/bulk-update")
    public ResponseEntity<Void> bulkUpdateStatus(@Valid @RequestBody BulkUpdateDto bulkUpdateDto) {
        try {
            formSubmissionService.bulkUpdateStatus(bulkUpdateDto.getSubmissionIds(), bulkUpdateDto.getStatus());
            return ResponseEntity.ok().build();
            
        } catch (Exception e) {
            log.error("Error during bulk update", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Get recent submissions (last N days)
     */
    @GetMapping("/recent")
    public ResponseEntity<List<FormSubmissionResponseDto>> getRecentSubmissions(
            @RequestParam(defaultValue = "7") int days) {
        
        List<FormSubmissionEntity> submissions = formSubmissionService.getRecentSubmissions(days);
        List<FormSubmissionResponseDto> responseDtos = submissions.stream()
            .map(mapper::toResponseDto)
            .toList();
        
        return ResponseEntity.ok(responseDtos);
    }
    
    /**
     * 手动同步所有数据到 Google Sheets
     */
    @PostMapping("/sync-to-sheets")
    public ResponseEntity<Map<String, String>> syncToGoogleSheets() {
        try {
            formSubmissionService.syncAllDataToGoogleSheets();
            
            Map<String, String> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "所有数据已成功同步到 Google Sheets");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("同步到 Google Sheets 失败", e);
            
            Map<String, String> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", "同步失败: " + e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * Helper method to get client IP address
     */
    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }
        
        return request.getRemoteAddr();
    }
}