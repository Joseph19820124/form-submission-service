package com.example.repository;

import com.example.entity.FormSubmissionEntity;
import com.example.entity.FormSubmissionEntity.SubmissionStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface FormSubmissionRepository extends JpaRepository<FormSubmissionEntity, Long> {
    
    // Find by email
    Optional<FormSubmissionEntity> findByEmail(String email);
    
    // Check if email exists
    boolean existsByEmail(String email);
    
    // Find by status
    List<FormSubmissionEntity> findByStatus(SubmissionStatus status);
    
    // Find by status with pagination
    Page<FormSubmissionEntity> findByStatus(SubmissionStatus status, Pageable pageable);
    
    // Find submissions within date range
    @Query("SELECT f FROM FormSubmissionEntity f WHERE f.createdAt BETWEEN :startDate AND :endDate")
    List<FormSubmissionEntity> findSubmissionsBetweenDates(
        @Param("startDate") LocalDateTime startDate, 
        @Param("endDate") LocalDateTime endDate
    );
    
    // Find submissions by name containing (case insensitive)
    @Query("SELECT f FROM FormSubmissionEntity f WHERE LOWER(f.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<FormSubmissionEntity> findByNameContainingIgnoreCase(@Param("name") String name);
    
    // Find submissions by address containing (case insensitive)
    @Query("SELECT f FROM FormSubmissionEntity f WHERE LOWER(f.address) LIKE LOWER(CONCAT('%', :address, '%'))")
    List<FormSubmissionEntity> findByAddressContainingIgnoreCase(@Param("address") String address);
    
    // Find submissions by phone number
    List<FormSubmissionEntity> findByPhoneNumber(String phoneNumber);
    
    // Count submissions by status
    @Query("SELECT COUNT(f) FROM FormSubmissionEntity f WHERE f.status = :status")
    long countByStatus(@Param("status") SubmissionStatus status);
    
    // Find recent submissions (last N days)
    @Query("SELECT f FROM FormSubmissionEntity f WHERE f.createdAt >= :sinceDate ORDER BY f.createdAt DESC")
    List<FormSubmissionEntity> findRecentSubmissions(@Param("sinceDate") LocalDateTime sinceDate);
    
    // Custom query to find submissions with specific criteria
    @Query("SELECT f FROM FormSubmissionEntity f WHERE " +
           "(:name IS NULL OR LOWER(f.name) LIKE LOWER(CONCAT('%', :name, '%'))) AND " +
           "(:email IS NULL OR f.email = :email) AND " +
           "(:address IS NULL OR LOWER(f.address) LIKE LOWER(CONCAT('%', :address, '%'))) AND " +
           "(:phoneNumber IS NULL OR f.phoneNumber = :phoneNumber) AND " +
           "(:status IS NULL OR f.status = :status) AND " +
           "(:fromDate IS NULL OR f.createdAt >= :fromDate) AND " +
           "(:toDate IS NULL OR f.createdAt <= :toDate)")
    Page<FormSubmissionEntity> findSubmissionsWithFilters(
        @Param("name") String name,
        @Param("email") String email,
        @Param("address") String address,
        @Param("phoneNumber") String phoneNumber,
        @Param("status") SubmissionStatus status,
        @Param("fromDate") LocalDateTime fromDate,
        @Param("toDate") LocalDateTime toDate,
        Pageable pageable
    );
    
    // Delete old submissions (for data cleanup)
    @Query("DELETE FROM FormSubmissionEntity f WHERE f.createdAt < :cutoffDate")
    void deleteSubmissionsOlderThan(@Param("cutoffDate") LocalDateTime cutoffDate);
}