package com.yash.simple_auth_project.user.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import org.springframework.lang.NonNull;

@Data
@Setter(AccessLevel.NONE)
public class SignUpDto {
    @NonNull
    private String username;

    @NonNull
    private String email;

    @NonNull
    private String password;
}