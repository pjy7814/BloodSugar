package kr.co.sugarmanager.userservice.entity;

import jakarta.persistence.*;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.*;

import java.util.*;

@Entity
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@DynamicInsert
@DynamicUpdate
@Table(name = "USER")
@SQLDelete(sql = "UPDATE SET DELETED_AT ON USER WHERE USER_PK = ?")
@ToString
public class UserEntity extends CUDBaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "USER_PK", updatable = false)
    private long pk;

    @Column(name = "USER_ID", unique = true, length = 320)
    private String id;

    @Column(name = "USER_PW", length = 200)
    private String pw;

    @Column(name = "USER_EMAIL", unique = true, length = 320)
    private String email;

    @Column(name = "USER_NAME", nullable = false, length = 20)
    private String name;

    @Column(name = "USER_NICKNAME", nullable = false, length = 20)
    private String nickname;

    @Column(name = "USER_HEIGHT")
    private Integer height;

    @Column(name = "USER_WEIGHT")
    private Integer weight;

    @Column(name = "USER_BIRTHDAY")
    private Date birthday;

    @Column(name = "USER_GENDER")
    private Boolean gender;

    @Column(name = "USER_SOCIAL_TYPE", length = 10)
    @Enumerated(EnumType.STRING)
    private SocialType socialType;

    @Column(name = "USER_SOCIAL_ID")
    private String socialId;

    @Column(name = "BLOOD_SUGAR_MAX")
    private int sugarMax;

    @Column(name = "BLOOD_SUGAR_MIN")
    private int sugarMin;

    //Relation
    @OneToOne(mappedBy = "user", fetch = FetchType.LAZY, optional = false, cascade = {CascadeType.PERSIST, CascadeType.REMOVE, CascadeType.MERGE})
    private UserSettingEntity setting;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "GROUP_PK")
    private GroupEntity group;

    @OneToOne(mappedBy = "user", fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private UserImageEntity userImage;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @Builder.Default
    private Set<UserRoleEntity> roles = new HashSet<>();

    public void addRoles(Set<UserRoleEntity> roles) {
        this.roles = roles;
        roles.stream().forEach(role -> role.setUser(this));
    }

    public void addSetting(UserSettingEntity setting) {
        this.setting = setting;
        setting.setUser(this);
    }

    public void addProfileImage(UserImageEntity userImage) {
        this.userImage = userImage;
        userImage.setUser(this);
    }
}
