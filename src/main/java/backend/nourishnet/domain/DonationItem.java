package backend.nourishnet.domain;

import jakarta.persistence.*;
import lombok.Getter; import lombok.Setter;
import java.time.LocalDate;

@Entity @Table(name="DONATION_ITEM")
@Getter @Setter
public class DonationItem {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="DONATION_ITEM_ID") private Long donationItemId;

    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="DONATION_ID", nullable=false)
    private Donation donation;

    @Column(name="NAME", nullable=false) private String name;
    @Column(name="UNIT", nullable=false) private String unit; // KG/G/L/ML/UN
    @Column(name="QTY", nullable=false) private Double qty;
    @Column(name="CATEGORY", nullable=false) private String category;
    @Column(name="BEST_BEFORE_DATE") private LocalDate bestBeforeDate;

    @Lob @Column(name="NOTES") private String notes;
}
