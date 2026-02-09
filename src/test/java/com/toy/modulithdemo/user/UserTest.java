package com.toy.modulithdemo.user;


import com.toy.modulithdemo.user.constant.UserErrorCode;
import com.toy.modulithdemo.user.dto.UserAddRequest;
import com.toy.modulithdemo.user.exception.UserException;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class UserTest {


    UserAddRequest userAddRequest;

    @BeforeEach
    void init() {
        userAddRequest = new UserAddRequest();
        userAddRequest.setUserId("TestId");
        userAddRequest.setPw("qwer1234");
    }

    @Test
    void user_validate_success() {

        User user = User.create(userAddRequest.getUserId(), userAddRequest.getPw());
        SoftAssertions.assertSoftly(softAssertions -> {
            softAssertions.assertThat(user.getUserId()).isEqualTo(userAddRequest.getUserId());
            softAssertions.assertThat(user.getPw()).isEqualTo(userAddRequest.getPw());
        });
    }

    @Test
    void user_validate_fail_id_is_empty() {
        userAddRequest.setUserId("   ");

        Assertions.assertThatThrownBy(() -> User.create(userAddRequest.getUserId(), userAddRequest.getPw()))
                .isInstanceOf(UserException.class)
                .hasMessage(UserErrorCode.INVALID_AUTH_INPUT.getMessage());

    }


    @Test
    void user_validate_fail_pw_is_empty() {
        userAddRequest.setPw("   ");

        Assertions.assertThatThrownBy(() -> User.create(userAddRequest.getUserId(), userAddRequest.getPw()))
                .isInstanceOf(UserException.class)
                .hasMessage(UserErrorCode.INVALID_AUTH_INPUT.getMessage());

    }

}