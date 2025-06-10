package com.evalify.evalifybackend.department.repository

import com.evalify.evalifybackend.batch.domain.Batch
import com.evalify.evalifybackend.department.domain.Department
import com.evalify.evalifybackend.department.domain.dto.DepartmentBatchResponse
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.data.rest.core.annotation.RepositoryRestResource
import java.util.*

@RepositoryRestResource(path = "department")
interface DepartmentRepository: JpaRepository<Department, UUID> {

    @Query("SELECT b FROM Batch b WHERE b.department.id = :departmentId")
    fun findBatchesByDepartmentId(@Param("departmentId") departmentId: UUID): List<Batch>

    @Query("SELECT d FROM Department d WHERE LOWER(d.name) LIKE LOWER(CONCAT('%', :query, '%'))")
    fun findByNameContainingIgnoreCase(@Param("query") query: String, pageable: Pageable): Page<Department>
    
    @Query("SELECT d.id FROM Department d WHERE LOWER(d.name) LIKE LOWER(CONCAT('%', :query, '%'))")
    fun findDepartmentIdsByNameContainingIgnoreCase(@Param("query") query: String, pageable: Pageable): Page<UUID>@Query("SELECT DISTINCT d FROM Department d LEFT JOIN FETCH d.batches")
    fun findAllWithBatches(): List<Department>

    @Query("SELECT d.id FROM Department d")
    fun findAllDepartmentIds(pageable: Pageable): Page<UUID>
}

