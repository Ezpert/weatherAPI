package com.vazquez.rest.Repo;


import com.vazquez.rest.Models.City;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import java.util.Optional;

@Repository
public interface CityRepo extends JpaRepository<City, Long> {

    Optional<City> findByZip(String zip);
    boolean existsByName(String city_name);
    boolean existsByZip(String zip);


}
