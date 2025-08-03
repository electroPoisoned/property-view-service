package by.daniliuk.property_view_service.service;

import by.daniliuk.property_view_service.repository.AmenityRepository;
import by.daniliuk.property_view_service.repository.HotelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HistogramService {

    private final HotelRepository hotelRepository;
    private final AmenityRepository amenityRepository;

    public Map<String, Long> getHistogram(String param) {
        switch (param.toLowerCase()) {
            case "brand":
                return convertListToMap(hotelRepository.countHotelsByBrand());
            case "city":
                return convertListToMap(hotelRepository.countHotelsByCity());
            case "country":
                return convertListToMap(hotelRepository.countHotelsByCountry());
            case "amenities":
                return convertListToMap(amenityRepository.countAmenitiesUsage());
            default:
                throw new IllegalArgumentException("Invalid histogram parameter: " + param);
        }
    }

    private Map<String, Long> convertListToMap(List<Map<String, Object>> list) {
        return list.stream()
                .collect(Collectors.toMap(
                        item -> (String) item.get("key"),
                        item -> (Long) item.get("value")
                ));
    }
}
