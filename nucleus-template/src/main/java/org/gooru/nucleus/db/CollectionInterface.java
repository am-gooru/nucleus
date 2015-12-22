package org.gooru.nucleus.db;

import java.util.HashMap;

public interface CollectionInterface {
  
  // returns new question id and sequence id of that question as a hashmap 
  public HashMap<String, String> copyQuestionToCollection(String question_id, String target_collection_id, String logged_in_user) ;
  
  //returns new collection_item id and sequence id of that resource as a hashmap 
  public HashMap<String, String> copyResourceToCollection(String resource_id, String target_collection_id, String logged_in_user) ;

  // returns new collection id with complete recursive copy of children of the collection
  public String copyCollection(String collection_id, String logged_in_user);
  
  //returns new collection id with complete recursive copy of children of the collection
  public String  copyCollectionToLesson(String source_course_id, String source_unit_id, String target_course_id, String target_unit_id, String lesson_id, String collection_id, String logged_in_user) ;
   
  //returns true or false if found or not
  public boolean getCollectionById(String collection_id) ;

  // returns -1 in case of error and if successful returns the last seq id of the elements ( questions/resources)
  public int getMaxSeqIdOfElementsInCollection(String collection_id);
}
