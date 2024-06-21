package com.server.ttoon.domain.feed.repository;

import com.server.ttoon.domain.feed.entity.Characters;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CharactersRepository extends JpaRepository<Characters, Long> {
}
