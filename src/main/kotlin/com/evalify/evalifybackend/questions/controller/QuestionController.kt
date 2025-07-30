package com.evalify.evalifybackend.questions.controller

import com.evalify.evalifybackend.bank.domain.DTO.questionTypes.PairDTO
import com.evalify.evalifybackend.bank.domain.DTO.questionTypes.coding.CreatePairDTO
import com.evalify.evalifybackend.questions.service.QuestionService
import com.evalify.evalifybackend.quiz.domain.DTO.questionTypes.Pair
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID


@RestController
@RequestMapping("/api/questions")
class QuestionController(val questionService : QuestionService)  {

    @PostMapping("/match/pair")
    fun createPair(@RequestBody dto: CreatePairDTO): ResponseEntity<PairDTO>{
        val pair = questionService.createPair(dto.text)
        return ResponseEntity.ok(pair)
    }
}