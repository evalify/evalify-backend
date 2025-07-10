package com.evalify.evalifybackend.batch.exception

import com.evalify.evalifybackend.core.exception.BusinessLogicException
import java.util.UUID

/** Exception thrown when there's an issue with batch department operations */
class BatchDepartmentException(message: String, val batchId: UUID? = null, val departmentId: UUID? = null) :
        BusinessLogicException(message)