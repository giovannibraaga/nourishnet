package backend.nourishnet.domain;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;

@Entity
@Table(name = "USER_ACCOUNT")
@Getter @Setter
public class UserAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "USER_ID")
    private Long userId;

    @Column(name = "FIREBASE_UID", unique = true)
    private String firebaseUid;

    @Column(name = "EMAIL", nullable = false)
    private String email;

    @Column(name = "NAME", nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "ROLE", nullable = false)
    private Role role;

    @Column(name = "STATUS", nullable = false)
    private String status;

    @Lob
    @Column(name = "BIO")
    private String bio;

    @Column(name = "CREATED_AT", insertable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "UPDATED_AT", insertable = false, updatable = false)
    private OffsetDateTime updatedAt;
}
