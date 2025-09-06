package com.example.progress.model;

import java.time.LocalDateTime;

public class Memo {
  private int id;
  private int classId;
  private String type;       // note | bug
  private String title;
  private String body;
  private String severity;   // optional (for bug)
  private String createdBy;
  private LocalDateTime createdAt;
  private String status;     // open | closed

  public Memo(int id, int classId, String type, String title, String body,
              String severity, String createdBy, LocalDateTime createdAt, String status) {
    this.id = id;
    this.classId = classId;
    this.type = type;
    this.title = title;
    this.body = body;
    this.severity = severity;
    this.createdBy = createdBy;
    this.createdAt = createdAt;
    this.status = status;
  }

  public int getId() { return id; }
  public int getClassId() { return classId; }
  public String getType() { return type; }
  public String getTitle() { return title; }
  public String getBody() { return body; }
  public String getSeverity() { return severity; }
  public String getCreatedBy() { return createdBy; }
  public LocalDateTime getCreatedAt() { return createdAt; }
  public String getStatus() { return status; }

  public void setId(int id) { this.id = id; }
  public void setClassId(int classId) { this.classId = classId; }
  public void setType(String type) { this.type = type; }
  public void setTitle(String title) { this.title = title; }
  public void setBody(String body) { this.body = body; }
  public void setSeverity(String severity) { this.severity = severity; }
  public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
  public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
  public void setStatus(String status) { this.status = status; }
}
