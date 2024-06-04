package ru.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/admin")
@RequiredArgsConstructor
@Slf4j
public class AdminController {

    @GetMapping
    @Operation(summary = "Доступен только авторизованным пользователям с ролью SUPER_ADMIN")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public String exampleAdmin() {
        return "Hello, admin!";
    }
}
