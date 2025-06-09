package com.evalify.evalifybackend.course.repository

import com.evalify.evalifybackend.course.domain.Course
import com.evalify.evalifybackend.user.domain.User
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.data.rest.core.annotation.RepositoryRestResource
import java.util.UUID


@RepositoryRestResource(path = "courses")
interface CourseRepository : JpaRepository<Course, UUID> {

    @Query("SELECT c FROM Course c WHERE c.semester.isActive = true")
    fun findCoursesByActiveSemesters(pageable: Pageable): Page<Course>

    @Query("SELECT c FROM Course c WHERE c.semester.isActive = true AND :instructor MEMBER OF c.instructors")
    fun findCoursesByActiveSemestersAndInstructor(@Param("instructor") instructor: User, pageable: Pageable): Page<Course>
    @Query("SELECT c FROM Course c WHERE c.semester.isActive = true AND (LOWER(c.name) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(c.code) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(c.description) LIKE LOWER(CONCAT('%', :query, '%')))")
    fun searchCoursesByActiveSemesters(@Param("query") query: String, pageable: Pageable): Page<Course>

    @Query("SELECT c FROM Course c WHERE c.semester.isActive = true AND :instructor MEMBER OF c.instructors AND (LOWER(c.name) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(c.code) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(c.description) LIKE LOWER(CONCAT('%', :query, '%')))")
    fun searchCoursesByActiveSemestersAndInstructor(@Param("instructor") instructor: User, @Param("query") query: String, pageable: Pageable): Page<Course>

    @Query("SELECT c FROM Course c WHERE c.semester.isActive = true AND :student MEMBER OF c.students")
    fun findCoursesByActiveSemestersAndStudent(@Param("student") student: User, pageable: Pageable): Page<Course>
}