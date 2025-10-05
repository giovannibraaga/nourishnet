package backend.nourishnet.domain;

import jakarta.persistence.*;
import lombok.Getter; import lombok.Setter;
import java.time.OffsetDateTime;

@Entity @Table(name="DONATION_MATCH")
@Getter @Setter
public class DonationMatch {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="MATCH_ID") private Long matchId;

    @Column(name="DONATION_ID", nullable=false) private Long donationId;
    @Column(name="NGO_USER_ID", nullable=false) private Long ngoUserId;

    @Column(name="STATUS", nullable=false) private String status; // REQUESTED/ACCEPTED/REJECTED/CANCELED

    @Column(name="SCHEDULED_PICKUP_AT") private OffsetDateTime scheduledPickupAt;

    @Column(name="CREATED_AT", insertable=false, updatable=false) private OffsetDateTime createdAt;
    @Column(name="UPDATED_AT", insertable=false, updatable=false) private OffsetDateTime updatedAt;
}
