package com.example.data_fetching_service;

import com.example.data_fetching_service.dto.BundestagApiResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.*;

public class BundestagApiResponseTest {

    @Test
    public void testParseJsonFile() throws IOException {
        // Load the JSON file from the test resources
        ClassPathResource resource = new ClassPathResource("20-214.json");
        InputStream inputStream = resource.getInputStream();
        
        // Parse the JSON file into a BundestagApiResponse object
        ObjectMapper objectMapper = new ObjectMapper();
        BundestagApiResponse.Document document = objectMapper.readValue(inputStream, BundestagApiResponse.Document.class);
        
        // Assert that the parsed object has the expected values
        assertEquals("5701", document.getId());
        assertEquals("20/214", document.getDocumentNumber());
        assertEquals(20, document.getElectionPeriod());
        assertEquals("BT", document.getPublisher());
        assertEquals("Plenarprotokoll", document.getDocumentType());
        assertEquals("Protokoll der 214. Sitzung des 20. Deutschen Bundestages", document.getTitle());
        assertEquals("2025-03-18", document.getDate());
        
        // Assert that the fundstelle has the expected values
        assertNotNull(document.getFundstelle());
        assertEquals("https://dserver.bundestag.de/btp/20/20214.pdf", document.getFundstelle().getPdfUrl());
        assertEquals("https://dserver.bundestag.de/btp/20/20214.xml", document.getFundstelle().getXmlUrl());
        assertEquals("5701", document.getFundstelle().getId());
        assertEquals("20/214", document.getFundstelle().getDocumentNumber());
        assertEquals("2025-03-18", document.getFundstelle().getDate());
        assertEquals("2025-03-19", document.getFundstelle().getDistributionDate());
        assertEquals("Plenarprotokoll", document.getFundstelle().getDocumentType());
        assertEquals("BT", document.getFundstelle().getPublisher());
        
        // Assert that the vorgangsbezug list has the expected values
        assertNotNull(document.getVorgangsbezug());
        assertEquals(2, document.getVorgangsbezug().size());
        assertEquals("320785", document.getVorgangsbezug().get(0).getId());
        assertEquals("Gesetz zur Errichtung eines Verteidigungsfonds für Deutschland und zur Änderung des Grundgesetzes (Artikel 87a)", 
                document.getVorgangsbezug().get(0).getTitle());
        assertEquals("Gesetzgebung", document.getVorgangsbezug().get(0).getType());
        assertEquals("320784", document.getVorgangsbezug().get(1).getId());
        assertEquals("Gesetz zur Änderung des Grundgesetzes (Artikel 109, 115 und 143h)", 
                document.getVorgangsbezug().get(1).getTitle());
        assertEquals("Gesetzgebung", document.getVorgangsbezug().get(1).getType());
    }
}