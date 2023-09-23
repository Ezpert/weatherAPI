package com.vazquez.rest.Controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.vazquez.rest.Models.City;
import com.vazquez.rest.Models.CityName;
import com.vazquez.rest.Models.WeatherCityInfo;
import com.vazquez.rest.Repo.CityRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.HttpClientErrorException;

import java.util.NoSuchElementException;

@Controller
public class FrontEndController {

    @Autowired
    private CityRepo cityRepo;



@Autowired
RestSpringBootController rest;




    @GetMapping("/search-alt")
    public String getCityName(@RequestParam("cityName") String city_N, @RequestParam("cityState") String cityS, Model model) throws JsonProcessingException {
        if(city_N.isEmpty() || cityS.isEmpty())
        {
            return "empty-form";
        }
        try
        {



        CityName cityN = rest.getCityName(city_N, cityS);


        WeatherCityInfo weatherCityInfo;

        City citym;

        try {
             citym = cityRepo.findByName(cityN.getName()).get();
        }catch(NoSuchElementException e)
        {
            return "invalid-form";
        }
        weatherCityInfo = rest.getWeather(citym.getId());


        model.addAttribute("tempInfo", rest.removeD(weatherCityInfo.getTemp()));
        model.addAttribute("weatherInfo", rest.capatalize(weatherCityInfo.getWeatherDescription()));
        model.addAttribute("hum", weatherCityInfo.getHumidity());
        model.addAttribute("feels", rest.removeD(weatherCityInfo.getFeelsLike()));
        model.addAttribute("cityN", citym.getName());
        model.addAttribute("cityS", cityN.getState());
        model.addAttribute("cityC", cityN.getCountry());
        }catch(HttpClientErrorException e)
        {
            return "invalid-form";
        }





        return "index";
    }



    @GetMapping("/search")
    public String searchCity(@RequestParam("zip-code") String input, Model model) throws JsonProcessingException {

        if(input.isEmpty())
        {
            return "empty-form";
        }

    try {
        int zip = Integer.parseInt(input);

        if (zip == 0) {
            return handleZeroFormSubmission();
        }


        City citym;
        WeatherCityInfo weatherCityInfo;
        try {
            citym = cityRepo.findByZip(String.valueOf(zip)).get();
        } catch (NoSuchElementException e) {
            rest.saveCity(zip);
            citym = cityRepo.findByZip(String.valueOf(zip)).get();

        }

        weatherCityInfo = rest.getWeather(citym.getId());


        CityName cityN = rest.getState(citym.getName(), citym.getLat(), citym.getLon());

        System.out.println("Name: " + cityN.getName());
        System.out.println("State: " + cityN.getState());
        System.out.println("Country: " + cityN.getCountry());
        System.out.println("Latitude: " + cityN.getLat());
        System.out.println("Longitude: " + cityN.getLon());
        System.out.println("---------------------------");



        // String iconUrl =  "https://openweathermap.org/img/wn/" + weatherCityInfo.getIcon() + ".png";


        model.addAttribute("tempInfo", rest.removeD(weatherCityInfo.getTemp()));
        model.addAttribute("weatherInfo", rest.capatalize(weatherCityInfo.getWeatherDescription()));
        model.addAttribute("hum", weatherCityInfo.getHumidity());
        model.addAttribute("feels", rest.removeD(weatherCityInfo.getFeelsLike()));
        model.addAttribute("cityN", citym.getName());
        model.addAttribute("cityS", cityN.getState());
        model.addAttribute("cityC", cityN.getCountry());


        return "index";

    }catch(NumberFormatException e)
    {
        return "invalid-form";
    }catch(HttpClientErrorException e)
    {
        return "invalid-form";
    }

    }


    @GetMapping("/zero-form")
    private String handleZeroFormSubmission() {



        return "zero-form";
    }



    @GetMapping("/home-alt")
    private String changeHome()
    {

        return "home-alt";

    }


    @GetMapping("/")
    public String index()
    {

        return "home";

    }




}
