package org.gooru.nucleus.bootstrap;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import org.gooru.nucleus.global.constants.ConfigConstants;
import org.gooru.nucleus.global.constants.EndpointsConstants;
import org.gooru.nucleus.global.utils.Runner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

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
    
	//Resources 
	executePost(router, "resources");
	executePut(router, "resources/:Id");

	//Get Resource
	router.route(HttpMethod.GET, ConfigConstants.BASE_PATH + "resources/:resourceId").handler(routingContext -> {
		String resourseId = routingContext.request().getParam("resourceId");
		JsonObject result = new JsonObject().put("id", resourseId).put("title", "This is my first resource")
				.put("description", "This resource is useful for new learner").put("format", "webpage")
				.put("thumbnail",
						"http://thumbnails-demo.s3.amazonaws.com/ee410cef-2a44-46ef-878d-172511e54e07.png")
				.put("url", "http://en.wikipedia.org/wiki/Austin_M").put("sharing", "public")
				.put("isFrameBreaker",
						"0")
				.put("user",
						new JsonObject().put("gooruUId", "ee410cef-2a44-46ef-878d-172511e54e07")
								.put("profileImageUrl",
										"http://profile-demo.s3.amazonaws.com/ee410cef-2a44-46ef-878d-172511e54e07.png")
						.put("username", "SachinZ"))
				.put("metadata",
						new JsonObject().put("educationalUse", new JsonArray())
								.put("educationalRole", new JsonArray())
								.put("depthOfKnowledge", new JsonArray().add(166).add(168))
								.put("standards", new JsonArray()).put("readingLevel", new JsonArray())
								.put("advertisementLevel", new JsonArray()).put("hazardLevel", new JsonArray())
								.put("mediaFeatures", new JsonArray()).put("mobileFriendly", new JsonArray())
								.put("publisher", new JsonArray()).put("aggregator", new JsonArray())
								.put("fqdn", new JsonArray()));
		routingContext.response().putHeader("content-type", "application/json");
		routingContext.response().setStatusCode(200);
		routingContext.response().end(result.toString());
	});
	
	//Questions
	executePost(router, "questions");
	executePut(router, "questions/:Id");

	//getQuestionById
	router.route(HttpMethod.GET, ConfigConstants.BASE_PATH + "questions/:questionId").handler(routingContext -> {
		String questionId = routingContext.request().getParam("questionId");
		JsonObject result = new JsonObject().put("id", questionId).put("itemSequence", "1").put("type", "FIB")
				.put("title", "The binary system uses powers of")
				.put("explanation",
						"This is the question related to basics of computer")
				.put("hint",
						new JsonArray()
								.add(new JsonObject()
										.put("hintId",
												"116390")
										.put("hintText", "<p>this is hint<br data-mce-bogus=\"1\"></p>")
										.put("sequence", "1")))
				.put("detail", new JsonArray())
				.put("answer",
						new JsonArray()
								.add(new JsonObject().put("answerId", "10860").put("answerText", "1")
										.put("answerType", "text").put("isCorrect", "true").put("sequence", "1"))
								.add(new JsonObject().put("answerId", "10861").put("answerText", "15")
										.put("answerType", "text").put("isCorrect", "false").put("sequence", "2"))
						.add(new JsonObject().put("answerId", "10862").put("answerText", "1")
								.put("answerType", "text").put("isCorrect", "false").put("sequence", "3"))
						.add(new JsonObject().put("answerId", "10863").put("answerText", "1")
								.put("answerType", "text").put("isCorrect", "false").put("sequence", "4")))
				.put("user",
						new JsonObject().put("gooruUId", "ca56333a-73b8-4e41-a25e-a015fe4276d3")
								.put("profileImageUrl",
										"http://profile-demo.s3.amazonaws.com/ca56333a-73b8-4e41-a25e-a015fe4276d3.png")
						.put("username", "sachin"))
				.put("metadata",
						new JsonObject().put("depthOfKnowledge", new JsonArray().add(166).add(168))
								.put("standards", new JsonArray()).put("21CenturySkills", new JsonArray())
								.put("learningTarget", new JsonArray()));
		;

		routingContext.response().putHeader("content-type", "application/json");
		routingContext.response().setStatusCode(200);
		routingContext.response().end(result.toString());
	});

	// Collections
	executePost(router, "collections"); //Create Collection
	executePut(router, "collections/:Id"); //Update Collection
	executeDelete(router, "collections/:Id"); //Delete Collection
	executePost(router, "collections/:Id/questions"); //Create question in collection
	executeDelete(router,"collections/:collectionId/questions/:Id"); //remove question from collection
	executeDelete(router, "collections/:collectionId/resources/:resourceId/:Id"); //remove resource from collection
	executePut(router, "collections/:Id/questions"); //copy existing question to collection
	executePost(router, "collections/:Id/resources"); //add new resource to collection
	
	//GetCollectionById
	router.route(HttpMethod.PUT, ConfigConstants.BASE_PATH + "collections/:collectionId")
			.handler(routingContext -> {
				String collectionId = routingContext.request().getParam("collectionId");

				JsonObject result = new JsonObject().put("id", collectionId)
						.put("title", "Assessment on OOP introduction")
						.put("thumbnails",
								"http://cdn.goorulearning.org/prod1//f000/2624/0222//109159af-acfc-4cc5-9b4b-ab234c93608b.jpg")
						.put("sharing", "anyonewithlink").put("learningObjective", "This is objective")
						.put("comments_enabled", "true")
						.put("audience", new JsonArray()
								.add(new JsonObject().put("id", "198").put("name", "English Language Learners"))
								.add(new JsonObject().put("id", "199").put("name",
										"Students With Special Needs")))
						.put("collaborator", new JsonArray()
								.add(new JsonObject().put("gooruUid", "398b2cff-fbc7-4ec5-ae85-eab25882cf6b")
										.put("gooruOid", "14d4a284-5b67-45c9-99f9-4e0c174bddea")
										.put("username", "SachinZ601").put("emailId", "sachin@gooru.org")
										.put("status", "active")
										.put("profileImageUrl",
												"http://profile-images-goorulearning-org.s3.amazonaws.com/398b2cff-fbc7-4ec5-ae85-eab25882cf6b.png")
								.put("associatedDate", "1447751112000"))
								.add(new JsonObject().put("gooruUid", "398b2cff-fbc7-4ec5-ae85-eab25882cf6b")
										.put("gooruOid", "14d4a284-5b67-45c9-99f9-4e0c174bddea")
										.put("username", "PersonZ601").put("emailId", "Sample@gooru.org")
										.put("status", "active")
										.put("profileImageUrl",
												"http://profile-images-goorulearning-org.s3.amazonaws.com/398b2cff-fbc7-4ec5-ae85-eab25882cf6b.png")
										.put("associatedDate", "1447751112000")))
						.put("metadata",
								new JsonObject().put("depthOfKnowledge", new JsonArray().add(166).add(168))
										.put("standards", new JsonArray()).put("21CenturySkills", new JsonArray())
										.put("learningTarget", new JsonArray()))
						.put("resource",
								new JsonArray()
										.add(new JsonObject().put("type", "question")
												.put("title", "This is first question").put("thumbnail",
														"http: //thumbnails-demo.s3.amazonaws.com/ee410cef-2a44-46ef-878d-172511e54e07.png"))
								.add(new JsonObject().put("type", "resource/url")
										.put("title", "This is first resource")
										.put("url", "https: //docs.oracle.com/javase/tutorial/java/concepts/")
										.put("thumbnail",
												"http: //thumbnails-demo.s3.amazonaws.com/ee410cef-2a44-46ef-878d-172511e54e07.png")));

				routingContext.response().putHeader("content-type", "application/json");
				routingContext.response().setStatusCode(200);
				routingContext.response().end(result.toString());
		});
		
	//Courses
	executePost(router, "courses"); //Create Course
	executePut(router, "courses"); //copy Course
	executeDelete(router, "courses/:Id"); //delete course
	executePut(router, "courses/:Id"); //update course
	executePut(router, "courses/:Id/order"); //reorder units in courses
	executePut(router, "courses/:Id/collaborators"); //update Collaborators
		
	//getCourseById  - /courses/{course-id}
	router.route(HttpMethod.GET, ConfigConstants.BASE_PATH + "courses/:courseId").handler(routingContext -> {
		String courseId = routingContext.request().getParam("courseId");
		JsonObject result = new JsonObject().put("id", courseId)
				.put("title", "Understanding Basics of Core Java")
				.put("sharing", "private")
				.put("thumbnail", "http://thumbnails-demo.s3.amazonaws.com/ee410cef-2a44-46ef-878d-172511e54e07.png")
				.put("audience", new JsonArray()
						.add(new JsonObject()
								.put("id", "197")
								.put("name","Students Above Grade Level")))
				.put("taxonomyCourse", new JsonArray()
						.add(new JsonObject()
								.put("id", "228")
								.put("name","Computer Science")
								.put("subjectId", "5"))
						.add(new JsonObject()
								.put("id", "75")
								.put("name","Grade 7 Science")
								.put("subjectId", "4")))
				.put("summary", new JsonObject().put("unitCount", "1"))
				.put("user",
						new JsonObject().put("gooruUId", "ca56333a-73b8-4e41-a25e-a015fe4276d3")
								.put("profileImageUrl", "http://profile-demo.s3.amazonaws.com/ca56333a-73b8-4e41-a25e-a015fe4276d3.png")
								.put("username", "sachin"))				
				;
		
		routingContext.response().putHeader("content-type", "application/json");
		routingContext.response().setStatusCode(200);
		routingContext.response().end(result.toString());
	});
	
	//listCoursesByUser
	router.route(HttpMethod.GET, ConfigConstants.BASE_PATH + "courses").handler(routingContext -> {
		JsonArray result = new JsonArray().add(new JsonObject()
								.put("id", "ee410cef-2a44-46ef-878d-172511e54e07")
								.put("title","Computer Science")
								.put("sharing", "private")
								.put("thumbnail", "http://thumbnails-demo.s3.amazonaws.com/ee410cef-2a44-46ef-878d-172511e54e07.png")
								.put("user",new JsonObject().put("gooruUId", "ca56333a-73b8-4e41-a25e-a015fe4276d3")
												.put("profileImageUrl", "http://profile-demo.s3.amazonaws.com/ca56333a-73b8-4e41-a25e-a015fe4276d3.png")
												.put("username", "sachin")))
							.add(new JsonObject()
									.put("id", "ee410cef-2a44-46ef-878d-172511e54e07")
									.put("title","Computer Science II")
									.put("sharing", "private")
									.put("thumbnail", "http://thumbnails-demo.s3.amazonaws.com/ee410cef-2a44-46ef-878d-172511e54e07.png")
									.put("user",new JsonObject().put("gooruUId", "ca56333a-73b8-4e41-a25e-a015fe4276d3")
													.put("profileImageUrl", "http://profile-demo.s3.amazonaws.com/ca56333a-73b8-4e41-a25e-a015fe4276d3.png")
													.put("username", "sachin")))	
				;
		
		routingContext.response().putHeader("content-type", "application/json");
		routingContext.response().setStatusCode(200);
		routingContext.response().end(result.toString());
	});
	
	//listCollaboratorsByCourse
	router.route(HttpMethod.GET, ConfigConstants.BASE_PATH + "courses/:courseId/collaborators").handler(routingContext -> {
		JsonArray result = new JsonArray().add(new JsonObject()
									.put("gooruUid", "ee410cef-2a44-46ef-878d-172511e54e07")
									.put("gooruOid", "14d4a284-5b67-45c9-99f9-4e0c174bddea")
									.put("username","SachinZ601")
									.put("emailId", "sachin@gooru.org")
									.put("status", "active")
									.put("profileImageUrl", "http://profile-images-goorulearning-org.s3.amazonaws.com/398b2cff-fbc7-4ec5-ae85-eab25882cf6b.png")
									.put("associatedDate", "1447751112000"))
							.add(new JsonObject()
									.put("gooruOid", "14d4a284-5b67-45c9-99f9-4e0c174bddea")
									.put("emailId", "sachin@gooru.org")
									.put("status", "pending")
									.put("associatedDate", "1447751112000"))							
								;
		
		routingContext.response().putHeader("content-type", "application/json");
		routingContext.response().setStatusCode(200);
		routingContext.response().end(result.toString());
	});
		
	//Units
	executePost(router, "courses/:Id/units"); //Create Unit
	executePut(router, "courses/:courseId/units/:Id"); //update unit
	executeDelete(router, "courses/:courseId/units/:Id"); //delete unit
	executePut(router, "courses/:Id/units"); //copy unit
	executePut(router, "courses/:courseId/units/:Id/order"); //reorder lessons in unit

	// getUnitById - /courses/{course-id}/units/{unit-id}
	router.route(HttpMethod.GET, ConfigConstants.BASE_PATH + "courses/:courseId/units/:unitId").handler(routingContext -> {
		String courseId = routingContext.request().getParam("courseId");
		String unitId = routingContext.request().getParam("unitId");
		JsonObject result = new JsonObject().put("summary", new JsonObject().put("lessonCount", "2"))
				.put("parentGooruOid", courseId)
				.put("itemSequence", "1")
				.put("lastModifiedUserUid", "eea60d3e-8d7d-432f-a9e0-235545d87893")
				.put("title", "OOPS").put("sharing", "private")
				.put("ideas", "This unit is based on OOP")
				.put("lastModified", "1447908014000")
				.put("questions", "What is OOP and what are the benefits of it")
				.put("gooruOid", unitId)
				.put("taxonomyCourse", new JsonArray()
						.add(new JsonObject()
								.put("id", "75")
								.put("name","Arabic 1")
								.put("subjectId", "5")))
				.put("subdomain", new JsonArray()
						.add(new JsonObject()
								.put("id", "963")
								.put("name","Engineering Design")
								.put("subjectId", "2")
								.put("courseId", "26"))
						.add(new JsonObject()
								.put("id", "1107")
								.put("name","Economics: Economic Decision Making")
								.put("subjectId", "4")
								.put("courseId", "60")))
				.put("user",
						new JsonObject().put("gooruUId", "ca56333a-73b8-4e41-a25e-a015fe4276d3")
								.put("profileImageUrl", "http://profile-demo.s3.amazonaws.com/ca56333a-73b8-4e41-a25e-a015fe4276d3.png")
								.put("username", "sachin"))
				;
		
		routingContext.response().putHeader("content-type", "application/json");
		routingContext.response().setStatusCode(200);
		routingContext.response().end(result.toString());
	});
			
	//Lessons
	executePost(router, "courses/:courseId/units/:Id/lessons"); //Create Lesson
	executePut(router, "courses/:courseId/units/:Id/lessons"); //copy lesson
	executeDelete(router, "courses/:courseId/units/:unitId/lessons/:Id"); //delete lesson
	executePut(router, "courses/:courseId/units/:unitId/lessons/:Id"); //update Lesson
	executePut(router, "courses/:courseId/units/:Id/lessons/:lessonId/order"); //reorder lessons in units
	
	//getLessonById
	router.route(HttpMethod.GET, ConfigConstants.BASE_PATH + "courses/:courseId/units/:unitId/lessons/:lessonId").handler(routingContext -> {
		String unitId = routingContext.request().getParam("unitId");
		String lessonId = routingContext.request().getParam("lessonId");
		JsonObject result = new JsonObject().put("summary", new JsonObject()
				.put("collectionCount", "2").put("assessmentCount", "0"))
				.put("parentGooruOid", lessonId)
				.put("itemSequence", "1")
				.put("lastModifiedUserUid", "eea60d3e-8d7d-432f-a9e0-235545d87893")
				.put("title", "Lesson-2").put("sharing", "private")
				.put("lastModified", "1447908014000")
				.put("standards", new JsonArray()
						.add(new JsonObject()
								.put("id", "77505")
								.put("rootNodeId","77271")
								.put("code", "NGSS-MS-ETS1-1"))
						.add(new JsonObject()
								.put("id", "77508")
								.put("rootNodeId","77871")
								.put("code", "C3.D3.1.K-2")))
				.put("gooruOid", lessonId)
				.put("taxonomyCourse", new JsonArray()
						.add(new JsonObject()
								.put("id", "113")
								.put("name","Physical Education")
								.put("subjectId", "5")))
				.put("user",
						new JsonObject().put("gooruUId", "ca56333a-73b8-4e41-a25e-a015fe4276d3")
								.put("profileImageUrl", "http://profile-demo.s3.amazonaws.com/ca56333a-73b8-4e41-a25e-a015fe4276d3.png")
								.put("username", "sachin"))
				;
		
		
		routingContext.response().putHeader("content-type", "application/json");
		routingContext.response().setStatusCode(200);
		routingContext.response().end(result.toString());
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
}
