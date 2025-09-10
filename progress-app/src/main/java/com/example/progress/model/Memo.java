package com.example.progress.model;

public class Memo {
  private int memoId;
  private String memoType;     // TEXT
  private String memoTitle;
  private String memoCreated;  // 作成者や作成時刻など（文字列）
  private String memoDetail;   // 本文詳細（任意）
  private String memoUpdated;  // 更新時刻（null可）
  private String memoTags;     // カンマ区切りタグ（null可）
  private boolean memoPinned;  // ピン留め（一覧上部に表示）

  public Memo(int memoId, String memoType, String memoTitle, String memoCreated) {
    this(memoId, memoType, memoTitle, memoCreated, null, null, null, false);
  }

  public Memo(int memoId, String memoType, String memoTitle, String memoCreated, String memoDetail) {
    this(memoId, memoType, memoTitle, memoCreated, memoDetail, null, null, false);
  }

  public Memo(int memoId, String memoType, String memoTitle, String memoCreated,
              String memoDetail, String memoUpdated, String memoTags, boolean memoPinned) {
    this.memoId = memoId;
    this.memoType = memoType;
    this.memoTitle = memoTitle;
    this.memoCreated = memoCreated;
    this.memoDetail = memoDetail;
    this.memoUpdated = memoUpdated;
    this.memoTags = memoTags;
    this.memoPinned = memoPinned;
  }

  public int getId() { return memoId; }
  public int getMemoId() { return memoId; }
  public String getMemoType() { return memoType; }
  public String getMemoTitle() { return memoTitle; }
  public String getMemoCreated() { return memoCreated; }
  public String getMemoDetail() { return memoDetail; }
  public String getMemoUpdated() { return memoUpdated; }
  public String getMemoTags() { return memoTags; }
  public boolean isMemoPinned() { return memoPinned; }

  public void setId(int memoId) { this.memoId = memoId; }
  public void setMemoId(int memoId) { this.memoId = memoId; }
  public void setMemoType(String memoType) { this.memoType = memoType; }
  public void setMemoTitle(String memoTitle) { this.memoTitle = memoTitle; }
  public void setMemoCreated(String memoCreated) { this.memoCreated = memoCreated; }
  public void setMemoDetail(String memoDetail) { this.memoDetail = memoDetail; }
  public void setMemoUpdated(String memoUpdated) { this.memoUpdated = memoUpdated; }
  public void setMemoTags(String memoTags) { this.memoTags = memoTags; }
  public void setMemoPinned(boolean memoPinned) { this.memoPinned = memoPinned; }
}

