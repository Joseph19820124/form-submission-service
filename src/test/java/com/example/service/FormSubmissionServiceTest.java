package com.example.service;

import com.example.entity.FormSubmissionEntity;
import com.example.entity.FormSubmissionEntity.SubmissionStatus;
import com.example.repository.FormSubmissionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FormSubmissionServiceTest {

    @Mock
    private FormSubmissionRepository formSubmissionRepository;
    
    @Mock
    private GoogleSheetsService googleSheetsService;

    @InjectMocks
    private FormSubmissionService formSubmissionService;

    private FormSubmissionEntity testSubmission;

    @BeforeEach
    void setUp() {
        testSubmission = new FormSubmissionEntity();
        testSubmission.setId(1L);
        testSubmission.setName("John Doe");
        testSubmission.setEmail("john.doe@example.com");
        testSubmission.setAddress("123 Main St, New York, NY 10001");
        testSubmission.setPhoneNumber("+1-555-0123");
        testSubmission.setComments("Test comment");
        testSubmission.setStatus(SubmissionStatus.PENDING);
    }

    @Test
    void createSubmission_Success() {
        // Given
        when(formSubmissionRepository.existsByEmail(testSubmission.getEmail())).thenReturn(false);
        when(formSubmissionRepository.save(any(FormSubmissionEntity.class))).thenReturn(testSubmission);
        doNothing().when(googleSheetsService).writeSubmissionToSheet(any(FormSubmissionEntity.class));

        // When
        FormSubmissionEntity result = formSubmissionService.createSubmission(testSubmission);

        // Then
        assertNotNull(result);
        assertEquals("John Doe", result.getName());
        assertEquals("john.doe@example.com", result.getEmail());
        assertEquals(SubmissionStatus.PENDING, result.getStatus());
        
        verify(formSubmissionRepository, times(1)).existsByEmail(testSubmission.getEmail());
        verify(formSubmissionRepository, times(1)).save(testSubmission);
        verify(googleSheetsService, times(1)).writeSubmissionToSheet(testSubmission);
    }

    @Test
    void createSubmission_EmailAlreadyExists_ThrowsException() {
        // Given
        when(formSubmissionRepository.existsByEmail(testSubmission.getEmail())).thenReturn(true);

        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> formSubmissionService.createSubmission(testSubmission)
        );
        
        assertEquals("Email already exists: john.doe@example.com", exception.getMessage());
        verify(formSubmissionRepository, never()).save(any());
        verify(googleSheetsService, never()).writeSubmissionToSheet(any());
    }

    @Test
    void getSubmissionById_Found() {
        // Given
        when(formSubmissionRepository.findById(1L)).thenReturn(Optional.of(testSubmission));

        // When
        Optional<FormSubmissionEntity> result = formSubmissionService.getSubmissionById(1L);

        // Then
        assertTrue(result.isPresent());
        assertEquals(testSubmission, result.get());
    }

    @Test
    void getSubmissionById_NotFound() {
        // Given
        when(formSubmissionRepository.findById(1L)).thenReturn(Optional.empty());

        // When
        Optional<FormSubmissionEntity> result = formSubmissionService.getSubmissionById(1L);

        // Then
        assertFalse(result.isPresent());
    }

    @Test
    void updateSubmissionStatus_Success() {
        // Given
        when(formSubmissionRepository.findById(1L)).thenReturn(Optional.of(testSubmission));
        when(formSubmissionRepository.save(any(FormSubmissionEntity.class))).thenReturn(testSubmission);
        doNothing().when(googleSheetsService).updateSubmissionStatus(anyLong(), anyString());

        // When
        FormSubmissionEntity result = formSubmissionService.updateSubmissionStatus(1L, SubmissionStatus.APPROVED);

        // Then
        assertEquals(SubmissionStatus.APPROVED, result.getStatus());
        verify(formSubmissionRepository, times(1)).save(testSubmission);
        verify(googleSheetsService, times(1)).updateSubmissionStatus(1L, "APPROVED");
    }

    @Test
    void updateSubmissionStatus_NotFound_ThrowsException() {
        // Given
        when(formSubmissionRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> formSubmissionService.updateSubmissionStatus(1L, SubmissionStatus.APPROVED)
        );
        
        assertEquals("Submission not found with id: 1", exception.getMessage());
    }

    @Test
    void deleteSubmission_Success() {
        // Given
        when(formSubmissionRepository.existsById(1L)).thenReturn(true);

        // When
        formSubmissionService.deleteSubmission(1L);

        // Then
        verify(formSubmissionRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteSubmission_NotFound_ThrowsException() {
        // Given
        when(formSubmissionRepository.existsById(1L)).thenReturn(false);

        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> formSubmissionService.deleteSubmission(1L)
        );
        
        assertEquals("Submission not found with id: 1", exception.getMessage());
    }
}