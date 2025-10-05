package backend.nourishnet.domain;

import jakarta.persistence.*;
import lombok.Getter; import lombok.Setter;
import java.time.OffsetDateTime;

@Entity @Table(name="DELIVERY_PROOF")
@Getter @Setter
public class DeliveryProof {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="PROOF_ID") private Long proofId;

    @Column(name="DONATION_ID", nullable=false) private Long donationId;
    @Column(name="RECEIVER_USER_ID") private Long receiverUserId;
    @Column(name="PROOF_TYPE", nullable=false) private String proofType;
    @Column(name="STORAGE_REF", nullable=false) private String storageRef;

    @Column(name="CREATED_AT", insertable=false, updatable=false) private OffsetDateTime createdAt;
}
