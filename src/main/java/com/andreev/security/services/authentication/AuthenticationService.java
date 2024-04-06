package com.andreev.security.services.authentication;

import com.andreev.security.domain.authentication.Role;
import com.andreev.security.domain.authentication.UserEntity;
import com.andreev.security.dto.authentication.AuthenticationResponse;
import com.andreev.security.dto.authentication.RegisterRequest;
import com.andreev.security.dto.authentication.SigninRequest;
import com.andreev.security.repositories.authentication.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final RefreshTokenService refreshTokenService;

    public AuthenticationResponse register(RegisterRequest req) {
        UserEntity user = UserEntity.builder()
                .firstname(req.getFirstname())
                .lastname(req.getLastname())
                .email(req.getEmail())
                .password(passwordEncoder.encode(req.getPassword()))
                .role(Role.USER)
                .isBanned(false)
                .build();
        userRepo.save(user);
        String jwt = jwtService.generateToken(user);
        String refreshToken = refreshTokenService.generateRefreshToken(user.getUuid());

        return AuthenticationResponse.builder()
                .token(jwt)
                .refreshToken(refreshToken)
                .build();
    }

    public AuthenticationResponse signin(SigninRequest req) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        req.getEmail(),
                        req.getPassword()
                )
        );

        UserEntity user = userRepo.findByEmail(req.getEmail()).orElseThrow();

        refreshTokenService.deleteAllRefreshTokensForUser(user.getUuid());

        String jwt = jwtService.generateToken(user);

        String refreshToken = refreshTokenService.generateRefreshToken(user.getUuid());
        return AuthenticationResponse.builder()
                .token(jwt)
                .refreshToken(refreshToken)
                .build();
    }
}
