package com.server.ttoon.security.jwt.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "refresh_token")
@Entity
public class RefreshToken {

    @Id
    private String memberId;
    private String value;

    public RefreshToken updateValue(String token) {
        this.value = token;
        return this;
    }
}