package com.evalify.evalifybackend.department.exception

import java.util.UUID

class DepartmentNotFoundException(departmentId: UUID) : RuntimeException("Department with id $departmentId not found")

class DepartmentAlreadyExistsException(departmentName: String) : RuntimeException("Department with name '$departmentName' already exists")

class DepartmentValidationException(message: String) : RuntimeException(message)

class DepartmentServiceException(message: String) : RuntimeException(message)

class DepartmentDeleteException(message: String) : RuntimeException(message)
