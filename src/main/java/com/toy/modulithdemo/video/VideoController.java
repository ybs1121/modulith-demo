package com.toy.modulithdemo.video;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/video")
public class VideoController {

    private final VideoService videoService;

    @PostMapping("/{productId}/videos")
    public Long uploadVideo(
            @PathVariable Long productId,
            @RequestParam("file") MultipartFile file
    ) {
        try {
            return videoService.uploadVideo(productId, file);
        } catch (IOException e) {
            log.error("e : {}", e.toString());
            return null;
        }
    }


    // 2. [추가] 해당 상품의 비디오 목록 조회 API
    @GetMapping("/{productId}/videos")
    public ResponseEntity<List<Video>> getVideos(@PathVariable Long productId) {
        return ResponseEntity.ok(videoService.getVideosByProductId(productId));
    }

    @GetMapping("/videos/stream/{filename}")
    public ResponseEntity<Resource> streamVideo(@PathVariable String filename) throws MalformedURLException {
        Resource resource = videoService.loadVideoAsResource(filename);


        ContentDisposition contentDisposition = ContentDisposition.builder("inline")
                .filename(filename, StandardCharsets.UTF_8) // 여기서 UTF-8 인코딩 처리됨
                .build();

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("video/mp4"))
                .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition.toString())
                .body(resource);
    }
}
