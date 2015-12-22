package org.gooru.nucleus.bootstrap;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;

import org.gooru.nucleus.db.AssessmentImpl;
import org.gooru.nucleus.db.AssessmentInterface;
import org.gooru.nucleus.db.CollectionImpl;
import org.gooru.nucleus.db.CollectionInterface;
import org.gooru.nucleus.db.CourseImpl;
import org.gooru.nucleus.db.CourseInterface;
import org.gooru.nucleus.db.LessonImpl;
import org.gooru.nucleus.db.LessonInterface;
import org.gooru.nucleus.db.QuestionImpl;
import org.gooru.nucleus.global.constants.ConfigConstants;
import org.gooru.nucleus.global.constants.EndpointsConstants;
import org.gooru.nucleus.global.utils.Runner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import org.gooru.nucleus.queries.*;
import org.gooru.nucleus.db.QuestionInterface;
import org.gooru.nucleus.db.UnitImpl;
import org.gooru.nucleus.db.UnitInterface;

/**
 * Created by ashish on 6/11/15.
 * This class is responsible to bootstrap the application.
 * To start the application, it does three things:
 * First it starts the verticles which need to be deployed based on the configuration.
 * Second, once that is done, it spawns a HTTP server and continues with main even loop.
 * Third, mount the subrouters and the main router
 */
public class BootstrapVerticle extends AbstractVerticle {

  static final Logger LOG = LoggerFactory.getLogger(BootstrapVerticle.class);

  // Convenience method to enable running from IDE
  public static void main(String[] args) {
    Runner.runVerticle(BootstrapVerticle.class);
  }

  @Override
  public void start() throws Exception {
    final HttpServer httpServer = vertx.createHttpServer();

    final Router router = Router.router(vertx);
    initializeRoutes(router);
    // If the port is not present in configuration then we end up
    // throwing as we are casting it to int. This is what we want.
    final int port = config().getInteger(ConfigConstants.HTTP_PORT);
    LOG.info("Http server starting on port {}", port);
    httpServer.requestHandler(router::accept).listen(port);
    deployVerticles();

  }

  /*
   * Deploy the verticles.
   */
  private void deployVerticles() {
    LOG.info("Starting to deploy other verticles...");

    final JsonArray verticlesList = config().getJsonArray(ConfigConstants.VERTICLES_DEPLOY_LIST);
    final CompletableFuture<Void>[] resultFutures = new CompletableFuture[verticlesList.size()];

    for (int i = 0; i < verticlesList.size(); i++) {
      final String verticleName = verticlesList.getString(i);
      // Note that verticle name should be starting with "service:" prefix
      if (verticleName != null && !verticleName.isEmpty()) {
        LOG.info("Starting verticle: {}", verticleName);

        final CompletableFuture<Void> deployFuture = new CompletableFuture<>();
        resultFutures[i] = deployFuture;

        vertx.deployVerticle(verticleName, res -> {
          if (res.succeeded()) {
            deployFuture.complete(null);
            LOG.info("Deployment id is: " + res.result() + " for verticle: " + verticleName);
          } else {
            deployFuture.completeExceptionally(res.cause());
            LOG.info("Deployment failed!");
          }
        });
      } else {
        LOG.error("Invalid verticle name specified in configuration. Aborting");
        throw new IllegalArgumentException("Invalid verticle name specified in configuration. Aborting.");
      }
    }
    vertx.executeBlocking(future -> {
      future.complete();
      try {
        CompletableFuture.allOf(resultFutures).join();
      } catch (CompletionException e) {
        e.printStackTrace();
        throw e;
      }

    }, blockingResult -> {
      if (blockingResult.succeeded()) {
        LOG.info("Deployment successful");
      } else {
        LOG.error("Error deploying verticles. Shutting down.");
      }
    });

  }

  private void initializeRoutes(Router router) {

    EventBus eb = vertx.eventBus();

    router.route("/").handler(routingContext -> {

      eb.send(EndpointsConstants.DUMMY_ENDPOINT, "ping!", reply -> {
        if (reply.succeeded()) {
          LOG.info("Received reply " + reply.result().body());
          routingContext.response().end(reply.result().body().toString());
        } else {
          LOG.info("No reply");
        }
      });
    });

    // Resources
    getResourceById (router, "resources/:resourceId");
    executePost(router, "resources"); // Create resource
    executePut(router, "resources/:Id"); // Update resource

    // Questions
    // getQuestionById
  //  getQuestionById(router, "questions/:questionId" );
    executePost(router, "questions"); // Create question
    executePut(router, "questions/:Id"); // update question


    // Assessments
    executePost(router, "assessments"); // Create assessment
    executePut(router, "assessments/:Id"); // update assessment
    executeDelete(router, "assessments/:Id"); // Delete assessment
    executePost(router, "assessments/:Id/questions"); // Add question to
                                                      // assessment
    executeDelete(router, "assessments/:assessmentId/questions/:Id"); // remove
                                                                      // question
                                                                      // from
                                                                      // assessment
    executePut(router, "assessments/:Id/questions"); // copy existing question to assessment
    executePut(router, "assessments/:Id/questions/order"); // Reorder questions
                                                           // in assessments

    // list Collaborators for assessment
    router.route(HttpMethod.GET, ConfigConstants.BASE_PATH + "assessments/:assessmentId/collaborators")
        .handler(routingContext -> {
          JsonArray result = new JsonArray()
              .add(new JsonObject().put("gooruUid", "ee410cef-2a44-46ef-878d-172511e54e07")
                  .put("gooruOid", "14d4a284-5b67-45c9-99f9-4e0c174bddea").put("username", "SachinZ601")
                  .put("emailId", "sachin@gooru.org").put("status", "active")
                  .put("profileImageUrl",
                      "http://profile-images-goorulearning-org.s3.amazonaws.com/398b2cff-fbc7-4ec5-ae85-eab25882cf6b.png")
              .put("associatedDate", "1447751112000"))
              .add(new JsonObject().put("gooruOid", "14d4a284-5b67-45c9-99f9-4e0c174bddea")
                  .put("emailId", "sachin@gooru.org").put("status", "pending").put("associatedDate", "1447751112000"));

          routingContext.response().putHeader("content-type", "application/json");
          routingContext.response().setStatusCode(200);
          routingContext.response().end(result.toString());
        });

    //update list of collaborator
    executePut(router, "assessments/:assessmentId/collaborators");

    // get assessment by Id
    router.route(HttpMethod.GET, ConfigConstants.BASE_PATH + "assessments/:Id").handler(routingContext -> {
      String assessmentId = routingContext.request().getParam("Id");
      JsonObject result = new JsonObject().put("id", assessmentId).put("type", "assessment")
          .put("url", "https://docs.oracle.com/javase/tutorial/java/concepts/")
          .put("title", "Assessment on OOP introduction")
          .put("thumbnail", "http://thumbnails-demo.s3.amazonaws.com/ee410cef-2a44-46ef-878d-172511e54e07.png")
          .put("sharing", "anyonewithlink").put("learningObjective", new JsonObject()).put("audience", new JsonObject())
          .put("collaborator", new JsonObject())
          .put("metadata",
              new JsonObject().put("depthOfKnowledge", new JsonArray().add(166).add(168)).put("21CenturySkills",
                  new JsonArray()))
          .put("login_required", "true").put("settings", new JsonObject().put("comment", "turn-on"));

      routingContext.response().putHeader("content-type", "application/json");
      routingContext.response().setStatusCode(200);
      routingContext.response().end(result.toString());
    });

    // Collections
    executePost(router, "collections"); // Create Collection
    executePut(router, "collections/:Id"); // Update Collection
    executeDelete(router, "collections/:Id"); // Delete Collection
    executePost(router, "collections/:Id/questions"); // Create question in
                                                      // collection
    // remove question from collection
    executeDelete(router, "collections/:collectionId/questions/:Id");
    // remove resource from collection
    executeDelete(router, "collections/:collectionId/resources/:resourceId/:Id");
    // copy existing question to collection
    executePut(router, "collections/:Id/questions");
    // add new resource to collection
    executePost(router, "collections/:Id/resources");

    // GetCollectionById
    router.route(HttpMethod.PUT, ConfigConstants.BASE_PATH + "collections/:collectionId").handler(routingContext -> {
      String collectionId = routingContext.request().getParam("collectionId");

      JsonObject result = new JsonObject().put("id", collectionId).put("title", "Assessment on OOP introduction")
          .put("thumbnails",
              "http://cdn.goorulearning.org/prod1//f000/2624/0222//109159af-acfc-4cc5-9b4b-ab234c93608b.jpg")
          .put("sharing", "anyonewithlink")
          .put("learningObjective",
              "This is objective")
          .put("comments_enabled",
              "true")
          .put("audience",
              new JsonArray().add(new JsonObject().put("id", "198").put("name", "English Language Learners"))
                  .add(new JsonObject().put("id", "199").put("name", "Students With Special Needs")))
          .put("collaborator",
              new JsonArray()
                  .add(new JsonObject().put("gooruUid", "398b2cff-fbc7-4ec5-ae85-eab25882cf6b")
                      .put("gooruOid", "14d4a284-5b67-45c9-99f9-4e0c174bddea").put("username", "SachinZ601")
                      .put("emailId", "sachin@gooru.org").put("status", "active")
                      .put("profileImageUrl",
                          "http://profile-images-goorulearning-org.s3.amazonaws.com/398b2cff-fbc7-4ec5-ae85-eab25882cf6b.png")
              .put("associatedDate", "1447751112000"))
              .add(new JsonObject().put("gooruUid", "398b2cff-fbc7-4ec5-ae85-eab25882cf6b")
                  .put("gooruOid", "14d4a284-5b67-45c9-99f9-4e0c174bddea").put("username", "PersonZ601")
                  .put("emailId", "Sample@gooru.org").put("status", "active")
                  .put("profileImageUrl",
                      "http://profile-images-goorulearning-org.s3.amazonaws.com/398b2cff-fbc7-4ec5-ae85-eab25882cf6b.png")
                  .put("associatedDate", "1447751112000")))
          .put("metadata",
              new JsonObject().put("depthOfKnowledge", new JsonArray().add(166).add(168))
                  .put("standards", new JsonArray()).put("21CenturySkills", new JsonArray())
                  .put("learningTarget", new JsonArray()))
          .put("resource",
              new JsonArray()
                  .add(new JsonObject().put("type", "question").put("title", "This is first question").put("thumbnail",
                      "http: //thumbnails-demo.s3.amazonaws.com/ee410cef-2a44-46ef-878d-172511e54e07.png"))
              .add(new JsonObject().put("type", "resource/url").put("title", "This is first resource")
                  .put("url", "https: //docs.oracle.com/javase/tutorial/java/concepts/").put("thumbnail",
                      "http: //thumbnails-demo.s3.amazonaws.com/ee410cef-2a44-46ef-878d-172511e54e07.png")));

      routingContext.response().putHeader("content-type", "application/json");
      routingContext.response().setStatusCode(200);
      routingContext.response().end(result.toString());
    });

    // Courses
    executePost(router, "courses"); // Create Course
    executePut(router, "courses"); // copy Course
    executeDelete(router, "courses/:Id"); // delete course
    executePut(router, "courses/:Id"); // update course
    executePut(router, "courses/:Id/order"); // reorder units in courses
    executePut(router, "courses/:Id/collaborators"); // update Collaborators

    // getCourseById - /courses/{course-id}
    router.route(HttpMethod.GET, ConfigConstants.BASE_PATH + "courses/:courseId").handler(routingContext -> {
      String courseId = routingContext.request().getParam("courseId");
      JsonObject result = new JsonObject().put("id", courseId).put("title", "Understanding Basics of Core Java")
          .put("sharing", "private")
          .put("thumbnail", "http://thumbnails-demo.s3.amazonaws.com/ee410cef-2a44-46ef-878d-172511e54e07.png")
          .put("audience",
              new JsonArray().add(new JsonObject().put("id", "197").put("name", "Students Above Grade Level")))
          .put("taxonomyCourse",
              new JsonArray()
                  .add(new JsonObject().put("id", "228").put("name", "Computer Science").put("subjectId", "5"))
                  .add(new JsonObject().put("id", "75").put("name", "Grade 7 Science").put("subjectId", "4")))
          .put("summary", new JsonObject().put("unitCount", "1")).put("user",
              new JsonObject().put("gooruUId", "ca56333a-73b8-4e41-a25e-a015fe4276d3")
                  .put("profileImageUrl",
                      "http://profile-demo.s3.amazonaws.com/ca56333a-73b8-4e41-a25e-a015fe4276d3.png")
              .put("username", "sachin"));

      routingContext.response().putHeader("content-type", "application/json");
      routingContext.response().setStatusCode(200);
      routingContext.response().end(result.toString());
    });

    // listCoursesByUser
    router.route(HttpMethod.GET, ConfigConstants.BASE_PATH + "courses").handler(routingContext -> {
      JsonArray result = new JsonArray().add(new JsonObject().put("id", "ee410cef-2a44-46ef-878d-172511e54e07")
          .put("title", "Computer Science").put("sharing", "private")
          .put("thumbnail", "http://thumbnails-demo.s3.amazonaws.com/ee410cef-2a44-46ef-878d-172511e54e07.png")
          .put("user",
              new JsonObject().put("gooruUId", "ca56333a-73b8-4e41-a25e-a015fe4276d3")
                  .put("profileImageUrl",
                      "http://profile-demo.s3.amazonaws.com/ca56333a-73b8-4e41-a25e-a015fe4276d3.png")
                  .put("username", "sachin")))
          .add(new JsonObject().put("id", "ee410cef-2a44-46ef-878d-172511e54e07").put("title", "Computer Science II")
              .put("sharing", "private")
              .put("thumbnail", "http://thumbnails-demo.s3.amazonaws.com/ee410cef-2a44-46ef-878d-172511e54e07.png")
              .put("user",
                  new JsonObject().put("gooruUId", "ca56333a-73b8-4e41-a25e-a015fe4276d3")
                      .put("profileImageUrl",
                          "http://profile-demo.s3.amazonaws.com/ca56333a-73b8-4e41-a25e-a015fe4276d3.png")
                          .put("username", "sachin")));

      routingContext.response().putHeader("content-type", "application/json");
      routingContext.response().setStatusCode(200);
      routingContext.response().end(result.toString());
    });

    // listCollaboratorsByCourse
    router.route(HttpMethod.GET, ConfigConstants.BASE_PATH + "courses/:courseId/collaborators")
        .handler(routingContext -> {
          JsonArray result = new JsonArray()
              .add(new JsonObject().put("gooruUid", "ee410cef-2a44-46ef-878d-172511e54e07")
                  .put("gooruOid", "14d4a284-5b67-45c9-99f9-4e0c174bddea").put("username", "SachinZ601")
                  .put("emailId", "sachin@gooru.org").put("status", "active")
                  .put("profileImageUrl",
                      "http://profile-images-goorulearning-org.s3.amazonaws.com/398b2cff-fbc7-4ec5-ae85-eab25882cf6b.png")
              .put("associatedDate", "1447751112000"))
              .add(new JsonObject().put("gooruOid", "14d4a284-5b67-45c9-99f9-4e0c174bddea")
                  .put("emailId", "sachin@gooru.org").put("status", "pending").put("associatedDate", "1447751112000"));

          routingContext.response().putHeader("content-type", "application/json");
          routingContext.response().setStatusCode(200);
          routingContext.response().end(result.toString());
        });

    // Units
    executePost(router, "courses/:Id/units"); // Create Unit
    executePut(router, "courses/:courseId/units/:Id"); // update unit
    executeDelete(router, "courses/:courseId/units/:Id"); // delete unit
    executePut(router, "courses/:Id/units"); // copy unit
    executePut(router, "courses/:courseId/units/:Id/order"); // reorder lessons
                                                             // in unit

    // getUnitById - /courses/{course-id}/units/{unit-id}
    router.route(HttpMethod.GET, ConfigConstants.BASE_PATH + "courses/:courseId/units/:unitId")
        .handler(routingContext -> {
          String courseId = routingContext.request().getParam("courseId");
          String unitId = routingContext.request().getParam("unitId");
          JsonObject result = new JsonObject().put("summary", new JsonObject().put("lessonCount", "2"))
              .put("parentGooruOid", courseId).put("itemSequence", "1")
              .put("lastModifiedUserUid", "eea60d3e-8d7d-432f-a9e0-235545d87893").put("title", "OOPS")
              .put("sharing", "private").put("ideas", "This unit is based on OOP").put("lastModified", "1447908014000")
              .put("questions", "What is OOP and what are the benefits of it").put("gooruOid", unitId)
              .put("taxonomyCourse",
                  new JsonArray().add(new JsonObject().put("id", "75").put("name", "Arabic 1").put("subjectId", "5")))
              .put("subdomain",
                  new JsonArray()
                      .add(new JsonObject().put("id", "963").put("name", "Engineering Design").put("subjectId", "2")
                          .put("courseId", "26"))
                  .add(new JsonObject().put("id", "1107").put("name", "Economics: Economic Decision Making")
                      .put("subjectId", "4").put("courseId", "60")))
              .put("user",
                  new JsonObject().put("gooruUId", "ca56333a-73b8-4e41-a25e-a015fe4276d3")
                      .put("profileImageUrl",
                          "http://profile-demo.s3.amazonaws.com/ca56333a-73b8-4e41-a25e-a015fe4276d3.png")
                  .put("username", "sachin"));

          routingContext.response().putHeader("content-type", "application/json");
          routingContext.response().setStatusCode(200);
          routingContext.response().end(result.toString());
        });

    // Lessons
    executePost(router, "courses/:courseId/units/:Id/lessons"); // Create Lesson
    executePut(router, "courses/:courseId/units/:Id/lessons"); // copy lesson
    executeDelete(router, "courses/:courseId/units/:unitId/lessons/:Id"); // delete lesson
    executePut(router, "courses/:courseId/units/:unitId/lessons/:Id"); // update lesson
    // reorder lessons in units
    executePut(router, "courses/:courseId/units/:Id/lessons/:lessonId/order");

    // getLessonById
    router.route(HttpMethod.GET, ConfigConstants.BASE_PATH + "courses/:courseId/units/:unitId/lessons/:lessonId")
        .handler(routingContext -> {
          String unitId = routingContext.request().getParam("unitId");
          String lessonId = routingContext.request().getParam("lessonId");
          JsonObject result = new JsonObject()
              .put("summary", new JsonObject().put("collectionCount", "2").put("assessmentCount", "0"))
              .put("parentGooruOid", lessonId).put("itemSequence", "1")
              .put("lastModifiedUserUid", "eea60d3e-8d7d-432f-a9e0-235545d87893").put("title", "Lesson-2")
              .put("sharing", "private").put("lastModified", "1447908014000")
              .put("standards",
                  new JsonArray()
                      .add(new JsonObject().put("id", "77505").put("rootNodeId", "77271").put("code", "NGSS-MS-ETS1-1"))
                      .add(new JsonObject().put("id", "77508").put("rootNodeId", "77871").put("code", "C3.D3.1.K-2")))
              .put("gooruOid", lessonId)
              .put("taxonomyCourse",
                  new JsonArray()
                      .add(new JsonObject().put("id", "113").put("name", "Physical Education").put("subjectId", "5")))
              .put("user",
                  new JsonObject().put("gooruUId", "ca56333a-73b8-4e41-a25e-a015fe4276d3")
                      .put("profileImageUrl",
                          "http://profile-demo.s3.amazonaws.com/ca56333a-73b8-4e41-a25e-a015fe4276d3.png")
                  .put("username", "sachin"));

          routingContext.response().putHeader("content-type", "application/json");
          routingContext.response().setStatusCode(200);
          routingContext.response().end(result.toString());
        });

    // Class
    executePost(router, "classes"); // create class
    executePut(router, "classes/:Id"); // update class

    // Get list of collaborators for a class
    router.route(HttpMethod.GET, ConfigConstants.BASE_PATH + "classes/:classId/collaborators")
        .handler(routingContext -> {
          JsonArray result = new JsonArray()
              .add(new JsonObject().put("gooruUid", "ee410cef-2a44-46ef-878d-172511e54e07")
                  .put("gooruOid", "14d4a284-5b67-45c9-99f9-4e0c174bddea").put("username", "SachinZ601")
                  .put("emailId", "sachin@gooru.org").put("status", "active")
                  .put("profileImageUrl",
                      "http://profile-images-goorulearning-org.s3.amazonaws.com/398b2cff-fbc7-4ec5-ae85-eab25882cf6b.png")
              .put("associatedDate", "1447751112000"))
              .add(new JsonObject().put("gooruOid", "14d4a284-5b67-45c9-99f9-4e0c174bddea")
                  .put("emailId", "sachin@gooru.org").put("status", "pending").put("associatedDate", "1447751112000"));

          routingContext.response().putHeader("content-type", "application/json");
          routingContext.response().setStatusCode(200);
          routingContext.response().end(result.toString());
        });
    // update list of collaborators for class
    executePut(router, "classes/:classId/collaborators");
    executePut(router, "classes/:Id/members"); // Join class

    // get list of members who joined the class
    router.route(HttpMethod.GET, ConfigConstants.BASE_PATH + "classes/:Id/members").handler(routingContext -> {
      JsonArray result = new JsonArray()
          .add(new JsonObject().put("associationDate", "1447911701000").put("lastname", "Zope")
              .put("emailId", "sachin@gooru.org").put("username", "SachinZ601").put("firstname", "Sachin")
              .put("gooruUId", "398b2cff-fbc7-4ec5-ae85-eab25882cf6b"))
          .add(new JsonObject().put("associationDate", "1447911701000").put("lastname", "Lu")
              .put("emailId", "mark@gmail.com").put("username", "MarkZ33").put("firstname", "Mark")
              .put("gooruUId", "398b2cff-fbc7-4ec5-ae85-eab25882cf6c"));
      routingContext.response().putHeader("content-type", "application/json");
      routingContext.response().setStatusCode(200);
      routingContext.response().end(result.toString());
    });

    // invite to class
    executePut(router, "classes/:Id/invites");
    
    // list pending members for class
    router.route(HttpMethod.GET, ConfigConstants.BASE_PATH + "classes/:Id/invites").handler(routingContext -> {
      JsonArray result = new JsonArray().add("sachin@gooru.org").add("mark@gmail.com");
      routingContext.response().putHeader("content-type", "application/json");
      routingContext.response().setStatusCode(200);
      routingContext.response().end(result.toString());
    });
    
    //associate course with class
    executePut(router, "classes/:Id/courses");
    
    // Fetch All Archived Classes - for User
    // Fetch All Active Classes - for User
    router.route(HttpMethod.GET, ConfigConstants.BASE_PATH + "classes?status=:status").handler(routingContext -> {
      //String status = routingContext.request().getParam("status");
      JsonArray result = new JsonArray()
          .add(new JsonObject().put("classUid", "ffdc3592-cc33-43ca-b1dd-73373544c92b").put("visibility", "true")
              .put("minimumScore", "50").put("name", "Test-04Aug2015").put("classCode", "2W5KDLL")
              .put("memberCount", "0"))
          .add(new JsonObject().put("classUid", "96d60d3b-1bb8-4f9b-99b0-42604674526d").put("visibility", "true")
              .put("minimumScore", "60").put("name", "MYCLASS").put("classCode", "VN74GXS")
              .put("courseGooruOid", "7d478abb-74e3-4187-b7e4-4f04f4b9956e").put("memberCount", "0"));
      routingContext.response().putHeader("content-type", "application/json");
      routingContext.response().setStatusCode(200);
      routingContext.response().end(result.toString());
    });
    
    // Fetch All Classes which are not associated with Course for User context
    router.route(HttpMethod.GET, ConfigConstants.BASE_PATH + "classes?course-assigned=:courseAssigned").handler(routingContext -> {
      JsonArray result = new JsonArray()
          .add(new JsonObject().put("classUid", "ffdc3592-cc33-43ca-b1dd-73373544c92b").put("visibility", "true")
              .put("minimumScore", "50").put("name", "Test-04Aug2015").put("classCode", "2W5KDLL")
              .put("memberCount", "0"))
          .add(new JsonObject().put("classUid", "96d60d3b-1bb8-4f9b-99b0-42604674526d").put("visibility", "true")
              .put("minimumScore", "60").put("name", "MYCLASS").put("classCode", "VN74GXS")
              .put("memberCount", "0"));
      routingContext.response().putHeader("content-type", "application/json");
      routingContext.response().setStatusCode(200);
      routingContext.response().end(result.toString());
    });
    
    // Fetch All Classes for which the specified Content is not Visible or Not
    router.route(HttpMethod.GET, ConfigConstants.BASE_PATH + "classes?content-id=:contentId").handler(routingContext -> {
      JsonArray result = new JsonArray()
          .add(new JsonObject().put("classUid", "ffdc3592-cc33-43ca-b1dd-73373544c92b").put("visibility", "true")
              .put("minimumScore", "50").put("name", "Test-04Aug2015").put("classCode", "2W5KDLL")
              .put("memberCount", "0"))
          .add(new JsonObject().put("classUid", "96d60d3b-1bb8-4f9b-99b0-42604674526d").put("visibility", "true")
              .put("minimumScore", "60").put("name", "MYCLASS").put("classCode", "VN74GXS")
              .put("memberCount", "0"));
      routingContext.response().putHeader("content-type", "application/json");
      routingContext.response().setStatusCode(200);
      routingContext.response().end(result.toString());
    });
    
    // Publish a specific Collection/Assessment to a multiple Classes
    executePut(router, "classes");
    
    // Fetch All Classes assigned to a specific Course
    router.route(HttpMethod.GET, ConfigConstants.BASE_PATH + "classes?assigned-course=:courseId").handler(routingContext -> {
      String coureId = routingContext.request().getParam("courseId");
      JsonArray result = new JsonArray()
          .add(new JsonObject().put("classUid", "ffdc3592-cc33-43ca-b1dd-73373544c92b").put("visibility", "true")
              .put("minimumScore", "50").put("name", "Test-04Aug2015").put("classCode", "2W5KDLL")
              .put("courseGooruOid", coureId).put("memberCount", "0"))
          .add(new JsonObject().put("classUid", "96d60d3b-1bb8-4f9b-99b0-42604674526d").put("visibility", "true")
              .put("minimumScore", "60").put("name", "MYCLASS").put("classCode", "VN74GXS")
              .put("courseGooruOid", coureId).put("memberCount", "0"));
      routingContext.response().putHeader("content-type", "application/json");
      routingContext.response().setStatusCode(200);
      routingContext.response().end(result.toString());
    });
    
    // Fetch published status for content of a Course associated with a specific Class
    //TODO
    
    // Publish content of a Course to a specific Class
    executePut(router, "classes/:classId/courses/:Id");

    // Retrieve a specific Class by the Class Code
    router.route(HttpMethod.GET, ConfigConstants.BASE_PATH + "classes?class-code=:classCode").handler(routingContext -> {
      String classCode = routingContext.request().getParam("classCode");
      JsonArray result = new JsonArray()
          .add(new JsonObject().put("classUid", "ffdc3592-cc33-43ca-b1dd-73373544c92b").put("visibility", "true")
              .put("minimumScore", "50").put("name", "Test-04Aug2015").put("classCode", classCode)
              .put("courseGooruOid", "96d60d3b-1bb8-4f9b-99b0-42604674526d").put("memberCount", "0"));
      routingContext.response().putHeader("content-type", "application/json");
      routingContext.response().setStatusCode(200);
      routingContext.response().end(result.toString());
    });
    
    copyQuestion(router, "questions/:questionId" );
    copyQuestionToAssessment(router, "assessments/:Id/questions/:qId");
    copyQuestionToCollection(router, "collections/:Id/questions/:qId");
    copyCollection(router, "collections/:Id");
    copyAssessment(router, "assessments/:Id");
    copyLessonToUnit(router, "courses/:courseId/units/:unitId/lessons/:lessonId/:targetCourseId/:targetUnitId");
    copyUnitToCourse(router, "courses/:courseId/units/:unitId/:targetCourseId");
    copyCourse(router, "courses-copy/:courseId");
    copyResourceToCollection(router, "collections/:Id/resources/:rId");

  }
  
  private void copyCourse(Router router, String path) {
    // TODO Auto-generated method stub
    //copyCourse(router, "courses-copy/:courseId");
    router.route(HttpMethod.GET, ConfigConstants.BASE_PATH + path).handler(routingContext -> {
      String source_course_id = routingContext.request().getParam("courseId");
        String logged_in_user = UUID.randomUUID().toString();
      
      CourseInterface ci = new CourseImpl();
      try {
        String new_course_id = ci.copyCourse(source_course_id, logged_in_user);
        
        LOG.info(" in copyCourse : {} " , new_course_id);
      } catch (Exception e) {
        // TODO Auto-generated catch block
        e.printStackTrace();;
      }      
    });
  }
  
  private void copyLessonToUnit(Router router, String path) {
    // TODO Auto-generated method stub
    //copyLessonToUnit(router, "courses/:courseId/units/:unitId/lessons/:lessonId/:targetCourseId/:targetUnitId");
    router.route(HttpMethod.GET, ConfigConstants.BASE_PATH + path).handler(routingContext -> {
      String source_course_id = routingContext.request().getParam("courseId");
      String source_unit_id = routingContext.request().getParam("unitId");
      String source_lesson_id = routingContext.request().getParam("lessonId");
      String target_course_id = routingContext.request().getParam("targetCourseId");
      String target_unit_id = routingContext.request().getParam("targetUnitId");
      String logged_in_user = UUID.randomUUID().toString();
      
      LessonInterface li = new LessonImpl();
      try {
        String new_lesson_id = li.copyLessonToUnit(source_course_id, source_unit_id, source_lesson_id, target_course_id, target_unit_id, logged_in_user);
        
        LOG.info(" in copyLessonToUnit : {} " , new_lesson_id);
      } catch (Exception e) {
        // TODO Auto-generated catch block
        e.printStackTrace();;
      }      
    });
  }
  
  private void copyUnitToCourse(Router router, String path) {
    // TODO Auto-generated method stub
    //copyLessonToUnit(router, "courses/:courseId/units/:unitId/:targetCourseId");
    router.route(HttpMethod.GET, ConfigConstants.BASE_PATH + path).handler(routingContext -> {
      String source_course_id = routingContext.request().getParam("courseId");
      String unit_id = routingContext.request().getParam("unitId");
      String target_course_id = routingContext.request().getParam("targetCourseId");
      String logged_in_user = UUID.randomUUID().toString();
      
      UnitInterface ui = new UnitImpl();
      try {
        String new_unit_id = ui.copyUnitToCourse(source_course_id, unit_id, target_course_id, logged_in_user);
        
        LOG.info(" in copyUnitToCourse : {} " , new_unit_id);
      } catch (Exception e) {
        // TODO Auto-generated catch block
        e.printStackTrace();;
      }      
    });
  }
  
  private void copyQuestionToAssessment(Router router, String path) {
    // TODO Auto-generated method stub
    router.route(HttpMethod.GET, ConfigConstants.BASE_PATH + path).handler(routingContext -> {
      String target_assessment_id = routingContext.request().getParam("Id");
      String question_id = routingContext.request().getParam("qId");
      String logged_in_user_id = UUID.randomUUID().toString();
      LOG.info(" in copyQuestionToAssessment : {}", target_assessment_id);
      LOG.info(" in copyQuestionToAssessment : {}", question_id);
      HashMap<String, String> resultHashmap= new HashMap<String, String>();
      AssessmentInterface ai = new AssessmentImpl();
      try {
        resultHashmap  = ai.copyQuestionToAssessment(question_id, target_assessment_id, logged_in_user_id);
        
        LOG.info(" in copyQuestionToAssessment : {} {}" , resultHashmap.get("new_question_id"), resultHashmap.get("new_question_seq_id"));
      } catch (Exception e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }      
    });
    
  }
  
  private void copyQuestionToCollection(Router router, String path) {
    // TODO Auto-generated method stub
    router.route(HttpMethod.GET, ConfigConstants.BASE_PATH + path).handler(routingContext -> {
      String target_collection_id = routingContext.request().getParam("Id");
      String question_id = routingContext.request().getParam("qId");
      String logged_in_user = UUID.randomUUID().toString();
      LOG.info(" in copyQuestionToCollection : {}", target_collection_id);
      LOG.info(" in copyQuestionToCollection : {}", question_id);
      HashMap<String, String> resultHashmap= new HashMap<String, String>();
      CollectionInterface ci = new CollectionImpl();
      try {
        resultHashmap  = ci.copyQuestionToCollection(question_id, target_collection_id, logged_in_user);
        
        LOG.info(" in copyQuestionToCollection : {} {}" , resultHashmap.get("new_question_id"), resultHashmap.get("new_question_seq_id"));
      } catch (Exception e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }      
    });
  }
  
  private void copyResourceToCollection(Router router, String path) {
    // TODO Auto-generated method stub
    router.route(HttpMethod.GET, ConfigConstants.BASE_PATH + path).handler(routingContext -> {
      String target_collection_id = routingContext.request().getParam("Id");
      String resource_id = routingContext.request().getParam("rId");
      String logged_in_user = UUID.randomUUID().toString();
      LOG.info(" in copyResourceToCollection : {}", target_collection_id);
      LOG.info(" in copyResourceToCollection : {}", resource_id);
      HashMap<String, String> resultHashmap= new HashMap<String, String>();
      CollectionInterface ci = new CollectionImpl();
      try {
        resultHashmap  = ci.copyResourceToCollection(resource_id, target_collection_id, logged_in_user);
        
        LOG.info(" in copyResourceToCollection : {} {}" , resultHashmap.get("new_generated_col_item_id"), resultHashmap.get("new_question_seq_id"));
      } catch (Exception e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }      
    });
  }

  private void copyCollection(Router router, String path) {
    // TODO Auto-generated method stub
    router.route(HttpMethod.GET, ConfigConstants.BASE_PATH + path).handler(routingContext -> {
      String collection_id = routingContext.request().getParam("Id");
      String logged_in_user = UUID.randomUUID().toString();
      LOG.info(" in copyCollection : {}", collection_id);

      CollectionInterface ci = new CollectionImpl();
      try {
        String newColId = ci.copyCollection(collection_id, logged_in_user);
        
        LOG.info(" in copyCollection : {} " , newColId);
      } catch (Exception e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }      
    });
  }
  
  private void copyAssessment(Router router, String path) {
    // TODO Auto-generated method stub
    router.route(HttpMethod.GET, ConfigConstants.BASE_PATH + path).handler(routingContext -> {
      String assessment_id = routingContext.request().getParam("Id");
      String logged_in_user = UUID.randomUUID().toString();
      LOG.info(" in copyAssessment : {}", assessment_id);

      AssessmentInterface ai = new AssessmentImpl();
      try {
        String newAssId = ai.copyAssessment(assessment_id, logged_in_user);
        
        LOG.info(" in copyAssessment : {} " , newAssId);
      } catch (Exception e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }      
    });
  }
  
  

  private void executePost(Router router, String path) {
  	router.route(HttpMethod.POST, ConfigConstants.BASE_PATH + path).handler(routingContext -> {
  	  routingContext.response().putHeader("Location", "ca56333a-73b8-4e41-a25e-a015fe4276d3");
  	  routingContext.response().setStatusCode(201);
  	  routingContext.response().end();
  	});
  }

  private void executePut(Router router, String path) {
  	router.route(HttpMethod.PUT, ConfigConstants.BASE_PATH + path).handler(routingContext -> {
  	  String id = routingContext.request().getParam("Id");
  
  	  routingContext.response().putHeader("Location", id);
  	  routingContext.response().setStatusCode(204);
  	  routingContext.response().end();
  	});
  }

  private void executeDelete(Router router, String path) {
  	router.route(HttpMethod.DELETE, ConfigConstants.BASE_PATH + path).handler(routingContext -> {	
  	  routingContext.response().setStatusCode(204);
  	  routingContext.response().end();
  	});
  }
  
  private void getResourceById(Router router, String path) {
    router.route(HttpMethod.GET, ConfigConstants.BASE_PATH + path).handler(routingContext -> {
      String resourceId = routingContext.request().getParam("resourceId");
      ResourceQuery rsQuery = new ResourceQuery();
      try {
        rsQuery.getResourceById(resourceId);
      } catch (Exception e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }

      JsonObject result =
              new JsonObject().put("id", resourceId).put("title", "This is my first resource")
                      .put("description",
                              "This resource is useful for new learner")
                      .put("format", "webpage")
                      .put("thumbnail",
                              "http://thumbnails-demo.s3.amazonaws.com/ee410cef-2a44-46ef-878d-172511e54e07.png")
                      .put("url", "http://en.wikipedia.org/wiki/Austin_M").put("sharing", "public").put("isFrameBreaker", "0")
                      .put("user",
                              new JsonObject().put("gooruUId", "ee410cef-2a44-46ef-878d-172511e54e07")
                                      .put("profileImageUrl", "http://profile-demo.s3.amazonaws.com/ee410cef-2a44-46ef-878d-172511e54e07.png")
                                      .put("username", "SachinZ"))
              .put("metadata",
                      new JsonObject().put("educationalUse", new JsonArray()).put("educationalRole", new JsonArray())
                              .put("depthOfKnowledge", new JsonArray().add(166).add(168)).put("standards", new JsonArray())
                              .put("readingLevel", new JsonArray()).put("advertisementLevel", new JsonArray()).put("hazardLevel", new JsonArray())
                              .put("mediaFeatures", new JsonArray()).put("mobileFriendly", new JsonArray()).put("publisher", new JsonArray())
                              .put("aggregator", new JsonArray()).put("fqdn", new JsonArray()));
      routingContext.response().putHeader("content-type", "application/json");
      routingContext.response().putHeader("Location", resourceId);
      routingContext.response().setStatusCode(200);
      routingContext.response().end(result.toString());

    });
    }
    
  private void copyQuestion(Router router, String path) {
    // getQuestionById
    router.route(HttpMethod.GET, ConfigConstants.BASE_PATH + "questions/:questionId").handler(routingContext -> {
      String questionId = routingContext.request().getParam("questionId");
      QuestionInterface qi = new QuestionImpl();
      String logged_in_user_id = UUID.randomUUID().toString();
      
      try {
        String new_q_id  = qi.copyQuestion(questionId, logged_in_user_id);
        
        LOG.info(" in getQById : {}" , new_q_id);
      } catch (Exception e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }      
      routingContext.response().setStatusCode(200);
      routingContext.response().end();
    });

  }
}
