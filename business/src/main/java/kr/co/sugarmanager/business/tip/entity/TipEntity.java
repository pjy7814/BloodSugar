package kr.co.sugarmanager.business.tip.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "FAQ")
@EntityListeners(value = AuditingEntityListener.class)
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TipEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "FAQ_PK")
    private long pk;

    @Column(name = "FAQ_TITLE")
    private String title;

    @Column(name = "FAQ_CONTENT")
    private String content;

    @Column(name = "FAQ_CREATED_AT")
    private String createdAt;

    @Column(name = "FAQ_UPDATED_AT")
    private String updatedAt;

    @Column(name = "DELETED_AT")
    private LocalDateTime deletedAt;
}
