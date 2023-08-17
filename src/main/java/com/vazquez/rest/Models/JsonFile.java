package com.vazquez.rest.Models;


import jakarta.persistence.*;




public class JsonFile {


    @Id
    private Long id;



    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }



    public static class Listid
    {


        private WeatherList[] list;


        public WeatherList[] getList() {
            return list;
        }

        public void setList(WeatherList[] list) {
            this.list = list;
        }
    }


    public static class WeatherList
    {

        private Long dt;
        private WeatherMain main;
        private WeatherA []weather;
        private String dt_txt;

        public Long getDt() {
            return dt;
        }

        public void setDt(Long dt) {
            this.dt = dt;
        }
        public WeatherMain getMain() {
            return main;
        }


        public void setMain(WeatherMain main) {
            this.main = main;
        }

        public String getDt_txt() {
            return dt_txt;
        }

        public void setDt_txt(String dt_txt) {
            this.dt_txt = dt_txt;
        }


        public WeatherA []getWeather() {
            return weather;
        }

        public void setWeather(WeatherA []weather) {
            this.weather = weather;
        }





    }


    public static class WeatherMain
    {

        private double temp;
        private double feels_like;
        private double temp_min;
        private double temp_max;






        public double getTemp() {
            return temp;
        }

        public void setTemp(double temp) {
            this.temp = temp;
        }

        public double getFeels_like() {
            return feels_like;
        }

        public void setFeels_like(double feels_like) {
            this.feels_like = feels_like;
        }

        public double getTemp_min() {
            return temp_min;
        }

        public void setTemp_min(double temp_min) {
            this.temp_min = temp_min;
        }

        public double getTemp_max() {
            return temp_max;
        }

        public void setTemp_max(double temp_max) {
            this.temp_max = temp_max;
        }
    }





    public static class WeatherA
    {

        private Long id;
        private String main;
        private String description;
        private String icon;


        public String getIcon() {
            return icon;
        }

        public void setIcon(String icon) {
            this.icon = icon;
        }
        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getMain() {
            return main;
        }

        public void setMain(String main) {
            this.main = main;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }
    }
}
