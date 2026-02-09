package com.toy.modulithdemo.user.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserAddRequest {


    @NotNull
    @Size(min = 6, max = 20)
    private String userId;

    @NotNull
    @Size(min = 8, max = 20)
    private String pw;
}
