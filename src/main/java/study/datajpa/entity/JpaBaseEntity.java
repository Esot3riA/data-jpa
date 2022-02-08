package study.datajpa.entity;

import lombok.Getter;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import java.time.LocalDateTime;

@MappedSuperclass   // 공통 속성 지정
@Getter
public class JpaBaseEntity {

    @Column(updatable = false)  // 생성일 갱신 방지
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;

    @PrePersist     // em.persist 전에 자동 실행
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        createdDate = now;
        updatedDate = now;
    }

    @PreUpdate      // dirty check 전에 자동 실행
    public void preUpdate() {
        updatedDate = LocalDateTime.now();
    }
}
