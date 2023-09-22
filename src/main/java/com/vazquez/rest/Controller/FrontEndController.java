package com.vazquez.rest.Controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.vazquez.rest.Models.City;
import com.vazquez.rest.Models.CityName;
import com.vazquez.rest.Models.WeatherCityInfo;
import com.vazquez.rest.Repo.CityRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.FluentQuery;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.Function;

@Controller
public class FrontEndController {

    @Autowired
    private CityRepo cityRepo;



@Autowired
RestSpringBootController rest;



    @GetMapping("/search")
    public String searchCity(@RequestParam("zip-code") int zip, Model model) throws JsonProcessingException {

        City citym = new City();
        WeatherCityInfo weatherCityInfo;
        try {
            citym = cityRepo.findByZip(String.valueOf(zip)).get();
        }catch(NoSuchElementException e)
        {
            rest.saveCity(zip);
            citym = cityRepo.findByZip(String.valueOf(zip)).get();

        }

        weatherCityInfo = rest.getWeather(citym.getId());




        System.out.println("Lat: " + (int)citym.getLat() + ", Lon: " + (int)citym.getLon());

        CityName cityN = rest.getState(citym.getName(), citym.getLat(), citym.getLon());

        System.out.println("Name: " + cityN.getName());
        System.out.println("State: " + cityN.getState());
        System.out.println("Country: " + cityN.getCountry());
        System.out.println("Latitude: " + cityN.getLat());
        System.out.println("Longitude: " + cityN.getLon());
        System.out.println("---------------------------");

        String str = weatherCityInfo.getTemp();
        if (str.contains("."))
        {
            str = str.substring(0, str.indexOf('.'));
        }







        model.addAttribute("tempInfo", rest.removeD(weatherCityInfo.getTemp()));
        model.addAttribute("weatherInfo", rest.capatalize(weatherCityInfo.getWeatherDescription()));
        model.addAttribute("hum", weatherCityInfo.getHumidity());
        model.addAttribute("feels", rest.removeD(weatherCityInfo.getFeelsLike()));
        model.addAttribute("cityN", citym.getName());
        model.addAttribute("cityS", cityN.getState());
        model.addAttribute("cityC", cityN.getCountry());




        return "index";

    }



    @GetMapping("/")
    public String index(Model model)
    {

        return "home";

    }




}
