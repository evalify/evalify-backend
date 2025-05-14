package com.evalify.evalifybackend.batch.repository

import com.evalify.evalifybackend.batch.domain.Batch
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface BatchRepository : JpaRepository<Batch,UUID>{
}