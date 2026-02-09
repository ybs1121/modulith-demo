package com.toy.modulithdemo.user;

import com.toy.modulithdemo.shared.sse.emitter.SseEmitterManager;
import com.toy.modulithdemo.user.dto.UserAddRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;


@RequiredArgsConstructor
@RestController
@RequestMapping("/users")
public class UserController {


    private final UserService userService;

    private final SseEmitterManager sseEmitterManager;

    @PostMapping("")
    public User create(UserAddRequest userAddRequest) {
        return userService.create(userAddRequest);
    }

    @GetMapping(value = "/subscribe/{userKey}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribe(@PathVariable Long userKey) {
        return sseEmitterManager.subscribe(userKey);
    }

}
