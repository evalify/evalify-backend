package com.evalify.evalifybackend.bank.exception

import com.evalify.evalifybackend.core.exception.NotFoundException

/**
 * Exception thrown when a topic is not found
 */
class TopicNotFoundException(topicId: String) : NotFoundException("Topic with ID $topicId not found")
