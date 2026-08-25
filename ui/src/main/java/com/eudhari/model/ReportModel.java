package com.eudhari.model;

public class ReportModel {
    private String reportId;
    private String reportType;
    private String generatedDate;
    private String generatedBy;
    private String status;
    private String fileSize;

    public ReportModel(String reportId, String reportType, String generatedDate, String generatedBy, String status, String fileSize) {
        this.reportId = reportId;
        this.reportType = reportType;
        this.generatedDate = generatedDate;
        this.generatedBy = generatedBy;
        this.status = status;
        this.fileSize = fileSize;
    }

    public String getReportId() { return reportId; }
    public void setReportId(String reportId) { this.reportId = reportId; }

    public String getReportType() { return reportType; }
    public void setReportType(String reportType) { this.reportType = reportType; }

    public String getGeneratedDate() { return generatedDate; }
    public void setGeneratedDate(String generatedDate) { this.generatedDate = generatedDate; }

    public String getGeneratedBy() { return generatedBy; }
    public void setGeneratedBy(String generatedBy) { this.generatedBy = generatedBy; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getFileSize() { return fileSize; }
    public void setFileSize(String fileSize) { this.fileSize = fileSize; }
}
