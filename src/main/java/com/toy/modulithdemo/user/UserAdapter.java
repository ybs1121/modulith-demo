package com.toy.modulithdemo.user;

import com.toy.modulithdemo.order.port.UserPort;
import com.toy.modulithdemo.user.constant.UserErrorCode;
import com.toy.modulithdemo.user.exception.UserException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserAdapter implements UserPort {

    private final UserRepository userRepository;


    @Override
    public void isValidUserKey(Long userKey) {
        userRepository.findById(userKey).orElseThrow(
                () -> new UserException(UserErrorCode.NOT_FOUND)
        );

        return;
    }
}
