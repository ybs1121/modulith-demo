package com.toy.modulithdemo.video;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value; // 필요 없어질 수도 있음
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VideoService {

    private final VideoRepository videoRepository;

    // 이 값은 이제 안 써도 되지만, 혹시 모르니 둡니다.
    @Value("${file.upload-dir}")
    private String uploadDir;

    @Transactional
    public Long uploadVideo(Long productId, MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("파일이 비어있습니다.");
        }

        String projectPath = System.getProperty("user.dir");

        String uploadPath = projectPath + File.separator + "uploads";

        File directory = new File(uploadPath);
        if (!directory.exists()) {
            directory.mkdirs(); // 폴더가 없으면 생성
        }

        String originalFilename = file.getOriginalFilename();
        String storedFilename = UUID.randomUUID() + "_" + originalFilename;

        String fullPath = uploadPath + File.separator + storedFilename;

        // 4. 파일 로컬 저장
        file.transferTo(new File(fullPath));

        // 5. DB에 메타데이터 저장
        Video video = new Video(
                productId,
                originalFilename,
                storedFilename,
                fullPath,
                file.getSize()
        );

        return videoRepository.save(video).getId();
    }
}
