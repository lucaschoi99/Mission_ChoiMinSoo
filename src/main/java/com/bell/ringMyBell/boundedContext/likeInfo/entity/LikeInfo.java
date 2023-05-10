package com.bell.ringMyBell.boundedContext.likeInfo.entity;

import com.bell.ringMyBell.base.response.ResponseData;
import com.bell.ringMyBell.boundedContext.instaMember.entity.InstaMember;
import com.bell.ringMyBell.util.Ut;
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

    private LocalDateTime modifyUnlockDate;

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

    public boolean isModifyUnlocked() {
        return modifyUnlockDate.isBefore(LocalDateTime.now());
    }

    // 초 단위에서 올림 해주세요.
    public String getModifyUnlockDateRemainStrHuman() {
        return Ut.time.diffFormat1Human(LocalDateTime.now(), modifyUnlockDate);
    }

    public ResponseData<LikeInfo> updateAttractionTypeCode(int attractiveTypeCode) {
        if (this.attractiveTypeCode == attractiveTypeCode) {
            return ResponseData.of("F-1", "이미 설정되었습니다.");
        }

        this.attractiveTypeCode = attractiveTypeCode;

        return ResponseData.of("S-1", "성공");
    }

    public String getAttractiveTypeDisplayNameWithIcon() {
        return switch (attractiveTypeCode) {
            case 1 -> "<i class=\"fa-solid fa-person-rays\"></i>";
            case 2 -> "<i class=\"fa-regular fa-face-smile\"></i>";
            default -> "<i class=\"fa-solid fa-people-roof\"></i>";
        } + "&nbsp;" + getAttractiveTypeDisplayName();
    }

}
