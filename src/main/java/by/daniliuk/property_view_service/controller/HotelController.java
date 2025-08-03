package by.daniliuk.property_view_service.controller;

import by.daniliuk.property_view_service.dto.HotelCreateDto;
import by.daniliuk.property_view_service.dto.HotelDetailsDto;
import by.daniliuk.property_view_service.dto.HotelSummaryDto;
import by.daniliuk.property_view_service.service.HotelService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/hotels")
@RequiredArgsConstructor
public class HotelController {

    private final HotelService hotelService;

    @GetMapping
    public List<HotelSummaryDto> getAllHotels() {
        return hotelService.getAllHotels();
    }

    @GetMapping("/{id}")
    public HotelDetailsDto getHotelById(@PathVariable Long id) {
        return hotelService.getHotelDetails(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public HotelSummaryDto createHotel(@Valid @RequestBody HotelCreateDto dto) {
        return hotelService.createHotel(dto);
    }

    @PostMapping("/{id}/amenities")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void addAmenities(@PathVariable Long id, @RequestBody List<String> amenities) {
        hotelService.addAmenities(id, amenities);
    }
}