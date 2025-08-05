package by.daniliuk.property_view_service.service;

import by.daniliuk.property_view_service.dto.AddressDto;
import by.daniliuk.property_view_service.dto.ContactDto;
import by.daniliuk.property_view_service.dto.HotelCreateDto;
import by.daniliuk.property_view_service.dto.HotelDetailsDto;
import by.daniliuk.property_view_service.dto.HotelSummaryDto;
import by.daniliuk.property_view_service.dto.TimeDto;
import by.daniliuk.property_view_service.exception.HotelNotFoundException;
import by.daniliuk.property_view_service.model.Address;
import by.daniliuk.property_view_service.model.Amenity;
import by.daniliuk.property_view_service.model.Contact;
import by.daniliuk.property_view_service.model.Hotel;
import by.daniliuk.property_view_service.model.Time;
import by.daniliuk.property_view_service.repository.AmenityRepository;
import by.daniliuk.property_view_service.repository.HotelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HotelService {

    private final HotelRepository hotelRepository;
    private final AmenityRepository amenityRepository;

    @Transactional(readOnly = true)
    public List<HotelSummaryDto> getAllHotels() {
        return hotelRepository.findAll().stream()
                .map(this::convertToSummaryDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public HotelDetailsDto getHotelDetails(Long id) {
        Hotel hotel = hotelRepository.findById(id)
                .orElseThrow(() -> new HotelNotFoundException("Hotel not found with id: " + id));
        return convertToDetailsDto(hotel);
    }

    @Transactional
    public HotelSummaryDto createHotel(HotelCreateDto dto) {
        if (hotelRepository.existsByContactPhone(dto.getContacts().getPhone())) {
            throw new DataIntegrityViolationException("Phone number must be unique");
        }
        if (hotelRepository.existsByContactEmail(dto.getContacts().getEmail())) {
            throw new DataIntegrityViolationException("Email must be unique");
        }
        Hotel hotel = convertToHotelEntity(dto);
        hotel = hotelRepository.save(hotel);
        return convertToSummaryDto(hotel);
    }

    @Transactional
    public Set<String> addAmenities(Long hotelId, List<String> amenityNames) {
        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() -> new HotelNotFoundException("Hotel not found with id: " + hotelId));

        Set<Amenity> amenities = amenityNames.stream()
                .map(name -> amenityRepository.findByName(name)
                        .orElseGet(() -> amenityRepository.save(Amenity.builder().name(name).build())))
                .collect(Collectors.toSet());

        hotel.getAmenities().addAll(amenities);
        hotelRepository.save(hotel);

        return hotel.getAmenities().stream()
                .map(Amenity::getName)
                .collect(Collectors.toSet());
    }

    @Transactional(readOnly = true)
    public List<HotelSummaryDto> searchHotels(
            String name,
            String brand,
            String city,
            String country,
            List<String> amenities
    ) {
        List<Hotel> hotels = hotelRepository.searchHotels(name, brand, city, country);

        if (amenities != null && !amenities.isEmpty()) {
            hotels = filterHotelsByAmenities(hotels, amenities);
        }

        return hotels.stream()
                .map(this::convertToSummaryDto)
                .collect(Collectors.toList());
    }

    private List<Hotel> filterHotelsByAmenities(List<Hotel> hotels, List<String> amenityNames) {
        return hotels.stream()
                .filter(hotel -> {
                    Set<String> hotelAmenities = hotel.getAmenities().stream()
                            .map(Amenity::getName)
                            .collect(Collectors.toSet());
                    return hotelAmenities.containsAll(amenityNames);
                })
                .collect(Collectors.toList());
    }


    private HotelSummaryDto convertToSummaryDto(Hotel hotel) {
        AddressDto addressDto = convertToAddressDto(hotel.getAddress());
        return HotelSummaryDto.builder()
                .id(hotel.getId())
                .name(hotel.getName())
                .description(hotel.getDescription())
                .address(formatAddress(addressDto))
                .phone(hotel.getContact().getPhone())
                .build();
    }

    private HotelDetailsDto convertToDetailsDto(Hotel hotel) {
        return HotelDetailsDto.builder()
                .id(hotel.getId())
                .name(hotel.getName())
                .description(hotel.getDescription())
                .brand(hotel.getBrand())
                .address(convertToAddressDto(hotel.getAddress()))
                .contacts(convertToContactDto(hotel.getContact()))
                .arrivalTime(convertToTimeDto(hotel.getArrivalTime()))
                .amenities(hotel.getAmenities().stream().map(Amenity::getName).collect(Collectors.toSet()))
                .build();
    }

    private Hotel convertToHotelEntity(HotelCreateDto dto) {
        return Hotel.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .brand(dto.getBrand())
                .address(convertToAddressEntity(dto.getAddress()))
                .contact(convertToContactEntity(dto.getContacts()))
                .arrivalTime(convertToTimeEntity(dto.getArrivalTime()))
                .amenities(new HashSet<>())
                .build();
    }

    private Address convertToAddressEntity(AddressDto dto) {
        return Address.builder()
                .houseNumber(dto.getHouseNumber())
                .street(dto.getStreet())
                .city(dto.getCity())
                .country(dto.getCountry())
                .postCode(dto.getPostCode())
                .build();
    }

    private Contact convertToContactEntity(ContactDto dto) {
        return Contact.builder()
                .phone(dto.getPhone())
                .email(dto.getEmail())
                .build();
    }

    private Time convertToTimeEntity(TimeDto dto) {
        return Time.builder()
                .checkIn(dto.getCheckIn())
                .checkOut(dto.getCheckOut())
                .build();
    }

    private AddressDto convertToAddressDto(Address address) {
        return AddressDto.builder()
                .houseNumber(address.getHouseNumber())
                .street(address.getStreet())
                .city(address.getCity())
                .country(address.getCountry())
                .postCode(address.getPostCode())
                .build();
    }

    private ContactDto convertToContactDto(Contact contact) {
        return ContactDto.builder()
                .phone(contact.getPhone())
                .email(contact.getEmail())
                .build();
    }

    private TimeDto convertToTimeDto(Time time) {
        return TimeDto.builder()
                .checkIn(time.getCheckIn())
                .checkOut(time.getCheckOut())
                .build();
    }

    public static String formatAddress(AddressDto address) {
        return String.format("%d %s, %s, %s, %s",
                address.getHouseNumber(),
                address.getStreet(),
                address.getCity(),
                address.getPostCode(),
                address.getCountry());
    }
}