package org.gooru.nucleus.db;

import java.util.HashMap;

public interface AssessmentInterface {

  // returns new question id and sequence id of that question as a hashmap 
  public HashMap<String, String>copyQuestionToAssessment(String question_id, String target_assessment_id, String logged_in_user);
  
  // returns true or false if found or not
  public boolean getAssessmentById(String assessment_id);
  
  // returns new_assessment_id
  public String copyAssessment(String assessment_id, String logged_in_user);
  
  //returns new assessment id with complete recursive copy of children of the collection
  public  String  copyAssessmentToLesson(String source_course_id, String source_unit_id, String target_course_id, String target_unit_id, String lesson_id, String assessment_id, String logged_in_user) ;

  //returns -1 in case of error and if successful returns the last seq id of the elements ( questions)
  public int getMaxSeqIdOfQuestionsInAssessment(String assessment_id);
}
