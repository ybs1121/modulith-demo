package com.toy.modulithdemo.video;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

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
            Long videoId = videoService.uploadVideo(productId, file);
            return videoId;
        } catch (IOException e) {
            log.error("e : {}", e.toString());
            return null;
        }
    }
}
