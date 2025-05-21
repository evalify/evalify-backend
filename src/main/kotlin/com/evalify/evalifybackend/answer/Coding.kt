package com.evalify.evalifybackend.answer

data class FunctionParam(val param: String, val type: String)

data class TestCase(val input: List<Any>, val expected: Any)

data class Coding(
    val driverCode: String,
    val boilerCode: String,
    val functionName: String,
    val returnType: String,
    val params: List<FunctionParam>,
    val testcases: List<TestCase>,
    val language: List<String>,
    val answer: String
) : QuestionAnswer