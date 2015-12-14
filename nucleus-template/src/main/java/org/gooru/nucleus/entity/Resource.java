package org.gooru.nucleus.entity;

import java.time.LocalDateTime;

/**
 * @author Sachin
 * 
 *         Used to store the Resourse
 */
public class Resource {

  private String id;

  private String title;

  private String description;

  private ResourceFormat resourceFormat;

  private String thumbnail;

  private String url;

  private SharingTypes sharingType;

  private LocalDateTime created;

  private LocalDateTime modified;

  private LocalDateTime accessed;

  private String originalCreatorId;

  private String creatorId;

  private LocalDateTime publishDate;

  private boolean isFrameBreaker;

  private boolean isBroken;

  private boolean isDeleted;

  private String flagReport;

  private String metadata;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public ResourceFormat getResourceFormat() {
    return resourceFormat;
  }

  public void setResourceFormat(ResourceFormat resourceFormat) {
    this.resourceFormat = resourceFormat;
  }

  public String getThumbnail() {
    return thumbnail;
  }

  public void setThumbnail(String thumbnail) {
    this.thumbnail = thumbnail;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public SharingTypes getSharingType() {
    return sharingType;
  }

  public void setSharingType(SharingTypes sharingType) {
    this.sharingType = sharingType;
  }

  public LocalDateTime getCreated() {
    return created;
  }

  public void setCreated(LocalDateTime created) {
    this.created = created;
  }

  public LocalDateTime getModified() {
    return modified;
  }

  public void setModified(LocalDateTime modified) {
    this.modified = modified;
  }

  public LocalDateTime getAccessed() {
    return accessed;
  }

  public void setAccessed(LocalDateTime accessed) {
    this.accessed = accessed;
  }

  public String getOriginalCreatorId() {
    return originalCreatorId;
  }

  public void setOriginalCreatorId(String originalCreatorId) {
    this.originalCreatorId = originalCreatorId;
  }

  public String getCreatorId() {
    return creatorId;
  }

  public void setCreatorId(String creatorId) {
    this.creatorId = creatorId;
  }

  public LocalDateTime getPublishDate() {
    return publishDate;
  }

  public void setPublishDate(LocalDateTime publishDate) {
    this.publishDate = publishDate;
  }

  public boolean isFrameBreaker() {
    return isFrameBreaker;
  }

  public void setFrameBreaker(boolean isFrameBreaker) {
    this.isFrameBreaker = isFrameBreaker;
  }

  public boolean isBroken() {
    return isBroken;
  }

  public void setBroken(boolean isBroken) {
    this.isBroken = isBroken;
  }

  public boolean isDeleted() {
    return isDeleted;
  }

  public void setDeleted(boolean isDeleted) {
    this.isDeleted = isDeleted;
  }

  public String getFlagReport() {
    return flagReport;
  }

  public void setFlagReport(String flagReport) {
    this.flagReport = flagReport;
  }

  public String getMetadata() {
    return metadata;
  }

  public void setMetadata(String metadata) {
    this.metadata = metadata;
  }

}
