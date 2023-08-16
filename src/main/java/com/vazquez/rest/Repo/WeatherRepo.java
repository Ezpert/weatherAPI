package com.vazquez.rest.Repo;

import com.vazquez.rest.Models.WeatherCityInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WeatherRepo extends JpaRepository<WeatherCityInfo, Long> {


}
