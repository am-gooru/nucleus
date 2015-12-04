package org.gooru.nucleus.infra;

import org.gooru.nucleus.entity.User;

import io.vertx.core.json.JsonObject;

public interface ClassMediator {

  public JsonObject createClass(JsonObject jsonData);

  public JsonObject updateClass(JsonObject jsonData);

  public JsonObject getListOfCollaboratorsForClass(String classId);

  public JsonObject updateListOfCollaboratorsForClass(String classId, JsonObject jsonData);

  public JsonObject joinClass(String classId, JsonObject jsonData);

  public JsonObject getListOfJoinedMembersOfClass(String classId);

  public JsonObject inviteToClass(String classId, JsonObject jsonData);

  public JsonObject getListOfPendingMembersForClass(String classId);

  public JsonObject associateCourseWithClass(String classId, JsonObject jsonData);

  public JsonObject getClassesByStatus(String status);

  public JsonObject getClassesByAssociationWithCourse(Boolean isAssociated, User user);

  public JsonObject getClassesByContentVisibility(String contentId);

  public JsonObject publishCollectionAndAssessmentToClasses(JsonObject jsonData);

  public JsonObject getClassesAssignedToCourse(String courseId);

  public JsonObject getPublishedStatusOfCourseContentForAClass(String classId, String courseId);

  public JsonObject publishCourseContentToAClass(String classId, String courseId, JsonObject jsonData);

  public JsonObject getClassByCode(String code);
}