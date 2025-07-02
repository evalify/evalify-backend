package com.evalify.evalifybackend.bank.exception

import com.evalify.evalifybackend.core.exception.ConflictException

/** Exception thrown when attempting to create a topic with a duplicate name */
class TopicDuplicateNameException(topicName: String, bankId: String) :
        ConflictException("Topic with name '$topicName' already exists in bank $bankId")
