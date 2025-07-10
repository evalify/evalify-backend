package com.evalify.evalifybackend.s3.exception

class InvalidFileTypeException(fileType: String?) : RuntimeException("File type not allowed: $fileType")
