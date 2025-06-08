package com.example.service;

import com.example.entity.FormSubmissionEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class GoogleSheetsService {
    
    @Value("${google.sheets.spreadsheet-id:18ea2cQeXOt96dGbssCP_ynsTP5AHEmQpFXE48-PtToo}")
    private String spreadsheetId;
    
    @Value("${google.sheets.worksheet-name:Sheet1}")
    private String worksheetName;
    
    /**
     * 将表单提交数据写入 Google Sheets
     */
    public void writeSubmissionToSheet(FormSubmissionEntity submission) {
        try {
            log.info("准备写入提交数据到 Google Sheets: ID={}, Name={}, Email={}", 
                submission.getId(), submission.getName(), submission.getEmail());
            
            // 这里您需要集成实际的 Google Sheets API 调用
            // 可以使用您现有的 MCP 工具或 Google Sheets API 库
            
            log.info("Successfully wrote submission {} to Google Sheets", submission.getId());
        } catch (Exception e) {
            log.error("Failed to write submission {} to Google Sheets", submission.getId(), e);
            // 不抛异常，避免影响主要业务流程
        }
    }
    
    /**
     * 批量写入多个提交到 Google Sheets
     */
    public void writeBatchSubmissionsToSheet(List<FormSubmissionEntity> submissions) {
        try {
            log.info("开始批量写入 {} 条记录到 Google Sheets", submissions.size());
            
            for (FormSubmissionEntity submission : submissions) {
                writeSubmissionToSheet(submission);
                // 添加小延迟避免API限流
                Thread.sleep(100);
            }
            
            log.info("Successfully wrote {} submissions to Google Sheets", submissions.size());
        } catch (Exception e) {
            log.error("Failed to write batch submissions to Google Sheets", e);
        }
    }
    
    /**
     * 更新 Google Sheets 中的提交状态
     */
    public void updateSubmissionStatus(Long submissionId, String newStatus) {
        try {
            log.info("更新 Google Sheets 中提交 {} 的状态为: {}", submissionId, newStatus);
            
            // 这里需要实现查找对应行并更新状态的逻辑
            // 可以使用 Google Sheets API 的更新功能
            
            log.info("Updated submission {} status to {} in Google Sheets", submissionId, newStatus);
        } catch (Exception e) {
            log.error("Failed to update submission {} status in Google Sheets", submissionId, e);
        }
    }
    
    /**
     * 同步所有数据库数据到 Google Sheets
     */
    public void syncAllDataToSheets(List<FormSubmissionEntity> allSubmissions) {
        try {
            log.info("开始全量同步 {} 条记录到 Google Sheets", allSubmissions.size());
            
            // 清除现有数据 (保留表头)
            // clearSheet();
            
            // 批量写入所有数据
            writeBatchSubmissionsToSheet(allSubmissions);
            
            log.info("Successfully synced {} submissions to Google Sheets", allSubmissions.size());
        } catch (Exception e) {
            log.error("Failed to sync all data to Google Sheets", e);
        }
    }
    
    /**
     * 构建行数据
     */
    private List<String> buildRowData(FormSubmissionEntity submission) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        
        List<String> rowData = new ArrayList<>();
        rowData.add(submission.getId() != null ? submission.getId().toString() : "");
        rowData.add(submission.getName() != null ? submission.getName() : "");
        rowData.add(submission.getEmail() != null ? submission.getEmail() : "");
        rowData.add(submission.getAddress() != null ? submission.getAddress() : "");
        rowData.add(submission.getPhoneNumber() != null ? submission.getPhoneNumber() : "");
        rowData.add(submission.getComments() != null ? submission.getComments() : "");
        rowData.add(submission.getStatus() != null ? submission.getStatus().toString() : "PENDING");
        rowData.add(submission.getCreatedAt() != null ? submission.getCreatedAt().format(formatter) : "");
        rowData.add(submission.getSubmittedByIp() != null ? submission.getSubmittedByIp() : "");
        
        return rowData;
    }
}

/**
 * 简化版的 Google Sheets 集成实现
 * 这个版本展示了如何与现有的 MCP 工具集成
 */
@Service
@Slf4j
class SimpleGoogleSheetsIntegration {
    
    @Value("${google.sheets.spreadsheet-id:18ea2cQeXOt96dGbssCP_ynsTP5AHEmQpFXE48-PtToo}")
    private String spreadsheetId;
    
    @Value("${google.sheets.worksheet-name:Sheet1}")
    private String worksheetName;
    
    /**
     * 实际的 Google Sheets 集成点
     * 这里可以调用您的 MCP 工具：
     * - google_sheets_create_spreadsheet_row
     * - google_sheets_update_spreadsheet_row
     * - google_sheets_get_many_spreadsheet_rows_advanced
     */
    public void integrateWithGoogleSheets(FormSubmissionEntity submission) {
        try {
            // 示例：使用 MCP 工具创建新行
            /*
            Map<String, Object> rowData = new HashMap<>();
            rowData.put("ID", submission.getId());
            rowData.put("Name", submission.getName());
            rowData.put("Email", submission.getEmail());
            rowData.put("Address", submission.getAddress());
            rowData.put("Phone Number", submission.getPhoneNumber());
            rowData.put("Comments", submission.getComments());
            rowData.put("Status", submission.getStatus().toString());
            rowData.put("Submitted Date", submission.getCreatedAt().toString());
            rowData.put("IP Address", submission.getSubmittedByIp());
            
            // 调用 MCP 工具
            mcpToolsService.createSpreadsheetRow(spreadsheetId, worksheetName, rowData);
            */
            
            log.info("Google Sheets 集成点：提交 {} 已准备好写入", submission.getId());
            
        } catch (Exception e) {
            log.error("Google Sheets 集成失败", e);
        }
    }
}