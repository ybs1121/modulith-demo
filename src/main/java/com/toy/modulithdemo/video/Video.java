package com.toy.modulithdemo.video;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Video {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long productId;

    private String originalFilename;
    private String storedFilename;
    private String filePath;
    private Long fileSize;

    private LocalDateTime uploadedAt;

    public Video(Long productId, String originalFilename, String storedFilename, String filePath, Long fileSize) {
        this.productId = productId;
        this.originalFilename = originalFilename;
        this.storedFilename = storedFilename;
        this.filePath = filePath;
        this.fileSize = fileSize;
        this.uploadedAt = LocalDateTime.now();
    }
}