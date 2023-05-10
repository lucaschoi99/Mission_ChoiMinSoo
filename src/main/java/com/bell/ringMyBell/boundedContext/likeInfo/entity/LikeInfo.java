package com.bell.ringMyBell.boundedContext.likeInfo.entity;

import com.bell.ringMyBell.boundedContext.instaMember.entity.InstaMember;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

import static jakarta.persistence.GenerationType.IDENTITY;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@ToString
@Entity
@Getter
public class LikeInfo {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @CreatedDate
    private LocalDateTime createDate;

    @LastModifiedDate
    private LocalDateTime modifyDate;

    @ManyToOne
    private InstaMember fromInstaMember;

    private String fromInstaMemberUsername;

    @ManyToOne
    private InstaMember toInstaMember;

    private String toInstaMemberUsername;

    private int attractiveTypeCode; // 매력포인트 (1=외모, 2=성격, 3=능력)

    public String getAttractiveTypeDisplayName() {
        return switch (attractiveTypeCode) {
            case 1 -> "외모";
            case 2 -> "성격";
            default -> "능력";
        };
    }

    public void changeTypeCode(int code) {
        this.attractiveTypeCode = code;
    }
}
