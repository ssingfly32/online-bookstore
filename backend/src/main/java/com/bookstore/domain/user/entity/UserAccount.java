package com.bookstore.domain.user.entity;

import com.bookstore.global.enums.Provider;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "user_account")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "account_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private Provider provider;   // "local", "kakao", "google"

    @Column(length = 255)
    private String providerId; // 소셜 고유 ID

    @Column(length = 255)
    private String password;   // 로컬 로그인용 (BCrypt), 소셜이면 null

    @Builder
    public UserAccount(User user, Provider provider, String providerId, String password) {
        this.user = user;
        this.provider = provider;
        this.providerId = providerId;
        this.password = password;
    }
}
