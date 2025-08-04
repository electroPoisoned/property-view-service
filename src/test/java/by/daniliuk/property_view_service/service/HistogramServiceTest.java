package by.daniliuk.property_view_service.service;

import by.daniliuk.property_view_service.dto.AddressDto;
import by.daniliuk.property_view_service.dto.ContactDto;
import by.daniliuk.property_view_service.dto.HotelCreateDto;
import by.daniliuk.property_view_service.dto.TimeDto;
import by.daniliuk.property_view_service.repository.HotelRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class HistogramServiceTest {

    @Autowired
    private HistogramService histogramService;

    @Autowired
    private HotelService hotelService;

    @Autowired
    private HotelRepository hotelRepository;

    @BeforeEach
    void setUp() {
        createHotel("Hotel A", "Brand X", "Minsk");
        createHotel("Hotel B", "Brand X", "Grodno");
        createHotel("Hotel C", "Brand Y", "Minsk");
    }

    private void createHotel(String name, String brand, String city) {
        hotelService.createHotel(HotelCreateDto.builder()
                .name(name)
                .brand(brand)
                .address(AddressDto.builder()
                        .houseNumber(1)
                        .street("Street")
                        .city(city)
                        .country("Belarus")
                        .postCode("220000")
                        .build())
                .contacts(ContactDto.builder()
                        .phone("+375" + (System.currentTimeMillis() % 100000000L)) // Unique phone
                        .email(name.replace(" ", "") + "@example.com")
                        .build())
                .arrivalTime(TimeDto.builder().checkIn("14:00").build())
                .build());
    }

    @Test
    void getHistogram_ByBrand_ReturnsCounts() {
        // When
        Map<String, Long> result = histogramService.getHistogram("brand");

        // Then
        assertEquals(2, result.get("Brand X"));
        assertEquals(1, result.get("Brand Y"));
    }

    @Test
    void getHistogram_ByCity_ReturnsCounts() {
        // When
        Map<String, Long> result = histogramService.getHistogram("city");

        // Then
        assertEquals(2, result.get("Minsk"));
        assertEquals(1, result.get("Grodno"));
    }

    @Test
    void getHistogram_ByAmenities_ReturnsCounts() {
        // Given
        Long hotelId = hotelRepository.findAll().get(0).getId();
        hotelService.addAmenities(hotelId, List.of("Pool", "Spa"));

        // When
        Map<String, Long> result = histogramService.getHistogram("amenities");

        // Then
        assertEquals(1, result.get("Pool"));
        assertEquals(1, result.get("Spa"));
    }

    @Test
    void getHistogram_InvalidParam_ThrowsException() {
        assertThrows(IllegalArgumentException.class,
                () -> histogramService.getHistogram("invalid_param"));
    }
}