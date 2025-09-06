package com.example.progress.model;

import java.time.LocalDate;

public class ClassEntry {
  private int id;
  private String packageName;
  private String className;
  private String author;
  private String status;   // todo / doing / done
  private int progress;    // 0-100
  private LocalDate lastUpdated;

  public ClassEntry(int id, String packageName, String className, String author,
                    String status, int progress, LocalDate lastUpdated) {
    this.id = id;
    this.packageName = packageName;
    this.className = className;
    this.author = author;
    this.status = status;
    this.progress = progress;
    this.lastUpdated = lastUpdated;
  }

  public int getId() { return id; }
  public String getPackageName() { return packageName; }
  public String getClassName() { return className; }
  public String getAuthor() { return author; }
  public String getStatus() { return status; }
  public int getProgress() { return progress; }
  public LocalDate getLastUpdated() { return lastUpdated; }

  public void setId(int id) { this.id = id; }
  public void setPackageName(String packageName) { this.packageName = packageName; }
  public void setClassName(String className) { this.className = className; }
  public void setAuthor(String author) { this.author = author; }
  public void setStatus(String status) { this.status = status; }
  public void setProgress(int progress) { this.progress = progress; }
  public void setLastUpdated(LocalDate lastUpdated) { this.lastUpdated = lastUpdated; }
}
