package kr.co.sugarmanager.userservice.entity;


import jakarta.persistence.*;
import kr.co.sugarmanager.userservice.util.StringUtils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.SQLDelete;

@Entity
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@DynamicInsert
@DynamicUpdate
@Table(name = "USER_GROUP")
@SQLDelete(sql = "UPDATE SET DELETED_AT = NOW() ON USER_GROUP WHERE GROUP_PK = ?")
public class GroupEntity extends CUDBaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "GROUP_PK")
    private long pk;

    @Column(name = "GROUP_CODE", nullable = false, length = 10)
    @Builder.Default
    private String groupCode = generateGroupCode();

    //custom methods
    private static String generateGroupCode() {
        return StringUtils.generateRandomString(10);
    }
}
