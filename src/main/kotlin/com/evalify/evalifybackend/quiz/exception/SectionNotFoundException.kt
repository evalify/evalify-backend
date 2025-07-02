package com.evalify.evalifybackend.quiz.exception

import com.evalify.evalifybackend.core.exception.NotFoundException

/** Exception thrown when a section is not found */
class SectionNotFoundException(sectionId: String) :
        NotFoundException("Section with ID $sectionId not found")
