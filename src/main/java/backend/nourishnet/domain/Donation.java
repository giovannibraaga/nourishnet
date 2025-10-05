package backend.nourishnet.domain;

import jakarta.persistence.*;
import lombok.Getter; import lombok.Setter;
import java.time.OffsetDateTime;
import java.util.ArrayList; import java.util.List;

@Entity @Table(name="DONATION")
@Getter @Setter
public class Donation {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="DONATION_ID") private Long donationId;

    @Column(name="DONOR_USER_ID", nullable=false) private Long donorUserId;
    @Column(name="STATUS", nullable=false) private String status; // OPEN/...

    @Column(name="ADDRESS_TEXT", nullable=false) private String addressText;
    @Column(name="GEO_LAT") private Double geoLat;
    @Column(name="GEO_LNG") private Double geoLng;

    @Column(name="PREFERRED_PICKUP_START") private OffsetDateTime preferredPickupStart;
    @Column(name="PREFERRED_PICKUP_END") private OffsetDateTime preferredPickupEnd;

    @Lob @Column(name="NOTES") private String notes;
    @Column(name="EXPIRES_AT") private OffsetDateTime expiresAt;

    @Column(name="CREATED_AT", insertable=false, updatable=false) private OffsetDateTime createdAt;
    @Column(name="UPDATED_AT", insertable=false, updatable=false) private OffsetDateTime updatedAt;

    @OneToMany(mappedBy="donation", cascade=CascadeType.ALL, orphanRemoval=true)
    private List<DonationItem> items = new ArrayList<>();
}
