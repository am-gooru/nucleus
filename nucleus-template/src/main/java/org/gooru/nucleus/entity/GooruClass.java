package org.gooru.nucleus.entity;

import java.time.LocalDateTime;

/**
 * @author Sachin
 * 
 *         Used to store class information
 */
public class GooruClass {

  private String id;

  private String title;

  private String description;

  private String greeting;

  private String grade;

  private ClassVisibility visibility;

  private String coverImage;

  private String code;

  private int minScore;

  private LocalDateTime endTime;

  private String courseId;

  private boolean isDeleted;

  private String collaborator;

  private String creatorId;

  private LocalDateTime created;

  private LocalDateTime modified;

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

  public String getGreeting() {
    return greeting;
  }

  public void setGreeting(String greeting) {
    this.greeting = greeting;
  }

  public String getGrade() {
    return grade;
  }

  public void setGrade(String grade) {
    this.grade = grade;
  }

  public ClassVisibility getVisibility() {
    return visibility;
  }

  public void setVisibility(ClassVisibility visibility) {
    this.visibility = visibility;
  }

  public String getCoverImage() {
    return coverImage;
  }

  public void setCoverImage(String coverImage) {
    this.coverImage = coverImage;
  }

  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public int getMinScore() {
    return minScore;
  }

  public void setMinScore(int minScore) {
    this.minScore = minScore;
  }

  public LocalDateTime getEndTime() {
    return endTime;
  }

  public void setEndTime(LocalDateTime endTime) {
    this.endTime = endTime;
  }

  public String getCourseId() {
    return courseId;
  }

  public void setCourseId(String courseId) {
    this.courseId = courseId;
  }

  public boolean isDeleted() {
    return isDeleted;
  }

  public void setDeleted(boolean isDeleted) {
    this.isDeleted = isDeleted;
  }

  public String getCollaborator() {
    return collaborator;
  }

  public void setCollaborator(String collaborator) {
    this.collaborator = collaborator;
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

}
