package com.evalify.evalifybackend.course.domain.DTO

import java.util.UUID

data class UpdateCourseBatchDTO (
    val batchId:List<UUID>
){
}

