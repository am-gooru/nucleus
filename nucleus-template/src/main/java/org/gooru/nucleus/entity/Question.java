package org.gooru.nucleus.entity;

import java.time.LocalDateTime;

/**
 * @author Sachin
 * 
 *         Used to store Question
 */
public class Question {

  private String id;

  private QuestionType questionType;

  private String title;

  private String explanation;

  private LocalDateTime publishDate;

  private String hint;

  private String detail;

  private String answer;

  private String metadata;

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

  public QuestionType getQuestionType() {
    return questionType;
  }

  public void setQuestionType(QuestionType questionType) {
    this.questionType = questionType;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getExplanation() {
    return explanation;
  }

  public void setExplanation(String explanation) {
    this.explanation = explanation;
  }

  public LocalDateTime getPublishDate() {
    return publishDate;
  }

  public void setPublishDate(LocalDateTime publishDate) {
    this.publishDate = publishDate;
  }

  public String getHint() {
    return hint;
  }

  public void setHint(String hint) {
    this.hint = hint;
  }

  public String getDetail() {
    return detail;
  }

  public void setDetail(String detail) {
    this.detail = detail;
  }

  public String getAnswer() {
    return answer;
  }

  public void setAnswer(String answer) {
    this.answer = answer;
  }

  public String getMetadata() {
    return metadata;
  }

  public void setMetadata(String metadata) {
    this.metadata = metadata;
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
