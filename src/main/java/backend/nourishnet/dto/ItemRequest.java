package backend.nourishnet.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record ItemRequest(
        @NotBlank @Size(max=160) String name,
        @NotBlank @Size(max=8) String unit,
        @NotNull @Positive Double qty,
        @NotBlank @Size(max=50) String category,
        LocalDate bestBeforeDate,
        String notes
) {}
