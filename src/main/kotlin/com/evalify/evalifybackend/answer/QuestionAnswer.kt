package com.evalify.evalifybackend.answer

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type", include = JsonTypeInfo.As.PROPERTY)
@JsonSubTypes(
    JsonSubTypes.Type(value = MCQ::class, name = "MCQ"),
    JsonSubTypes.Type(value = MMCQ::class, name = "MMCQ"),
    JsonSubTypes.Type(value = TrueFalse::class, name = "TRUEFALSE"),
    JsonSubTypes.Type(value = FillUp::class, name = "FILL_UP"),
    JsonSubTypes.Type(value = MatchTheFollowing::class, name = "MATCHTHEFOLLOWING"),
    JsonSubTypes.Type(value = Descriptive::class, name = "DESCRIPTIVE"),
    JsonSubTypes.Type(value = FileUpload::class, name = "FILL_UPLOAD"),
    JsonSubTypes.Type(value = Coding::class, name = "CODING")
)
sealed interface QuestionAnswer