package org.gooru.nucleus.entity;

import java.time.LocalDateTime;

/**
 * @author Sachin
 * 
 *         Used to store taxonomy subject information
 */
public class TaxonomySubject {

  private int id;

  private String code;

  private String name;

  private String description;

  private int sequenceId;

  private SubjectClassificationType classification;

  private String creatorId;

  private LocalDateTime created;

  private LocalDateTime modified;

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
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

  public int getSequenceId() {
    return sequenceId;
  }

  public void setSequenceId(int sequenceId) {
    this.sequenceId = sequenceId;
  }

  public SubjectClassificationType getClassification() {
    return classification;
  }

  public void setClassification(SubjectClassificationType classification) {
    this.classification = classification;
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
