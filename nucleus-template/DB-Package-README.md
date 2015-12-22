DB Package details
==============

# Names are self descriptive :)

copyQuestion(router, "questions/:questionId" );
copyQuestionToAssessment(router, "assessments/:Id/questions/:qId");
copyAssessment(router, "assessments/:Id");
copyQuestionToCollection(router, "collections/:Id/questions/:qId");
copyResourceToCollection(router, "collections/:Id/resources/:rId");
copyCollection(router, "collections/:Id");
copyLessonToUnit(router, "courses/:courseId/units/:unitId/lessons/:lessonId/:targetCourseId/:targetUnitId");
copyUnitToCourse(router, "courses/:courseId/units/:unitId/:targetCourseId");
copyCourse(router, "courses-copy/:courseId");

# Pending tasks
* interface and implementation details need to be corrected - as of now all interfaces and implementations are public and in same package - need to refactor them in different package and a builder class with proper exposure to the methods 
( private/public/protected)
* DB structure is as on 14 Dec 2015
* Any DB changes would need changes to all queries
* Thumbnail generation needs to be taken care , wherever needed
* Duplication checks need to be done -- pkey violation checks
* Need to check all fields like metadata, standards, etc - which all should be null and which should not be while copying row data
* Exceptional and error handling needs to reviewed and reworked
* Bootstrap functions ( all Http.GET methods are written to test them tested )
* Mode of testing done was from browser - path will be 
http://localhost:8080/api/nucleus/v1.0/ + parameter from the function call as above

Eg:
copyQuestion(router, "questions/:questionId" );
URL - http://localhost:8080/api/nucleus/v1.0/questions/:questionId

copyQuestionToAssessment(router, "assessments/:Id/questions/:qId");
URL - http://localhost:8080/api/nucleus/v1.0/assessments/:Id/questions/:qId

copyAssessment(router, "assessments/:Id");
URL - http://localhost:8080/api/nucleus/v1.0/assessments/:Id

copyQuestionToCollection(router, "collections/:Id/questions/:qId");
URL - http://localhost:8080/api/nucleus/v1.0/collections/:Id/questions/:qId

copyResourceToCollection(router, "collections/:Id/resources/:rId");
URL - http://localhost:8080/api/nucleus/v1.0/collections/:Id/resources/:rId

copyCollection(router, "collections/:Id");
URL - http://localhost:8080/api/nucleus/v1.0/collections/:Id

copyLessonToUnit(router, "courses/:courseId/units/:unitId/lessons/:lessonId/:targetCourseId/:targetUnitId");
URL - http://localhost:8080/api/nucleus/v1.0/courses/:courseId/units/:unitId/lessons/:lessonId/:targetCourseId/:targetUnitId

copyUnitToCourse(router, "courses/:courseId/units/:unitId/:targetCourseId");
URL - http://localhost:8080/api/nucleus/v1.0/courses/:courseId/units/:unitId/:targetCourseId

copyCourse(router, "courses-copy/:courseId");
URL - http://localhost:8080/api/nucleus/v1.0/courses-copy/:courseId


