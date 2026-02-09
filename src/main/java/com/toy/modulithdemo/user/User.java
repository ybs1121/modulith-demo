package com.toy.modulithdemo.user;


import com.toy.modulithdemo.user.constant.UserErrorCode;
import com.toy.modulithdemo.user.dto.UserAddRequest;
import com.toy.modulithdemo.user.exception.UserException;
import jakarta.persistence.*;
import lombok.*;

@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
@Getter
@Entity
@Table(name = "users") // order가 예약어일 수 있어 테이블명은 orders로
public class User {

    @Id
    @GeneratedValue
    private Long userKey;


    @Column(nullable = false, unique = true)
    private String userId;

    @Column(nullable = false)
    private String pw;


    public static User create(String userId, String passwordHash) {
        User user = new User(null, userId, passwordHash);
        user.validate();
        return user;
    }



    public void validate() {
        if (this.userId == null || this.userId.isBlank()) {
            throw new UserException(UserErrorCode.INVALID_AUTH_INPUT);
        }

        if (this.pw == null || this.pw.isBlank()) {
            throw new UserException(UserErrorCode.INVALID_AUTH_INPUT);
        }
    }


}
