package com.bookstore.domain.user.controller;

import com.bookstore.domain.user.dto.LoginRequest;
import com.bookstore.domain.user.dto.ReissueRequest;
import com.bookstore.domain.user.dto.SignupRequest;
import com.bookstore.domain.user.dto.TokenResponse;
import com.bookstore.domain.user.service.AuthService;
import com.bookstore.global.response.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.net.URI;

@RestController
@RequestMapping("/api/users")   // ← 명사 + 복수형
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * 회원가입
     * POST /api/users
     * 성공 → 201 Created + Location 헤더
     */
    @PostMapping
    public ResponseEntity<ApiResponse<Void>> signup(
            @Valid @RequestBody SignupRequest request,
            HttpServletRequest httpRequest) {

        Long userId = authService.signup(request);

        // Location 헤더: 생성된 리소스 URI
        URI location = URI.create(httpRequest.getRequestURI() + "/" + userId);

        return ResponseEntity
                .created(location)   // 201 Created
                .body(ApiResponse.success("회원가입이 완료되었습니다."));
    }

    /**
     * 로그인
     * POST /api/users/login
     * 성공 → 200 OK + TokenResponse
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<TokenResponse>> login(
            @Valid @RequestBody LoginRequest request) {

        TokenResponse tokens = authService.login(request);

        return ResponseEntity
                .ok(ApiResponse.success(tokens));   // 200 OK
    }

    /**
     * Access Token 재발급
     * POST /api/users/reissue
     * 성공 → 200 OK + TokenResponse
     */
    @PostMapping("/reissue")
    public ResponseEntity<ApiResponse<TokenResponse>> reissue(
            @RequestBody ReissueRequest request) {

        TokenResponse tokens = authService.reissue(request.getRefreshToken());

        return ResponseEntity
                .ok(ApiResponse.success(tokens));   // 200 OK
    }

    /**
     * 로그아웃
     * POST /api/users/logout
     * 성공 → 204 No Content
     */
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @AuthenticationPrincipal Long userId) {

        authService.logout(userId);

        return ResponseEntity
                .noContent()         // 204 No Content
                .build();
    }
}

