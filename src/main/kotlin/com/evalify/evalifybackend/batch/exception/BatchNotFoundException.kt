package com.evalify.evalifybackend.batch.exception

import com.evalify.evalifybackend.core.exception.NotFoundException
import java.util.UUID

/** Exception thrown when a batch is not found */
class BatchNotFoundException(batchId: UUID) : NotFoundException("Batch with ID $batchId not found")