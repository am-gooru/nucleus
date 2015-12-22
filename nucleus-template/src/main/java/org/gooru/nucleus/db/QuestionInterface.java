package org.gooru.nucleus.db;


public interface QuestionInterface {
  
  //returns true or false if found or not
  public boolean getQuestionById(String question_id);
   
  // returns new question id
  public String copyQuestion(String source_question_id, String logged_in_user);

}
