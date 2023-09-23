package com.vazquez.rest.Controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vazquez.rest.Models.*;
import com.vazquez.rest.Repo.WeatherRepo;
import com.vazquez.rest.Repo.CityRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;


import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

@RestController
public class RestSpringBootController {

    @Autowired
    private CityRepo cityRepo;




    @Autowired
    private WeatherRepo weatherRepo;
    ObjectMapper objectMapper = new ObjectMapper();







    @GetMapping("/cities")
    public List<City> getCities()
    {
        //returns all of the cities currently
        return cityRepo.findAll();
    }


    //Eventually fix this so we could add cities not just by zip codes but also by names

    @PostMapping("/saveName/{cityName}/{stateX}")
    public CityName getCityName(@PathVariable String cityName, @PathVariable String stateX) throws JsonProcessingException {

        String url = "http://api.openweathermap.org/geo/1.0/direct?q=" + cityName + "&limit=5&appid=c051ee13146872970e23ec2fa286f339";
        RestTemplate restTemplate = new RestTemplate();

        CityName[] cityNames = restTemplate.getForObject(url, CityName[].class);
        CityName firstCity = new CityName();

        System.out.println(stateX);

        for (CityName city : cityNames) {

            if(city.getState().equals(stateX))
            {
                firstCity.setName(city.getName());
                firstCity.setState(city.getState());
                firstCity.setCountry(city.getCountry());
                firstCity.setLat(city.getLat());
                firstCity.setLon(city.getLon());

                return firstCity;

            }
            System.out.println("Name: " + city.getName());
            System.out.println("State: " + city.getState());
            System.out.println("Country: " + city.getCountry());
            System.out.println("Latitude: " + city.getLat());
            System.out.println("Longitude: " + city.getLon());
            System.out.println("---------------------------");
        }

        return firstCity;
    }

    public CityName getState(String cityName, double lat, double lon) throws JsonProcessingException {

        String url = "http://api.openweathermap.org/geo/1.0/direct?q=" + cityName + "&limit=5&appid=c051ee13146872970e23ec2fa286f339";
        RestTemplate restTemplate = new RestTemplate();

        CityName[] cityNames = restTemplate.getForObject(url, CityName[].class);
        CityName firstCity = new CityName();





        for (CityName city : cityNames) {

            if((int)city.getLat() == (int)lat && (int)city.getLon() == (int)lon)
            {
                firstCity.setName(city.getName());
                firstCity.setState(city.getState());
                firstCity.setCountry(city.getCountry());
                firstCity.setLat(city.getLat());
                firstCity.setLon(city.getLon());

                return firstCity;

            }
            System.out.println("Name: " + city.getName());
            System.out.println("State: " + city.getState());
            System.out.println("Country: " + city.getCountry());
            System.out.println("Latitude: " + city.getLat());
            System.out.println("Longitude: " + city.getLon());
            System.out.println("---------------------------");
        }

        return firstCity;
    }






    //Save city by zip code
    @PostMapping("/save/{zip}")
    public String saveCity(@PathVariable int zip) throws JsonProcessingException {

        String url = ("https://api.openweathermap.org/geo/1.0/zip?zip=" + zip + "&appid=c051ee13146872970e23ec2fa286f339");
        RestTemplate restTemplate = new RestTemplate();



        City city1 = restTemplate.getForObject(url, City.class);



        if (cityRepo != null && cityRepo.existsByName(city1.getName())) {
            boolean cityExists = cityRepo.existsByName(city1.getName());
            // perform repository operations
        if(cityExists)
        {
            return "Duplicate City...";
        }


        cityRepo.save(city1);
        createWeather(city1.getId());
        return "Saved....";
        }

        cityRepo.save(city1);


        return "Null";

    }






    @DeleteMapping("/delete/{cityId}")
    public String deleteCity(@PathVariable long cityId) {

        if (!cityRepo.existsById(cityId)) {

            try {
                WeatherCityInfo deleteInfo = weatherRepo.findById(cityId).get();
                weatherRepo.delete(deleteInfo);
            } catch(NoSuchElementException e)
            {
                e.printStackTrace();

            }

            return "No City Found with the ID:  " + cityId;

        }

        WeatherCityInfo deleteInfo = weatherRepo.findById(cityId).get();
        City deleteCity = cityRepo.findById(cityId).get();
        cityRepo.delete(deleteCity);
        weatherRepo.delete(deleteInfo);

        return "Deleted City with the ID: " + cityId;

    }


    public void createWeather(long cityId) throws JsonProcessingException
    {

        City city = cityRepo.findById(cityId).get();
        WeatherCityInfo weatherCityInfo = new WeatherCityInfo();

        String url = "https://api.openweathermap.org/data/2.5/forecast?appid=c051ee13146872970e23ec2fa286f339&units=imperial&lat=" + city.getLat() + "&lon=" + city.getLon();

        RestTemplate restTemplate = new RestTemplate();

        JsonFile.Listid weather = restTemplate.getForObject(url, JsonFile.Listid.class);

        String jsonString = objectMapper.writeValueAsString(weather);

        JsonNode jsonNode = objectMapper.readTree(jsonString);

        HashMap<String, String> keyValues = new HashMap<>();
        getAllKeysAndValues(jsonNode, keyValues);


        for (Map.Entry<String, String> entry : keyValues.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            System.out.println(key + ": " + value);
        }

        long epochTimeSeconds = Long.parseLong(keyValues.get("dt"));

        LocalDateTime dateTime = LocalDateTime.ofInstant(Instant.ofEpochSecond(epochTimeSeconds), ZoneId.systemDefault());

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy h:mm a");
        String formattedTime = dateTime.format(formatter);

        System.out.println(formattedTime);



        weatherCityInfo.setId(cityId);
        weatherCityInfo.setCityName(city.getName());
        weatherCityInfo.setTemp(keyValues.get("temp"));
        weatherCityInfo.setFeelsLike(keyValues.get("feels_like"));
        weatherCityInfo.setHumidity(keyValues.get("humidity"));
        weatherCityInfo.setWeather(keyValues.get("main"));
        weatherCityInfo.setIcon(keyValues.get("icon"));
        weatherCityInfo.setWeatherDescription(keyValues.get("description"));
        weatherCityInfo.setDate(formattedTime);


        weatherRepo.save(weatherCityInfo);

    }


    @GetMapping(value = "/weather/{cityId}")
    public WeatherCityInfo getWeather(@PathVariable long cityId) throws JsonProcessingException {

        createWeather(cityId);
        return weatherRepo.findById(cityId).get();
    }


    public String capatalize(String str)
    {
        String[] words = str.split(" ");
        StringBuilder result = new StringBuilder();

        for (String word : words) {
            if (!word.isEmpty()) {
                result.append(Character.toUpperCase(word.charAt(0))).append(word.substring(1)).append(" ");
            }
        }

        String capitalizedString = result.toString().trim();

        return capitalizedString;


    }
    public String removeD(String str)
    {

        if (str.contains("."))
        {
            str = str.substring(0, str.indexOf('.'));
        }
        return str;
    }




    private static void getAllKeysAndValues(JsonNode jsonNode, HashMap<String, String> keyValues) {
        if (jsonNode.isObject()) {
            Iterator<String> fieldNames = jsonNode.fieldNames();
            while (fieldNames.hasNext()) {
                String fieldName = fieldNames.next();
                JsonNode fieldValue = jsonNode.get(fieldName);

                // if current node is 'list' key, process only first element in array
                if ("list".equals(fieldName) && fieldValue.isArray() && fieldValue.size() > 0) {
                    getAllKeysAndValues(fieldValue.get(0), keyValues);
                    // Prevent processing rest of the array
                    continue;
                }

                if (fieldValue.isValueNode()) {
                    keyValues.put(fieldName, fieldValue.asText());
                }
                getAllKeysAndValues(fieldValue, keyValues);
            }
        } else if (jsonNode.isArray()) {
            for (JsonNode arrayElement : jsonNode) {
                getAllKeysAndValues(arrayElement, keyValues);
            }
        }
    }





}
