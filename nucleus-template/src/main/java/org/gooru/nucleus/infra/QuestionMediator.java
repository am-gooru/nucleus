package org.gooru.nucleus.infra;

import io.vertx.core.json.JsonObject;

public interface QuestionMediator {

  public JsonObject getQuestionById(String questionId);

  public JsonObject createQuestion(JsonObject jsonData);

  public JsonObject updateQuestion(JsonObject jsonData);

}
