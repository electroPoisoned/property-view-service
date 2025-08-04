package by.daniliuk.property_view_service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContactDto {
    @NotNull
    @NotBlank
    @Pattern(regexp = "^\\+[0-9]{1,15}$")
    private String phone;

    @NotNull
    @NotBlank
    @Email
    private String email;
}