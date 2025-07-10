package com.evalify.evalifybackend.s3.controller

import com.evalify.evalifybackend.s3.service.MinioService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/blob")
class BlobController @Autowired constructor(
    private val minioService: MinioService
) {

    /**
     * Upload a file to Minio storage
     * @param file The file to upload
     * @param customName Optional custom name for the file
     * @return The object name (key) and URL in Minio
     */
    @PostMapping("/upload")
    fun uploadFile(
        @RequestParam("file") file: MultipartFile,
        @RequestParam("customName", required = false) customName: String?
    ): ResponseEntity<Map<String, String>> {
        val objectName = minioService.uploadFile(file, customName)
        val objectUrl = minioService.getObjectUrl(objectName)
        return ResponseEntity.ok(mapOf("objectName" to objectName, "url" to objectUrl))
    }

    /**
     * Delete a file from Minio storage
     * @param objectName The name of the object to delete
     */
    @DeleteMapping("/delete")
    fun deleteFile(@RequestParam("objectName") objectName: String): ResponseEntity<Map<String, String>> {
        minioService.deleteFile(objectName)
        return ResponseEntity.ok(mapOf("message" to "File deleted successfully"))
    }

    /**
     * Get a pre-signed URL for accessing a file
     * @param objectName The name of the object
     * @param expirySeconds Optional expiry time in seconds (default: 7 days)
     */
    @GetMapping("/url")
    fun getFileUrl(
        @RequestParam("objectName") objectName: String,
        @RequestParam("expirySeconds", required = false, defaultValue = "604800") expirySeconds: Int
    ): ResponseEntity<Map<String, String>> {
        val url = minioService.getObjectUrl(objectName, expirySeconds)
        return ResponseEntity.ok(mapOf("url" to url))
    }

    /**
     * Check if a file exists
     * @param objectName The name of the object to check
     */
    @GetMapping("/exists")
    fun fileExists(@RequestParam("objectName") objectName: String): ResponseEntity<Map<String, Boolean>> {
        // This will throw FileNotFoundException if the file doesn't exist
        minioService.getObjectUrl(objectName, 1) // Short expiry just to check existence
        return ResponseEntity.ok(mapOf("exists" to true))
    }
}
