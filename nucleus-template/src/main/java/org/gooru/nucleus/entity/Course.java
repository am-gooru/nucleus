package org.gooru.nucleus.entity;

import java.time.LocalDateTime;

/**
 * @author Sachin
 * 
 *         Used to store course
 */
public class Course {

  private String id;

  private String title;

  private String thumbnail;

  private SharingTypes sharingType;

  private String audience;

  private String metadata;

  private String collaborator;

  private String classList;

  private LocalDateTime publishDate;

  private String originalCreatorId;

  private String creatorId;

  private LocalDateTime created;

  private LocalDateTime modified;

  private LocalDateTime accessed;

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

  public String getThumbnail() {
    return thumbnail;
  }

  public void setThumbnail(String thumbnail) {
    this.thumbnail = thumbnail;
  }

  public SharingTypes getSharingType() {
    return sharingType;
  }

  public void setSharingType(SharingTypes sharingType) {
    this.sharingType = sharingType;
  }

  public String getAudience() {
    return audience;
  }

  public void setAudience(String audience) {
    this.audience = audience;
  }

  public String getMetadata() {
    return metadata;
  }

  public void setMetadata(String metadata) {
    this.metadata = metadata;
  }

  public String getCollaborator() {
    return collaborator;
  }

  public void setCollaborator(String collaborator) {
    this.collaborator = collaborator;
  }

  public String getClassList() {
    return classList;
  }

  public void setClassList(String classList) {
    this.classList = classList;
  }

  public LocalDateTime getPublishDate() {
    return publishDate;
  }

  public void setPublishDate(LocalDateTime publishDate) {
    this.publishDate = publishDate;
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

}
