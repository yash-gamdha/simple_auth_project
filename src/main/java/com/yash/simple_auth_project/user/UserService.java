package com.yash.simple_auth_project.user;

import com.yash.simple_auth_project.user.dto.DeleteRequestDto;
import com.yash.simple_auth_project.user.dto.LogInDto;
import com.yash.simple_auth_project.user.dto.LogInResponseDto;
import com.yash.simple_auth_project.user.dto.SignUpDto;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepo userRepo;

    public UserService(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    public String signUp(SignUpDto signUpReq) throws UserNameTakenException {
        var isUsernameAlreadyUsed = userRepo.findByUsername(signUpReq.getUsername());

        if (isUsernameAlreadyUsed.isPresent()) {
            throw new UserNameTakenException(signUpReq.getUsername());
        }

        var user = UserEntity.builder()
                .username(signUpReq.getUsername())
                .email(signUpReq.getEmail())
                .password(signUpReq.getPassword())
                .build();

        userRepo.save(user);

        return "success";
    }

    public LogInResponseDto logIn(LogInDto logInReq) throws UserNotFoundException, InvalidPasswordException {
        var user = userRepo.findByUsername(logInReq.getUsername()).orElseThrow(() -> new UserNotFoundException(logInReq.getUsername()));

        if (user.getPassword().equals(logInReq.getPassword())) {
            return new LogInResponseDto(user.getUsername());
        } else {
            throw new InvalidPasswordException();
        }
    }

    public String deleteAccount(DeleteRequestDto deleteReq) throws InvalidPasswordException {
        var user = userRepo.findByUsername(deleteReq.getUsername());

        if (user.isPresent()) {
            if (user.get().getPassword().equals(deleteReq.getPassword())) {
                userRepo.delete(user.get());
                return "Success";
            } else {
                throw new InvalidPasswordException();
            }
        }

        return "failure";
    }

    public static class UserNotFoundException extends Exception {
        public UserNotFoundException(String username) {
            super("No user found with username : " + username);
        }
    }

    public static class InvalidPasswordException extends Exception {
        public InvalidPasswordException() {
            super("Invalid password");
        }
    }

    public static class UserNameTakenException extends Exception {
        public UserNameTakenException(String username) {
            super("Username : " + username + " already in use.");
        }
    }
}