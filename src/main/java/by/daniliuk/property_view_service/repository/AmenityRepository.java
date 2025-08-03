package by.daniliuk.property_view_service.repository;

import by.daniliuk.property_view_service.model.Amenity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public interface AmenityRepository extends JpaRepository<Amenity, Long> {
    Optional<Amenity> findByName(String name);

    @Query("SELECT a.name AS key, COUNT(h) AS value " +
            "FROM Hotel h JOIN h.amenities a " +
            "GROUP BY a.name")
    List<Map<String, Object>> countAmenitiesUsage();
}
