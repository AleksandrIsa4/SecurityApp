package ru.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.example.service.UserService;

import java.security.Principal;

@RestController
@RequestMapping(path = "/user")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    @GetMapping
    @Operation(summary = "Доступен только авторизованным пользователям с ролью MODERATOR, SUPER_ADMIN")
    @PreAuthorize("hasAnyRole('MODERATOR','SUPER_ADMIN')")
    public String exampleUser(HttpServletRequest request) {
        return "Hello, user!";
    }

    @GetMapping("/get-admin")
    @Operation(summary = "Получить роль SUPER_ADMIN")
    @PreAuthorize("hasAnyRole('MODERATOR','SUPER_ADMIN')")
    public void getAdmin(Principal principal) {
        userService.getAdmin();
        log.info("Получить роль SUPER_ADMIN пользователем {}", principal.getName());
    }
}
