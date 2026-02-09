package com.toy.modulithdemo.user;

import com.toy.modulithdemo.user.dto.UserAddRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;

    public User create(UserAddRequest userAddRequest) {
        User user = User.create(userAddRequest.getUserId(), userAddRequest.getPw());
        return userRepository.save(user);
    }
}
