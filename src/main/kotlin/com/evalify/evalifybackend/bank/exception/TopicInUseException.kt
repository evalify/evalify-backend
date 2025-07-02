package com.evalify.evalifybackend.bank.exception

import com.evalify.evalifybackend.core.exception.ConflictException

/** Exception thrown when attempting to delete a topic that has dependencies */
class TopicInUseException(topicId: String, dependencyType: String) :
        ConflictException(
                "Topic $topicId cannot be deleted as it is being used by $dependencyType"
        )
