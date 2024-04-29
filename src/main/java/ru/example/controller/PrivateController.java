package ru.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/private")
@RequiredArgsConstructor
public class PrivateController {

    @GetMapping
    @Operation(summary = "Доступен только авторизованным пользователям")
    public String examplePrivate() {
        return "Hello, authorized user!";
    }
}
