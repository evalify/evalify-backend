package com.evalify.evalifybackend.bank.domain.DTO.questionTypes

import com.evalify.evalifybackend.bank.domain.DTO.bank.BankQuestionsReturnDTO
import com.evalify.evalifybackend.bank.domain.DTO.topic.ReturnTopicDTO
import com.evalify.evalifybackend.questions.domain.Difficulty
import com.evalify.evalifybackend.questions.domain.QuestionTypes
import com.evalify.evalifybackend.questions.domain.Taxonomy
import com.evalify.evalifybackend.quiz.domain.DTO.questionTypes.MatchShuffleDTO
import com.evalify.evalifybackend.quiz.domain.DTO.questionTypes.Pair
import com.evalify.evalifybackend.quiz.question.domain.MatchPair
import java.util.UUID

class MatchBankReturnDTO(
        val question: String,
        override val questionId : UUID?,
        override val hint: String?,
        val keyValues:MatchShuffleDTO,
        val matchPair: MutableList<MatchPair>?,
        override val marks: Int,
        override val bloomsTaxonomy: Taxonomy?,
        override val co: Int,
        override val difficulty: Difficulty?,
        val explanation: String?,
        val type: QuestionTypes?,
        override val topics: List<ReturnTopicDTO>

) :
        BankQuestionsReturnDTO(
                hint = hint,
                marks = marks,
                bloomsTaxonomy = bloomsTaxonomy,
                co = co,
                difficulty = difficulty,
                topics = topics,
                questionId = questionId

        )
