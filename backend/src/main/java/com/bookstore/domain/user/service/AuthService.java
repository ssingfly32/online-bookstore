package com.bookstore.domain.user.service;

import com.bookstore.domain.user.dto.LoginRequest;
import com.bookstore.domain.user.dto.SignupRequest;
import com.bookstore.domain.user.dto.TokenResponse;
import com.bookstore.domain.user.entity.RefreshToken;
import com.bookstore.domain.user.entity.User;
import com.bookstore.domain.user.entity.UserAccount;
import com.bookstore.domain.user.repository.RefreshTokenRepository;
import com.bookstore.domain.user.repository.UserAccountRepository;
import com.bookstore.domain.user.repository.UserRepository;
import com.bookstore.global.enums.Provider;
import com.bookstore.global.enums.Role;
import com.bookstore.global.exception.ErrorCode;
import com.bookstore.global.exception.auth.AuthException;
import com.bookstore.global.exception.auth.UserException;
import com.bookstore.global.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private final UserRepository userRepository;
    private final UserAccountRepository userAccountRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtProvider jwtProvider;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public Long signup(SignupRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserException(ErrorCode.DUPLICATE_EMAIL);
        }

        User user = User.builder()
                .email(request.getEmail())
                .nickname(request.getNickname())
                .role(Role.ROLE_USER)
                .build();
        userRepository.save(user);

        UserAccount account = UserAccount.builder()
                .user(user)
                .provider(Provider.LOCAL)
                .providerId(null)
                .password(passwordEncoder.encode(request.getPassword()))
                .build();
        userAccountRepository.save(account);

        return user.getId();
    }

    @Transactional
    public TokenResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UserException(ErrorCode.USER_NOT_FOUND));

        UserAccount account = userAccountRepository.findByUser(user)
                .orElseThrow(() -> new UserException(ErrorCode.ACCOUNT_NOT_FOUND));

        if (!passwordEncoder.matches(request.getPassword(), account.getPassword())) {
            throw new AuthException(ErrorCode.INVALID_PASSWORD);
        }

        return issueTokens(user);
    }

    @Transactional
    public TokenResponse reissue(String refreshToken) {
        jwtProvider.validateToken(refreshToken);

        Long userId = jwtProvider.getUserId(refreshToken);

        RefreshToken saved = refreshTokenRepository.findByUserId(userId)
                .orElseThrow(() -> new AuthException(ErrorCode.TOKEN_NOT_FOUND));

        if (!saved.getToken().equals(refreshToken)) {
            throw new AuthException(ErrorCode.TOKEN_MISMATCH);
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(ErrorCode.USER_NOT_FOUND));

        return issueTokens(user);
    }

    @Transactional
    public void logout(Long userId) {
        refreshTokenRepository.deleteByUserId(userId);
    }

    private TokenResponse issueTokens(User user) {
        String accessToken = jwtProvider.generateAccessToken(user.getId(), user.getRole());
        String newRefreshToken = jwtProvider.generateRefreshToken(user.getId());
        LocalDateTime expiredAt = jwtProvider.getRefreshTokenExpiredAt();

        refreshTokenRepository.findByUserId(user.getId())
                .ifPresentOrElse(
                        token -> token.update(newRefreshToken, expiredAt),
                        () -> refreshTokenRepository.save(
                                RefreshToken.builder()
                                        .userId(user.getId())
                                        .token(newRefreshToken)
                                        .expiredAt(expiredAt)
                                        .build()
                        )
                );

        return new TokenResponse(accessToken, newRefreshToken);
    }
}