package ru.example.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.example.dto.CreateUserRequest;
import ru.example.dto.SignInRequest;
import ru.example.dto.TokenResponse;
import ru.example.entity.Block;
import ru.example.entity.UserEntity;
import ru.example.exceptions.DataNotFoundException;
import ru.example.model.RoleType;
import ru.example.repository.BlockRepository;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserService userService;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    private final BlockRepository blockRepository;

    @Value("${block.time}")
    private Integer BLOCK_TIME;

    @Value("${block.attempt}")
    private Integer BLOCK_ATTEMPT;

    /**
     * Регистрация пользователя
     *
     * @param request данные пользователя
     * @return токен
     */
    public TokenResponse signUp(CreateUserRequest request) {
        UserEntity userEntity = UserEntity.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .roles(new HashSet<>(Arrays.asList(RoleType.ROLE_MODERATOR)))
                .build();
        userService.create(userEntity);
        String jwt = jwtService.generateToken(userEntity);
        return new TokenResponse(jwt);
    }

    /**
     * Аутентификация пользователя
     *
     * @param request данные пользователя
     * @return токен
     */
    public TokenResponse signIn(SignInRequest request) {
        Block block = checkBlock(request.getUsername());
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    request.getUsername(),
                    request.getPassword()
            ));
        } catch (BadCredentialsException e) {
            blockUser(block);
            throw new DataNotFoundException("Неправильно введен логин или пароль");
        }

        var user = userService
                .userDetailsService()
                .loadUserByUsername(request.getUsername());
        var jwt = jwtService.generateToken(user);
        return new TokenResponse(jwt);
    }

    private void blockUser(Block block) {
        long attempt = block.getAttempt();
        if (attempt > BLOCK_ATTEMPT) {
            block.setAttempt(0);
        } else {
            block.setAttempt(attempt + 1);
            block.setDateTime(LocalDateTime.now());
        }
        blockRepository.save(block);
    }

    private Block checkBlock(String userName) {
        Block block = blockRepository.findByUserName(userName).orElse(new Block());
        block.setUserName(userName);
        if (block.getDateTime().plusSeconds(BLOCK_TIME).isAfter(LocalDateTime.now()) && block.getAttempt() > BLOCK_ATTEMPT) {
            throw new LockedException("Много неправильных попыток входа");
        }
        return block;
    }
}
