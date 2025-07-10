package com.example.data_fetching_service.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BundestagPersonApiResponse {

    @JsonProperty("documents")
    private List<Person> documents;

    @JsonProperty("numFound")
    private int numFound;

    public List<Person> getDocuments() {
        return documents;
    }

    public int getNumFound() {
        return numFound;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Person {
        @JsonProperty("id")
        private String id;

        @JsonProperty("nachname")
        private String lastName;

        @JsonProperty("vorname")
        private String firstName;

        @JsonProperty("namenszusatz")
        private String nameAddition;

        @JsonProperty("typ")
        private String type;

        @JsonProperty("wahlperiode")
        private List<Integer> electionPeriods;

        @JsonProperty("basisdatum")
        private String baseDate;

        @JsonProperty("datum")
        private String date;

        @JsonProperty("aktualisiert")
        private String updated;

        @JsonProperty("titel")
        private String title;

        @JsonProperty("funktion")
        private List<String> function;

        @JsonProperty("funktionszusatz")
        private String functionAddition;

        @JsonProperty("fraktion")
        private String party;

        @JsonProperty("wahlkreiszusatz")
        private String constituency;

        @JsonProperty("ressort")
        private List<String> department;

        @JsonProperty("bundesland")
        private String state;

        @JsonProperty("person_roles")
        private List<PersonRole> roles;

        public String getId() {
            return id;
        }

        public String getLastName() {
            return lastName;
        }

        public String getFirstName() {
            return firstName;
        }

        public String getNameAddition() {
            return nameAddition;
        }

        public String getType() {
            return type;
        }

        public List<Integer> getElectionPeriods() {
            return electionPeriods;
        }

        public String getBaseDate() {
            return baseDate;
        }

        public String getDate() {
            return date;
        }

        public String getUpdated() {
            return updated;
        }

        public String getTitle() {
            return title;
        }

        public List<String> getFunction() {
            return function;
        }

        public String getFunctionAddition() {
            return functionAddition;
        }

        public String getParty() {
            return party;
        }

        public String getConstituency() {
            return constituency;
        }

        public List<String> getDepartment() {
            return department;
        }

        public String getState() {
            return state;
        }

        public List<PersonRole> getRoles() {
            return roles;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PersonRole {
        @JsonProperty("funktion")
        private String function;

        @JsonProperty("funktionszusatz")
        private String functionAddition;

        @JsonProperty("fraktion")
        private String party;

        @JsonProperty("nachname")
        private String lastName;

        @JsonProperty("vorname")
        private String firstName;

        @JsonProperty("namenszusatz")
        private String nameAddition;

        @JsonProperty("wahlperiode_nummer")
        private List<Integer> electionPeriods;

        @JsonProperty("wahlkreiszusatz")
        private String constituency;

        @JsonProperty("ressort_titel")
        private String departmentTitle;

        @JsonProperty("bundesland")
        private String state;

        public String getFunction() {
            return function;
        }

        public String getFunctionAddition() {
            return functionAddition;
        }

        public String getParty() {
            return party;
        }

        public String getLastName() {
            return lastName;
        }

        public String getFirstName() {
            return firstName;
        }

        public String getNameAddition() {
            return nameAddition;
        }

        public List<Integer> getElectionPeriods() {
            return electionPeriods;
        }

        public String getConstituency() {
            return constituency;
        }

        public String getDepartmentTitle() {
            return departmentTitle;
        }

        public String getState() {
            return state;
        }
    }
}
