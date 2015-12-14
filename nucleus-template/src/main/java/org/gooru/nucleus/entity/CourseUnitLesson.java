package org.gooru.nucleus.entity;

import java.time.LocalDateTime;

/**
 * @author Sachin
 * 
 *         Used to store the course, unit and lesson information
 */
public class CourseUnitLesson {

  private String courseId;

  private String unitId;

  private String lessonId;

  private String title;

  private String metadata;

  private int sequenceId;

  private String creatorId;

  private LocalDateTime created;

  private LocalDateTime modified;

  private LocalDateTime accessed;

  public String getCourseId() {
    return courseId;
  }

  public void setCourseId(String courseId) {
    this.courseId = courseId;
  }

  public String getUnitId() {
    return unitId;
  }

  public void setUnitId(String unitId) {
    this.unitId = unitId;
  }

  public String getLessonId() {
    return lessonId;
  }

  public void setLessonId(String lessonId) {
    this.lessonId = lessonId;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getMetadata() {
    return metadata;
  }

  public void setMetadata(String metadata) {
    this.metadata = metadata;
  }

  public int getSequenceId() {
    return sequenceId;
  }

  public void setSequenceId(int sequenceId) {
    this.sequenceId = sequenceId;
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
