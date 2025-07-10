package com.evalify.evalifybackend.lab.exception

import java.util.UUID

class LabNotFoundException(labId: UUID) : RuntimeException("Lab with id $labId not found")
