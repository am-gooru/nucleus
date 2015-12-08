package org.gooru.nucleus.infra;

import org.gooru.nucleus.entity.User;

import io.vertx.core.json.JsonObject;

public interface CourseMediator {

  public JsonObject createCourse(JsonObject jsonData);

  public JsonObject copyCourse(JsonObject jsonData);

  public JsonObject updateCourse(JsonObject jsonData);

  public JsonObject getCourseById(String courseId);

  public JsonObject getCoursesByUser(User user);

  public JsonObject deleteCourse(String courseId);

  public JsonObject reorderUnitsInCourse(String courseId, JsonObject jsonData);

  public JsonObject getListOfCollaboratorsForCourse(String courseId);

  public JsonObject updateListOfCollaboratorsForCourse(String courseId, JsonObject jsonData);
}
