package ru.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.example.service.UserService;

@RestController
@RequestMapping(path = "/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    @Operation(summary = "Доступен только авторизованным пользователям с ролью USER, ADMIN")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public String exampleUser(HttpServletRequest request) {
        return "Hello, user!";
    }

    @GetMapping("/get-admin")
    @Operation(summary = "Получить роль ADMIN")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public void getAdmin() {
        userService.getAdmin();
    }
}
