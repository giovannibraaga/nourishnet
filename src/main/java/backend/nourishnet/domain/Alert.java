package backend.nourishnet.domain;

import jakarta.persistence.*;
import lombok.Getter; import lombok.Setter;
import java.time.OffsetDateTime;

@Entity @Table(name="ALERT")
@Getter @Setter
public class Alert {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="ALERT_ID") private Long alertId;

    @Column(name="TYPE") private String type;
    @Column(name="SEVERITY") private String severity;
    @Column(name="DONATION_ID") private Long donationId;
    @Lob @Column(name="MESSAGE") private String message;
    @Column(name="STATUS") private String status;
    @Column(name="CREATED_AT") private OffsetDateTime createdAt;
    @Column(name="CLOSED_AT") private OffsetDateTime closedAt;
}
