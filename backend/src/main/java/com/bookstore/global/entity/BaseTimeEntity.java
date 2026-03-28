package com.bookstore.global.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@MappedSuperclass          // 이 클래스 자체는 테이블 안 만들고, 자식 엔티티 테이블에 컬럼으로 포함
@EntityListeners(AuditingEntityListener.class)  // ← JPA가 저장/수정 시점을 감지해서 자동으로 값 넣어줌
public abstract class BaseTimeEntity {

    @CreatedDate           // ← INSERT 시점에 자동으로 현재 시간 저장
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate      // ← UPDATE 시점에 자동으로 현재 시간 갱신
    private LocalDateTime updatedAt;
}
