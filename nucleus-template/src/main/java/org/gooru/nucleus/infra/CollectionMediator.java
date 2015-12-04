package org.gooru.nucleus.infra;

import io.vertx.core.json.JsonObject;

public interface CollectionMediator {

  public JsonObject getCollectionById(String collectionId);

  public JsonObject createCollection(JsonObject jsonData);

  public JsonObject updateCollection(JsonObject jsonData);

  public JsonObject deleteCollection(String collectionId);

  public JsonObject addNewQuestionToCollection(String collectionId, JsonObject jsonData);

  public JsonObject addNewResourceToCollection(String collectionId, JsonObject jsonData);

  public JsonObject addExistingResourceToCollection(String collectionId, JsonObject jsonData);

  public JsonObject removeQuestionFromCollection(String collectionId, String questionId);

  public JsonObject removeResourceFromCollection(String collectionId, String resourceId, String sequenceId);

  public JsonObject updateResourceInCollection(String collectionId, String resourceId, String sequenceId,
      JsonObject jsonData);

  public JsonObject updateQuestionInCollection(String collectionId, String questionId, String sequenceId,
      JsonObject jsonData);

  public JsonObject copyQuestionToCollection(String collectionId, JsonObject jsonData);

  public JsonObject getListOfCollaboratorsForCollection(String collectionId);

  public JsonObject updateListOfCollaboratorsForCollection(String collectionId, JsonObject jsonData);

  public JsonObject reorderResourcesAndQuestionsInColelction(String collectionId, JsonObject jsonData);

}
