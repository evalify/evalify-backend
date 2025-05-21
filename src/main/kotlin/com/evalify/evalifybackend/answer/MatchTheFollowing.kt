package com.evalify.evalifybackend.answer

data class MatchPair(val id: String, val text: String)

data class MatchAnswer(val keyId: String, val valueId: String)

data class MatchTheFollowing(
    val keys: List<MatchPair>,
    val values: List<MatchPair>,
    val answer: List<Map<String, String>>
) : QuestionAnswer
