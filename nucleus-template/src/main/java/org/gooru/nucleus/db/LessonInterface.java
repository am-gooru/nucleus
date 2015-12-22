package org.gooru.nucleus.db;

import java.util.HashMap;

public interface LessonInterface {

  //returns false if not found 
  public boolean getLessonByCourseIdAndUnitId(String course_id, String unit_id, String lesson_id) ;
  
  //returns new cul id and seq id of the lesson with complete recursive copy of children of the collection
  public String copyLessonToUnit(String source_course_id, String source_unit_id, String lesson_id, String target_course_id, String target_unit_id, String logged_in_user);

  // returns max seq id of lessons by course and unit
  public int getMaxSeqIdOfLessonsByCourseIdAndUnitId(String course_id, String unit_id) ;
  
  // returns -1 in case of error and if successful returns the last seq id of the elements ( collections/assessments)
  public int getMaxSeqIdOfElementsInLesson(String course_id, String unit_id, String lesson_id);
}
