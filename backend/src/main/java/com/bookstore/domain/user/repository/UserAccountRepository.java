package com.bookstore.domain.user.repository;

import com.bookstore.domain.user.entity.User;
import com.bookstore.domain.user.entity.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserAccountRepository extends JpaRepository<UserAccount, Long> {
    Optional<UserAccount> findByUser(User user);
    Optional<UserAccount> findByProviderAndProviderId(String provider, String providerId);
}

