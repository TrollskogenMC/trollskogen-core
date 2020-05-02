package com.github.hornta.trollskogen_core.bans;

import java.time.Instant;

public class Ban {
  private final int id;
  private Instant issuedDate;
  private String reason;
  private Instant expiryDate;
  private boolean isCancelled;
  private int userId;
  private Integer issuedBy;
  private Integer cancelledBy;
  private Instant cancelledDate;

  Ban(int id, Instant issuedDate, String reason, Instant expiryDate, boolean isCancelled, int userId, Integer issuedBy, Integer cancelledBy, Instant cancelledDate) {
    this.id = id;
    this.issuedDate = issuedDate;
    this.reason = reason;
    this.expiryDate = expiryDate;
    this.isCancelled = isCancelled;
    this.userId = userId;
    this.issuedBy = issuedBy;
    this.cancelledBy = cancelledBy;
    this.cancelledDate = cancelledDate;
  }

  public int getId() {
    return id;
  }

  public Instant getIssuedDate() {
    return issuedDate;
  }

  public String getIssuedDateFormatted() {
    if(issuedDate == null) {
      return null;
    }
    return BanManager.formatter.format(issuedDate);
  }

  public String getReason() {
    return reason;
  }

  public Instant getExpiryDate() {
    return expiryDate;
  }

  public String getExpiryDateFormatted() {
    if(expiryDate == null) {
      return null;
    }
    return BanManager.formatter.format(expiryDate);
  }

  public boolean isCancelled() {
    return isCancelled;
  }

  public int getUserId() {
    return userId;
  }

  public Integer getIssuedBy() {
    return issuedBy;
  }

  public Integer getCancelledBy() {
    return cancelledBy;
  }

  public Instant getCancelledDate() {
    return cancelledDate;
  }

  public String getCancelledDateFormatted() {
    if(cancelledDate == null) {
      return null;
    }
    return BanManager.formatter.format(cancelledDate);
  }

  public void setCancelled(boolean cancelled) {
    isCancelled = cancelled;
  }

  public void setCancelledDate(Instant cancelledDate) {
    this.cancelledDate = cancelledDate;
  }

  public void setCancelledBy(int cancelledBy) {
    this.cancelledBy = cancelledBy;
  }

  public void setExpiryDate(Instant expiryDate) {
    this.expiryDate = expiryDate;
  }

  public void setIssuedDate(Instant issuedDate) {
    this.issuedDate = issuedDate;
  }

  public void setIssuedBy(int issuedBy) {
    this.issuedBy = issuedBy;
  }

  public void setReason(String reason) {
    this.reason = reason;
  }

  public void setUserId(int userId) {
    this.userId = userId;
  }
}
