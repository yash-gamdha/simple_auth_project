package com.yash.simple_auth_project.user;

import com.yash.simple_auth_project.common.ExceptionResponse;
import com.yash.simple_auth_project.user.dto.DeleteRequestDto;
import com.yash.simple_auth_project.user.dto.LogInDto;
import com.yash.simple_auth_project.user.dto.LogInResponseDto;
import com.yash.simple_auth_project.user.dto.SignUpDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/api/")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("signup")
    public ResponseEntity<String> signUp(@RequestBody SignUpDto signUpReq) throws UserService.UserNameTakenException {
        var isSuccess = userService.signUp(signUpReq);

        return ResponseEntity.ok(isSuccess);
    }

    @PostMapping("login")
    public ResponseEntity<LogInResponseDto> logIn(@RequestBody LogInDto logInReq) throws UserService.UserNotFoundException, UserService.InvalidPasswordException {
        var user = userService.logIn(logInReq);

        return ResponseEntity.ok(user);
    }

    @DeleteMapping("remove")
    public ResponseEntity<String> deleteAccount(@RequestBody DeleteRequestDto deleteReq) throws UserService.InvalidPasswordException {
        var isSuccess = userService.deleteAccount(deleteReq);

        return ResponseEntity.ok(isSuccess);
    }

    @ExceptionHandler
    public ResponseEntity<ExceptionResponse> exceptionHandler(Exception e) {
        String message;
        HttpStatusCode statusCode;

        switch (e) {
            case UserService.UserNotFoundException userNotFoundException -> {
                message = userNotFoundException.getMessage();
                statusCode = HttpStatus.NOT_FOUND;
            }
            case UserService.InvalidPasswordException invalidPasswordException -> {
                message = invalidPasswordException.getMessage();
                statusCode = HttpStatus.UNAUTHORIZED;
            }
            case UserService.UserNameTakenException userNameTakenException -> {
                message = userNameTakenException.getMessage();
                statusCode = HttpStatus.CONFLICT;
            }
            case null, default -> {
                assert e != null;
                message = String.format("Something went wrong.\n%s", e.getMessage());
                statusCode = HttpStatus.INTERNAL_SERVER_ERROR;
            }
        }

        return ResponseEntity
                .status(statusCode)
                .body(
                        ExceptionResponse.builder()
                                .message(message)
                                .build()
                );
    }
}