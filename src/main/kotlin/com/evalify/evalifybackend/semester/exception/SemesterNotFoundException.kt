package com.evalify.evalifybackend.semester.exception

class SemesterNotFoundException(semesterId: String) : RuntimeException("Semester with id '$semesterId' not found")
