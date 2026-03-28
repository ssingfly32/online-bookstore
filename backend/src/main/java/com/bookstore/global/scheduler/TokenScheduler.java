package com.bookstore.global.scheduler;

import com.bookstore.domain.user.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class TokenScheduler {

    private final RefreshTokenRepository refreshTokenRepository;

    @Scheduled(cron = "0 0 3 * * *")   // 매일 새벽 3시
    @Transactional
    public void deleteExpiredTokens() {
        refreshTokenRepository.deleteAllByExpiredAtBefore(LocalDateTime.now());
        log.info("만료된 Refresh Token 정리 완료 - {}", LocalDateTime.now());
    }
}

