package by.daniliuk.property_view_service.repository;

import by.daniliuk.property_view_service.model.Hotel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface HotelRepository extends JpaRepository<Hotel, Long> {

    @Query("SELECT h FROM Hotel h " +
            "WHERE (:name IS NULL OR LOWER(h.name) LIKE LOWER(CONCAT('%', :name, '%'))) " +
            "AND (:brand IS NULL OR LOWER(h.brand) = LOWER(:brand)) " +
            "AND (:city IS NULL OR LOWER(h.address.city) = LOWER(:city)) " +
            "AND (:country IS NULL OR LOWER(h.address.country) = LOWER(:country)) " +
            "AND (:amenity IS NULL OR EXISTS (SELECT a FROM h.amenities a WHERE LOWER(a.name) = LOWER(:amenity)))")
    List<Hotel> searchHotels(
            @Param("name") String name,
            @Param("brand") String brand,
            @Param("city") String city,
            @Param("country") String country,
            @Param("amenity") String amenity
    );

    @Query("SELECT h.address.city AS key, COUNT(h) AS value FROM Hotel h GROUP BY h.address.city")
    List<Map<String, Object>> countHotelsByCity();

    @Query("SELECT h.address.country AS key, COUNT(h) AS value FROM Hotel h GROUP BY h.address.country")
    List<Map<String, Object>> countHotelsByCountry();

    @Query("SELECT h.brand AS key, COUNT(h) AS value FROM Hotel h GROUP BY h.brand")
    List<Map<String, Object>> countHotelsByBrand();

    @Query("SELECT CASE WHEN COUNT(h) > 0 THEN true ELSE false END " +
            "FROM Hotel h WHERE h.contact.phone = :phone")
    boolean existsByContactPhone(@Param("phone") String phone);

    @Query("SELECT CASE WHEN COUNT(h) > 0 THEN true ELSE false END " +
            "FROM Hotel h WHERE h.contact.email = :email")
    boolean existsByContactEmail(@Param("email") String email);
}
