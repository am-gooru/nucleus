package org.gooru.nucleus.infra;

import io.vertx.core.json.JsonObject;

public interface AssessmentMediator {

  public JsonObject getAssessmentById(String assessmentId);

  public JsonObject createAssessment(JsonObject jsonData);

  public JsonObject updateAssessment(JsonObject jsonData);

  public JsonObject deleteAssessment(String assessmentId);

  public JsonObject addNewQuestionToAssessment(JsonObject jsonData);

  public JsonObject removeQuestionFromAssessment(String assessmentId, String questionId);

  public JsonObject copyQuestionToAssessment(String assessmentId, JsonObject jsonData);

  public JsonObject reorderQuestionsInAssessment(String assessmentId, JsonObject jsonData);

  public JsonObject getListOfCollaboratorsForAssessment(String assessmentId);

  public JsonObject updateListOfCollaboratorsForAssessment(String assessmentId, JsonObject jsonData);
}
