package com.server.ttoon.domain.feed.repository;

import com.server.ttoon.domain.feed.entity.Feed;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FeedRepository extends JpaRepository<Feed, Long> {
}
