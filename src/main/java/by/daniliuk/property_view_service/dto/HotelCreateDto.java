package by.daniliuk.property_view_service.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HotelCreateDto {
    @NotNull
    private String name;

    private String description;
    private String brand;

    @Valid
    @NotNull
    private AddressDto address;

    @Valid
    @NotNull
    private ContactDto contacts;

    @Valid
    private TimeDto arrivalTime;
}