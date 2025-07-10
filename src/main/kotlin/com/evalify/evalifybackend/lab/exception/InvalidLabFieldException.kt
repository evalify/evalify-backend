package com.evalify.evalifybackend.lab.exception

class InvalidLabFieldException(fieldName: String, value: String) : RuntimeException("Invalid value '$value' for field '$fieldName'")
