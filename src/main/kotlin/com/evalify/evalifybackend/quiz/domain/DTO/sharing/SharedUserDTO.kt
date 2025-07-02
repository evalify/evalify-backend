package com.evalify.evalifybackend.quiz.domain.DTO.sharing

enum class SharedTags {
    OWNER,
    SHARED
}

data class SharedUserDTO(val user: SimpleUserDTO?, val tag: SharedTags) {}
