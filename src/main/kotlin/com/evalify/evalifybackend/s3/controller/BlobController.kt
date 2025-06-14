package com.evalify.evalifybackend.s3.controller

import com.evalify.evalifybackend.s3.service.MinioService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
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
        return try {
            if (file.isEmpty) {
                return ResponseEntity.badRequest().body(mapOf("error" to "File is empty"))
            }

            val objectName = minioService.uploadFile(file, customName)
            val objectUrl = minioService.getObjectUrl(objectName)
            ResponseEntity.ok(mapOf("objectName" to objectName, "url" to objectUrl))
        } catch (e: IllegalArgumentException) {
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body(mapOf("error" to e.message.toString()))
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(mapOf("error" to "Failed to upload file: ${e.message}"))
        }
    }

    /**
     * Delete a file from Minio storage
     * @param objectName The name of the object to delete
     */
    @DeleteMapping("/delete")
    fun deleteFile(@RequestParam("objectName") objectName: String): ResponseEntity<Map<String, String>> {
        return try {
            minioService.deleteFile(objectName)
            ResponseEntity.ok(mapOf("message" to "File deleted successfully"))
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(mapOf("error" to "Failed to delete file: ${e.message}"))
        }
    }
}
