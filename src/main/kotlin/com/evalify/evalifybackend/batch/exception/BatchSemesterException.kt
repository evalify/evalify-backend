package com.evalify.evalifybackend.batch.exception

import com.evalify.evalifybackend.core.exception.BusinessLogicException
import java.util.UUID

/** Exception thrown when there's an issue with batch semester operations */
class BatchSemesterException(message: String, val batchId: UUID? = null) :
        BusinessLogicException(message)