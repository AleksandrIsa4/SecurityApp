package ru.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.example.dto.CreateUserRequest;
import ru.example.dto.SignInRequest;
import ru.example.dto.TokenResponse;
import ru.example.service.AuthenticationService;

@RestController
@RequestMapping(path = "/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @Operation(summary = "Регистрация пользователя")
    @PostMapping("/sign-up")
    public TokenResponse signUp(@RequestBody @Valid CreateUserRequest request) {
        TokenResponse tokenResponse = authenticationService.signUp(request);
        log.info("Регистрация пользователя {}", request.getUsername());
        return authenticationService.signUp(request);
    }

    @Operation(summary = "Авторизация пользователя")
    @PostMapping("/sign-in")
    public TokenResponse signIn(@RequestBody @Valid SignInRequest request) {
        TokenResponse tokenResponse = authenticationService.signIn(request);
        log.info("Получение токена пользователем {}", request.getUsername());
        return tokenResponse;
    }
}
