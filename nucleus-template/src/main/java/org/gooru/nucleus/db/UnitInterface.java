package org.gooru.nucleus.db;

public interface UnitInterface {
  
  public boolean getUnitByCourseId(String course_id, String unit_id);
  
  public String copyUnitToCourse(String source_course_id, String unit_id, String target_course_id, String logged_in_user);
  
  public int getMaxSeqIdOfUnitsByCourseId(String course_id);

}
