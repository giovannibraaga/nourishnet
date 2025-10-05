package backend.nourishnet.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.OffsetDateTime;
import java.util.List;

public record CreateDonationRequest(
        @NotBlank @Size(max=255) String addressText,
        Double geoLat, Double geoLng,
        OffsetDateTime preferredPickupStart, OffsetDateTime preferredPickupEnd,
        OffsetDateTime expiresAt,
        String notes,
        @NotNull @Size(min=1) List<ItemRequest> items
) {}