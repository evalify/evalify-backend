package com.evalify.evalifybackend.course.exception

import com.evalify.evalifybackend.core.exception.BusinessLogicException

/** Exception thrown when there's an issue with course student operations */
class CourseStudentException(message: String) : BusinessLogicException(message)
