package org.gooru.nucleus.entity;

import java.time.LocalDateTime;

/**
 * @author Sachin
 * 
 *         Used to store metadata reference type values (lookup)
 */
public class MetadataReference {
  
  private int id;

  private MetadataReferenceType type;

  private String name;

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

  public MetadataReferenceType getType() {
    return type;
  }

  public void setType(MetadataReferenceType type) {
    this.type = type;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
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
