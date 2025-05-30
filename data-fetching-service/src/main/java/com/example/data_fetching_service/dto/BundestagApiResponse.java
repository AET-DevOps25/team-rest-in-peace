package com.example.data_fetching_service.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BundestagApiResponse {

    @JsonProperty("documents")
    private List<Document> documents;

    @JsonProperty("numFound")
    private int numFound;

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Document {
        @JsonProperty("id")
        private String id;

        @JsonProperty("dokumentart")
        private String documentType;

        @JsonProperty("typ")
        private String type;

        @JsonProperty("dokumentnummer")
        private String documentNumber;

        @JsonProperty("wahlperiode")
        private Integer electionPeriod;

        @JsonProperty("herausgeber")
        private String publisher;

        @JsonProperty("fundstelle")
        private Fundstelle fundstelle;

        @JsonProperty("vorgangsbezug")
        private List<Vorgangsbezug> vorgangsbezug;

        @JsonProperty("titel")
        private String title;

        @JsonProperty("datum")
        private String date;

        // Getters and Setters
        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getDocumentNumber() {
            return documentNumber;
        }

        public void setDocumentNumber(String documentNumber) {
            this.documentNumber = documentNumber;
        }

        public Integer getElectionPeriod() {
            return electionPeriod;
        }

        public void setElectionPeriod(Integer electionPeriod) {
            this.electionPeriod = electionPeriod;
        }

        public String getPublisher() {
            return publisher;
        }

        public void setPublisher(String publisher) {
            this.publisher = publisher;
        }

        public Fundstelle getFundstelle() {
            return fundstelle;
        }

        public void setFundstelle(Fundstelle fundstelle) {
            this.fundstelle = fundstelle;
        }

        public List<Vorgangsbezug> getVorgangsbezug() {
            return vorgangsbezug;
        }

        public void setVorgangsbezug(List<Vorgangsbezug> vorgangsbezug) {
            this.vorgangsbezug = vorgangsbezug;
        }

        public String getDocumentType() {
            return documentType;
        }

        public void setDocumentType(String documentType) {
            this.documentType = documentType;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Fundstelle {
        @JsonProperty("pdf_url")
        private String pdfUrl;

        @JsonProperty("xml_url")
        private String xmlUrl;

        @JsonProperty("id")
        private String id;

        @JsonProperty("dokumentnummer")
        private String documentNumber;

        @JsonProperty("datum")
        private String date;

        @JsonProperty("verteildatum")
        private String distributionDate;

        @JsonProperty("dokumentart")
        private String documentType;

        @JsonProperty("herausgeber")
        private String publisher;

        // Getters and Setters
        public String getPdfUrl() {
            return pdfUrl;
        }

        public void setPdfUrl(String pdfUrl) {
            this.pdfUrl = pdfUrl;
        }

        public String getXmlUrl() {
            return xmlUrl;
        }

        public void setXmlUrl(String xmlUrl) {
            this.xmlUrl = xmlUrl;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getDocumentNumber() {
            return documentNumber;
        }

        public void setDocumentNumber(String documentNumber) {
            this.documentNumber = documentNumber;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public String getDistributionDate() {
            return distributionDate;
        }

        public void setDistributionDate(String distributionDate) {
            this.distributionDate = distributionDate;
        }

        public String getDocumentType() {
            return documentType;
        }

        public void setDocumentType(String documentType) {
            this.documentType = documentType;
        }

        public String getPublisher() {
            return publisher;
        }

        public void setPublisher(String publisher) {
            this.publisher = publisher;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Vorgangsbezug {
        @JsonProperty("id")
        private String id;

        @JsonProperty("titel")
        private String title;

        @JsonProperty("vorgangstyp")
        private String type;

        // Getters and Setters
        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }
    }

    // Getters and Setters
    public List<Document> getDocuments() {
        return documents;
    }

    public void setDocuments(List<Document> documents) {
        this.documents = documents;
    }

    public int getNumFound() {
        return numFound;
    }

    public void setNumFound(int numFound) {
        this.numFound = numFound;
    }
}
