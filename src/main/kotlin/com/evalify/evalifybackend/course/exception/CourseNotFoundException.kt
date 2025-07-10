package com.evalify.evalifybackend.course.exception

import com.evalify.evalifybackend.core.exception.NotFoundException
import java.util.UUID

/** Exception thrown when a course is not found */
class CourseNotFoundException(courseId: UUID) : NotFoundException("Course not found with ID: $courseId")

/** Exception thrown when a user is not found */
class UserNotFoundException(userId: String) : RuntimeException("User with id $userId not found")

/** Exception thrown when multiple users are not found */
class UsersNotFoundException(userIds: List<String>) : RuntimeException("Users with ids ${userIds.joinToString(", ")} not found")

/** Exception thrown when a batch is not found */
class BatchNotFoundException(batchId: UUID) : RuntimeException("Batch with id $batchId not found")

/** Exception thrown when multiple batches are not found */
class BatchesNotFoundException(batchIds: List<UUID>) : RuntimeException("Batches with ids ${batchIds.joinToString(", ")} not found")
