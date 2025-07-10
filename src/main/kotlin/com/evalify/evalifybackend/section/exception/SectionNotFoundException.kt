package com.evalify.evalifybackend.section.exception

class SectionNotFoundException(sectionId: String) : RuntimeException("Section with id '$sectionId' not found")
