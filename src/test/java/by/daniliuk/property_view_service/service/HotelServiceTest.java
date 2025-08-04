package by.daniliuk.property_view_service.service;

import by.daniliuk.property_view_service.dto.AddressDto;
import by.daniliuk.property_view_service.dto.ContactDto;
import by.daniliuk.property_view_service.dto.HotelCreateDto;
import by.daniliuk.property_view_service.dto.HotelSummaryDto;
import by.daniliuk.property_view_service.dto.TimeDto;
import by.daniliuk.property_view_service.exception.HotelNotFoundException;
import by.daniliuk.property_view_service.repository.HotelRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class HotelServiceTest {

    @Autowired
    private HotelService hotelService;

    @Autowired
    private HotelRepository hotelRepository;

    private HotelCreateDto createDto;

    @BeforeEach
    void setUp() {
        createDto = HotelCreateDto.builder()
                .name("Test Hotel")
                .description("Test Description")
                .brand("Test Brand")
                .address(AddressDto.builder()
                        .houseNumber(123)
                        .street("Test Street")
                        .city("Test City")
                        .country("Test Country")
                        .postCode("12345")
                        .build())
                .contacts(ContactDto.builder()
                        .phone("+375111111111")
                        .email("test@example.com")
                        .build())
                .arrivalTime(TimeDto.builder()
                        .checkIn("14:00")
                        .build())
                .build();
    }

    @Test
    void createHotel_Success() {
        // When
        HotelSummaryDto result = hotelService.createHotel(createDto);

        // Then
        assertNotNull(result.getId());
        assertEquals("Test Hotel", result.getName());
        assertEquals(1, hotelRepository.count());
    }

    @Test
    void createHotel_DuplicatePhone_ThrowsException() {
        // Given
        hotelService.createHotel(createDto);

        // When
        HotelCreateDto duplicateDto = HotelCreateDto.builder()
                .name("Another Hotel")
                .brand("Another Brand")
                .address(createDto.getAddress())
                .contacts(ContactDto.builder()
                        .phone("+375111111111") // Same phone
                        .email("another@example.com")
                        .build())
                .arrivalTime(createDto.getArrivalTime())
                .build();

        // Then
        assertThrows(DataIntegrityViolationException.class,
                () -> hotelService.createHotel(duplicateDto));
    }

    @Test
    void getHotelDetails_NotFound_ThrowsException() {
        assertThrows(HotelNotFoundException.class,
                () -> hotelService.getHotelDetails(999L));
    }

    @Test
    void searchHotels_ByCity_ReturnsFiltered() {
        // Given
        hotelService.createHotel(createDto);

        // When
        List<HotelSummaryDto> results = hotelService.searchHotels(
                null, null, "Test City", null, null);

        // Then
        assertEquals(1, results.size());
        assertTrue(results.get(0).getAddress().contains("Test City"));
    }

    @Test
    void addAmenities_Success() {
        // Given
        HotelSummaryDto hotel = hotelService.createHotel(createDto);

        // When
        hotelService.addAmenities(hotel.getId(), List.of("Pool", "Spa"));

        // Then
        assertEquals(2, hotelService.getHotelDetails(hotel.getId())
                .getAmenities().size());
    }
}