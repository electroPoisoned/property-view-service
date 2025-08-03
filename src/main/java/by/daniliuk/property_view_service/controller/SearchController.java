package by.daniliuk.property_view_service.controller;

import by.daniliuk.property_view_service.dto.HotelSummaryDto;
import by.daniliuk.property_view_service.service.HotelService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/search")
@RequiredArgsConstructor
public class SearchController {

    private final HotelService hotelService;

    @GetMapping
    public List<HotelSummaryDto> searchHotels(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String brand,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String country,
            @RequestParam(required = false) String amenity) {

        return hotelService.searchHotels(name, brand, city, country, amenity);
    }
}
