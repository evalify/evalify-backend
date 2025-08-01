package com.evalify.evalifybackend.s3.controller

import com.evalify.evalifybackend.s3.service.MinioService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.net.URI

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
    /**
     * Uploads a file to Minio object storage.
     *
     * Use Cases:
     * - Uploading quiz attachments and resources
     * - Storing student submission files
     * - Managing course materials and documents
     * - Handling media files for questions
     *
     * @param file The MultipartFile to be uploaded
     * @param customName Optional custom name for the stored file
     * @return ResponseEntity containing the object name and URL in Minio storage
     * @throws IllegalArgumentException if file validation fails
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
     * @param url The URL of the object to delete
     */
    /**
     * Deletes a file from Minio object storage.
     *
     * Use Cases:
     * - Removing outdated quiz resources
     * - Cleaning up temporary files
     * - Managing storage space
     * - Handling file version updates
     *
     * @param objectName The unique identifier of the file to be deleted
     * @return ResponseEntity with success message or error details
     */
    @DeleteMapping("/delete")
    fun deleteFile(@RequestParam("url") url: String): ResponseEntity<Map<String, String>> {
        return try {
            val objectName = extractObjectNameFromUrl(url)
            minioService.deleteFile(objectName)
            ResponseEntity.ok(mapOf("message" to "File deleted successfully"))
        } catch (e: IllegalArgumentException) {
            ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(mapOf("error" to e.message.toString()))
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(mapOf("error" to "Failed to delete file: ${e.message}"))
        }
    }

    /**
     * Extract object name from Minio URL
     * @param url The full URL of the object
     * @return The object name (key)
     */
    private fun extractObjectNameFromUrl(url: String): String {
        return try {
            val uri = URI(url)
            val path = uri.path

            // Remove the bucket name from the path
            // Expected format: /bucket-name/object-name
            val pathParts = path.split("/").filter { it.isNotEmpty() }

            if (pathParts.size < 2) {
                throw IllegalArgumentException("Invalid URL format: cannot extract object name")
            }

            // Return everything after the bucket name as the object name
            pathParts.drop(1).joinToString("/")
        } catch (e: Exception) {
            throw IllegalArgumentException("Invalid URL format: ${e.message}")
        }
    }
}
