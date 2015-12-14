package org.gooru.nucleus.entity;

/**
 * @author Sachin
 * 
 *         Used to store the course, unit, lesson and collection/assessment
 *         information
 */
public class CourseUnitLessonCollectionAssessment {

  private String id;

  private String courseId;

  private String unitId;

  private String lessonId;

  private String collectionId;

  private String assessmentId;

  private int sequenceId;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

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

  public String getCollectionId() {
    return collectionId;
  }

  public void setCollectionId(String collectionId) {
    this.collectionId = collectionId;
  }

  public String getAssessmentId() {
    return assessmentId;
  }

  public void setAssessmentId(String assessmentId) {
    this.assessmentId = assessmentId;
  }

  public int getSequenceId() {
    return sequenceId;
  }

  public void setSequenceId(int sequenceId) {
    this.sequenceId = sequenceId;
  }

}
