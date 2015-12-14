package org.gooru.nucleus.entity;

import java.time.LocalDateTime;

/**
 * @author Sachin
 * 
 *         Used to store taxonomy course information
 */
public class TaxonomyCourse {

  private int id;

  private int subjectId;

  private String code;

  private String name;

  private String description;

  private String grades;

  private int sequenceId;

  private String creatorId;

  private LocalDateTime created;

  private LocalDateTime modified;

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public int getSubjectId() {
    return subjectId;
  }

  public void setSubjectId(int subjectId) {
    this.subjectId = subjectId;
  }

  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getGrades() {
    return grades;
  }

  public void setGrades(String grades) {
    this.grades = grades;
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

}
