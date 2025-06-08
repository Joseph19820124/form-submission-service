package com.example.mapper;

import com.example.dto.*;
import com.example.entity.FormSubmissionEntity;
import com.example.service.FormSubmissionService.SubmissionStats;
import org.springframework.stereotype.Component;

@Component
public class FormSubmissionMapper {
    
    /**
     * Convert Entity to Response DTO
     */
    public FormSubmissionResponseDto toResponseDto(FormSubmissionEntity entity) {
        if (entity == null) {
            return null;
        }
        
        return new FormSubmissionResponseDto(
            entity.getId(),
            entity.getName(),
            entity.getEmail(),
            entity.getAddress(),
            entity.getPhoneNumber(),
            entity.getComments(),
            entity.getStatus(),
            entity.getCreatedAt(),
            entity.getUpdatedAt(),
            entity.getSubmittedByIp()
        );
    }
    
    /**
     * Convert Request DTO to Entity
     */
    public FormSubmissionEntity toEntity(FormSubmissionRequestDto dto) {
        if (dto == null) {
            return null;
        }
        
        FormSubmissionEntity entity = new FormSubmissionEntity();
        entity.setName(dto.getName());
        entity.setEmail(dto.getEmail());
        entity.setAddress(dto.getAddress());
        entity.setPhoneNumber(dto.getPhoneNumber());
        entity.setComments(dto.getComments());
        entity.setSubmittedByIp(dto.getSubmittedByIp());
        
        return entity;
    }
    
    /**
     * Update entity with request DTO data
     */
    public void updateEntityFromDto(FormSubmissionEntity entity, FormSubmissionRequestDto dto) {
        if (entity == null || dto == null) {
            return;
        }
        
        entity.setName(dto.getName());
        entity.setAddress(dto.getAddress());
        entity.setPhoneNumber(dto.getPhoneNumber());
        entity.setComments(dto.getComments());
        // Note: email is typically not updated after creation
        // entity.setEmail(dto.getEmail());
    }
    
    /**
     * Convert service stats to DTO stats
     */
    public SubmissionStatsDto toStatsDto(SubmissionStats stats) {
        if (stats == null) {
            return null;
        }
        
        double approvalRate = stats.totalSubmissions() > 0 ? 
            (double) stats.approvedSubmissions() / stats.totalSubmissions() * 100 : 0.0;
            
        double rejectionRate = stats.totalSubmissions() > 0 ? 
            (double) stats.rejectedSubmissions() / stats.totalSubmissions() * 100 : 0.0;
        
        return new SubmissionStatsDto(
            stats.totalSubmissions(),
            stats.pendingSubmissions(),
            stats.approvedSubmissions(),
            stats.rejectedSubmissions(),
            0L, // processing submissions - add to service if needed
            Math.round(approvalRate * 100.0) / 100.0,
            Math.round(rejectionRate * 100.0) / 100.0
        );
    }
}

// Alternative using MapStruct (add dependency: org.mapstruct:mapstruct:1.5.5.Final)
/*
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface FormSubmissionMapperStruct {
    
    FormSubmissionMapperStruct INSTANCE = Mappers.getMapper(FormSubmissionMapperStruct.class);
    
    FormSubmissionResponseDto toResponseDto(FormSubmissionEntity entity);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    FormSubmissionEntity toEntity(FormSubmissionRequestDto dto);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "email", ignore = true) // Don't update email
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntityFromDto(@MappingTarget FormSubmissionEntity entity, FormSubmissionRequestDto dto);
}
*/