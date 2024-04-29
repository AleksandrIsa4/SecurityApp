package ru.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/public")
@RequiredArgsConstructor
public class PublicController {

    @GetMapping
    @Operation(summary = "Доступен всем пользователям")
    public String examplePublic() {
        return "Hello!";
    }
}
