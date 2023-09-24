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
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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
    public List<City> getCities() {
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

            if (city.getState().equals(stateX)) {
                firstCity.setName(city.getName());
                firstCity.setState(city.getState());
                firstCity.setCountry(city.getCountry());
                firstCity.setLat(city.getLat());
                firstCity.setLon(city.getLon());

                return firstCity;

            }
            printCityName(city);
        }

        return firstCity;
    }

    public CityName getState(String cityName, double lat, double lon) throws JsonProcessingException {

        String url = "http://api.openweathermap.org/geo/1.0/direct?q=" + cityName + "&limit=5&appid=c051ee13146872970e23ec2fa286f339";
        RestTemplate restTemplate = new RestTemplate();

        CityName[] cityNames = restTemplate.getForObject(url, CityName[].class);
        CityName firstCity = new CityName();


        for (CityName city : cityNames) {

            if ((int) city.getLat() == (int) lat && (int) city.getLon() == (int) lon) {
                firstCity.setName(city.getName());
                firstCity.setState(city.getState());
                firstCity.setCountry(city.getCountry());
                firstCity.setLat(city.getLat());
                firstCity.setLon(city.getLon());

                return firstCity;

            }
            printCityName(city);
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
            if (cityExists) {
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
            } catch (NoSuchElementException e) {
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


    public void createWeather(long cityId) throws JsonProcessingException {

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

    @GetMapping("/getZip/{lat}/{lon}")
    public String getZip(@PathVariable double lat, @PathVariable double lon) throws NoSuchAlgorithmException, KeyManagementException, JsonProcessingException {
        DecimalFormat decimalFormat = new DecimalFormat("#.######"); // Format to 6 decimal places

        String formattedLat = decimalFormat.format(lat);
        String formattedLon = decimalFormat.format(lon);
        System.out.println(formattedLon + " " + formattedLat);

        String url = ("https://maps.googleapis.com/maps/api/geocode/json?latlng=" + formattedLat + "," + formattedLon + "&result_type=postal_code&key=AIzaSyAX6-S2FTlBB_m50RZxA8F-kUJLj85AlX4");

        RestTemplate rest = new RestTemplate();

        PostalCodes.PostalList postalCodes = rest.getForObject(url, PostalCodes.PostalList.class);

        if (postalCodes != null && postalCodes.getResults() != null && postalCodes.getResults().length > 0) {
            PostalCodes.AddressList firstAddress = postalCodes.getResults()[0];
            PostalCodes.PCodes[] addressComponents = firstAddress.getAddress_components();
            if (addressComponents != null && addressComponents.length > 0) {
                String longName = addressComponents[0].getLong_name();
                String shortName = addressComponents[0].getShort_name();
                System.out.println("long_name: " + longName);
                System.out.println("short_name: " + shortName);
                return shortName;
            } else {
                System.out.println("No address components found.");
            }
        } else {
            System.out.println("Failed to retrieve postal codes.");
        }

        return "Done";
    }


    @GetMapping(value = "/weather/{cityId}")
    public WeatherCityInfo getWeather(@PathVariable long cityId) throws JsonProcessingException {

        createWeather(cityId);
        return weatherRepo.findById(cityId).get();
    }


    public String capatalize(String str) {
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

    public String removeD(String str) {

        if (str.contains(".")) {
            str = str.substring(0, str.indexOf('.'));
        }
        return str;
    }


    public String getWeatherType(String str, String date) {

        switch (str) {
            case "clear sky": {

                return isDay(date) ? "sun": "moon2";

            }
            case "few clouds", "scattered clouds", "broken clouds", "overcast clouds": {

                return isDay(date) ? "suncloud":"mooncloudy";


            }
            case "moderate rain", "light rain", "extreme rain", "very heavy rain", "heavy intensity rain": {
                return "scatteredrain";


            }
            case "light intensity shower rain", "shower rain", "heavy intensity shower rain", "ragged shower rain", "light intensity drizzle", "drizzle rain", "drizzle", "heavy intensity drizzle", "light intensity drizzle rain", "heavy intensity drizzle rain", "shower rain and drizzle", "heavy shower rain and drizzle", "shower drizzle": {
                return "rain";

            }
            case "thunderstorm with heavy rain", "light thunderstorm", "heavy thunderstorm", "ragged thunderstorm", "thunderstorm with light drizzle", "thunderstorm with drizzle", "thunderstorm with heavy drizzle", "thunderstorm", "thunderstorm with light rain", "thunderstorm with rain": {
                return "thunderstorm";

            }
            case "snow", "freezing rain", "light snow", "heavy snow", "sleet", "light shower sleet", "shower sleet", "light rain and snow", "rain and snow", "light shower snow", "shower snow", "heavy shower snow": {
                return "snow";

            }
            case "mist", "smoke", "haze", "sand/dust whirls", "fog", "sand", "dust", "volcanic ash", "squalls", "tornado": {
                return "mist";

            }

            default:
                return "ERROR";

        }
    }

    public static boolean isDay(String dateTimeString) {
        SimpleDateFormat inputFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm a");
        SimpleDateFormat outputFormat = new SimpleDateFormat("hh:mm a");

        try {
            Date dateTime = inputFormat.parse(dateTimeString);
            String timeOnly = outputFormat.format(dateTime);
            Date nightStart = inputFormat.parse("06/01/2000 06:00 PM");
            Date nightEnd = inputFormat.parse("06/01/2000 06:00 AM");

            if (dateTime.after(nightStart) || dateTime.before(nightEnd)) {
                return false;
            } else {
                return true;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return true;
    }

    public void printCityName(CityName cityN)
    {
        System.out.println("Name: " + cityN.getName());
        System.out.println("State: " + cityN.getState());
        System.out.println("Country: " + cityN.getCountry());
        System.out.println("Latitude: " + cityN.getLat());
        System.out.println("Longitude: " + cityN.getLon());
        System.out.println("---------------------------");

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
