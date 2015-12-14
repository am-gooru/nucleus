/**
 * 
 */
package org.gooru.nucleus.entity;

import java.time.LocalDateTime;

/**
 * @author Sachin
 *
 *         Used to store assessment item
 */
public class AssessmentItem {

  private String id;

  private String assessmentId;

  private String questionId;

  private String sequenceId;

  private String narration;

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

  public String getAssessmentId() {
    return assessmentId;
  }

  public void setAssessmentId(String assessmentId) {
    this.assessmentId = assessmentId;
  }

  public String getQuestionId() {
    return questionId;
  }

  public void setQuestionId(String questionId) {
    this.questionId = questionId;
  }

  public String getSequenceId() {
    return sequenceId;
  }

  public void setSequenceId(String sequenceId) {
    this.sequenceId = sequenceId;
  }

  public String getNarration() {
    return narration;
  }

  public void setNarration(String narration) {
    this.narration = narration;
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
