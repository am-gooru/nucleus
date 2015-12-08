package org.gooru.nucleus.infra;

import io.vertx.core.json.JsonObject;

public interface TaxonomyMediator {

  public JsonObject getSubjectsInTaxonomy();
  
  public JsonObject getCoursesInTaxonomy(String subjectId);
  
  public JsonObject getDomainsInTaxonomy(String subjectId, String courseId);
  
  public JsonObject getStandardsForDomainsInTaxonomy(String subjectId, String courseId, String domainId, String standardsFrameworkId);
  
  public JsonObject getStandardsFramework();
  
  public JsonObject getStandardsFrameworkLevel1(String standardsFrameworkId);
  
  public JsonObject getStandardsFrameworkLevel2(String standardsFrameworkId, String level1Id);
  
  public JsonObject getStandardsFrameworkLevel3(String standardsFrameworkId, String level1Id, String level2Id);
  
  public JsonObject getStandardsFrameworkLevel4(String standardsFrameworkId, String level1Id, String level2Id, String level3Id);
  
  public JsonObject getDOK();
}
