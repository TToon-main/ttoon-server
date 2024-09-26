package com.server.ttoon.domain.feed.entity;

import com.server.ttoon.common.BaseEntity;
import com.server.ttoon.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Feed extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String content;

    private int number;

    private int likes;
    private LocalDate date;

    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

    @OneToMany(mappedBy = "feed", cascade = CascadeType.ALL)
    private List<FeedImage> feedImageList = new ArrayList<>();

    public void updateLike(int likes) {
        this.likes = likes;
    }

    public void setFeedImageList(List<FeedImage> feedImageList){
        this.feedImageList = feedImageList;
    }

    public String getThumbnail(){
        String thunmbnail = null;

        for (FeedImage feedImage : feedImageList) {
            if(feedImage.getIsFirst()){
                thunmbnail = feedImage.getImageUrl();
            }
        }

        return thunmbnail;
    }
}
