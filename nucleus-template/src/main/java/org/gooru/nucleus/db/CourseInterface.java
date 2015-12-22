package org.gooru.nucleus.db;

public interface CourseInterface {
  
  public boolean getCourseById(String course_id);
  
  public String copyCourse(String source_course_id,String logged_in_user);
  
}
