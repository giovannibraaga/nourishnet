package backend.nourishnet.domain;

import jakarta.persistence.*;
import lombok.Getter; import lombok.Setter;
import java.time.OffsetDateTime;

@Entity @Table(name="DONATION_EVENT_LOG")
@Getter @Setter
public class DonationEventLog {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="EVENT_ID") private Long eventId;

    @Column(name="DONATION_ID", nullable=false) private Long donationId;
    @Column(name="EVENT_TYPE", nullable=false) private String eventType;
    @Column(name="ACTOR_USER_ID") private Long actorUserId;
    @Lob @Column(name="PAYLOAD_JSON") private String payloadJson;
    @Column(name="EVENT_TIME", nullable=false) private OffsetDateTime eventTime;
}
