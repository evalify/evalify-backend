package com.evalify.evalifybackend.s3.exception

class FileNotFoundException(objectName: String) : RuntimeException("File with name '$objectName' not found")
