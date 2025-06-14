package com.evalify.evalifybackend.core.config

import io.minio.MinioClient
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class MinioConfig {

    @Value("\${minio.url}")
    private lateinit var minioUrl: String

    @Value("\${minio.access-key}")
    private lateinit var minioAccessKey: String

    @Value("\${minio.secret-key}")
    private lateinit var minioSecretKey: String

    @Value("\${minio.bucket-name}")
    private lateinit var minioBucketName: String

    @Bean
    fun minioClient(): MinioClient {
        return MinioClient.builder()
            .endpoint(minioUrl)
            .credentials(minioAccessKey, minioSecretKey)
            .build()
    }

    @Bean
    fun minioBucketName(): String {
        return minioBucketName
    }
}
