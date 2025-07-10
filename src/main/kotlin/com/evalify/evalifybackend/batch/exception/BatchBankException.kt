package com.evalify.evalifybackend.batch.exception

import com.evalify.evalifybackend.core.exception.BusinessLogicException
import java.util.UUID

/** Exception thrown when there's an issue with batch bank operations */
class BatchBankException(message: String, val batchId: UUID? = null, val bankId: UUID? = null) :
        BusinessLogicException(message)