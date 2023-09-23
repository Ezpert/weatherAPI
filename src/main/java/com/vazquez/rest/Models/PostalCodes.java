package com.vazquez.rest.Models;


import jakarta.persistence.Id;

public class PostalCodes {

    @Id
    private Long id;

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public static class PostalList {
        private AddressList[] results;

        public AddressList[] getResults() {
            return results;
        }

        public void setResults(AddressList[] results) {
            this.results = results;
        }
    }

    public static class AddressList {


        private PCodes[] address_components;

        public PCodes[] getAddress_components() {
            return address_components;
        }

        public void setAddress_components(PCodes[] address_components) {
            this.address_components = address_components;
        }


    }
    public static class PCodes
    {

        private String long_name;
        private String short_name;
        public String getLong_name() {
            return long_name;
        }

        public void setLong_name(String long_name) {
            this.long_name = long_name;
        }

        public String getShort_name() {
            return short_name;
        }

        public void setShort_name(String short_name) {
            this.short_name = short_name;
        }
    }

}