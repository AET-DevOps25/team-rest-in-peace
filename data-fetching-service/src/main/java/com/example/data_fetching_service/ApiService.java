package com.example.data_fetching_service;

import com.example.data_fetching_service.dto.BundestagApiResponse;
import com.example.data_fetching_service.dto.DomXmlParser;
import com.example.data_fetching_service.dto.PlenaryProtocolXml;
import com.example.data_fetching_service.model.*;
import com.example.data_fetching_service.repository.PlenaryProtocolRepository;
import com.example.data_fetching_service.repository.PersonRepository;
import com.example.data_fetching_service.repository.SpeechRepository;
import com.example.data_fetching_service.repository.SpeechChunkRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ApiService {
    private static final Logger logger = LoggerFactory.getLogger(ApiService.class);

    @Value("${bundestag.api.key}")
    private String apiKey;

    @Value("${bundestag.api.baseurl}")
    private String baseUrl;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private PlenaryProtocolRepository plenaryProtocolRepository;

    @Autowired
    private SpeechRepository speechRepository;

    @Autowired
    private SpeechChunkRepository speechChunkRepository;

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private ObjectMapper objectMapper;


    public void fetchAndStoreData(
            String aktualisiertStart, String aktualisiertEnd,
            String datumStart, String datumEnd,
            String dokumentnummer, String id,
            String vorgangstyp, String vorgangstypNotation,
            String wahlperiode, String zuordnung,
            String cursor, String format) {
        try {
            // Fetch metadata from the Bundestag API
            List<BundestagApiResponse.Document> documents = fetchPlenaryProtocolMetaDataList(
                    aktualisiertStart, aktualisiertEnd,
                    datumStart, datumEnd,
                    dokumentnummer, id,
                    vorgangstyp, vorgangstypNotation,
                    wahlperiode, zuordnung,
                    cursor, format
            );
            logger.info("Fetched {} documents from the Bundestag API", documents.size());

            // Process each document
            for (BundestagApiResponse.Document document : documents) {
                try {
                    int plenaryProtocolId = Integer.parseInt(document.getId());
                    PlenaryProtocol existingProtocol = plenaryProtocolRepository.findById(plenaryProtocolId).orElse(null);
                    if (existingProtocol != null) {
                        logger.info("Skipping document {} because it already exists in the database", document.getId());
                        break;
                    }
                    // Extract and store plenary protocol
                    PlenaryProtocol plenaryProtocol = extractAndStorePlenaryProtocol(document);

                    // Fetch and process XML file
                    if (document.getFundstelle() != null && document.getFundstelle().getXmlUrl() != null) {
                        PlenaryProtocolXml protocolXml = fetchXmlDocument(document.getFundstelle().getXmlUrl());
                        if (protocolXml != null && protocolXml.getSitzungsverlauf() != null) {
                            processSpeeches(protocolXml, plenaryProtocol);
                        }
                    }
                } catch (Exception e) {
                    logger.error("Error processing document {}: {}", document.getId(), e.getMessage(), e);
                }
            }
        } catch (Exception e) {
            logger.error("Error fetching and storing data: {}", e.getMessage(), e);
        }
    }

    private List<BundestagApiResponse.Document> fetchPlenaryProtocolMetaDataList(
            String aktualisiertStart, String aktualisiertEnd,
            String datumStart, String datumEnd,
            String dokumentnummer, String id,
            String vorgangstyp, String vorgangstypNotation,
            String wahlperiode, String zuordnung,
            String cursor, String format) {

        UriComponentsBuilder builder = UriComponentsBuilder
                .fromUriString(baseUrl)
                .path("/plenarprotokoll")
                .queryParam("apikey", apiKey);

        // Conditionally add parameters if they are not null or empty
        if (aktualisiertStart != null && !aktualisiertStart.isEmpty()) {
            builder.queryParam("f.aktualisiert.start", aktualisiertStart);
        }
        if (aktualisiertEnd != null && !aktualisiertEnd.isEmpty()) {
            builder.queryParam("f.aktualisiert.end", aktualisiertEnd);
        }
        if (datumStart != null && !datumStart.isEmpty()) {
            builder.queryParam("f.datum.start", datumStart);
        }
        if (datumEnd != null && !datumEnd.isEmpty()) {
            builder.queryParam("f.datum.end", datumEnd);
        }
        if (dokumentnummer != null && !dokumentnummer.isEmpty()) {
            builder.queryParam("f.dokumentnummer", dokumentnummer);
        }
        if (id != null && !id.isEmpty()) {
            builder.queryParam("f.id", id);
        }
        if (vorgangstyp != null && !vorgangstyp.isEmpty()) {
            builder.queryParam("f.vorgangstyp", vorgangstyp);
        }
        if (vorgangstypNotation != null && !vorgangstypNotation.isEmpty()) {
            builder.queryParam("f.vorgangstyp_notation", vorgangstypNotation);
        }
        if (wahlperiode != null && !wahlperiode.isEmpty()) {
            builder.queryParam("f.wahlperiode", wahlperiode);
        }
        if (zuordnung != null && !zuordnung.isEmpty()) {
            builder.queryParam("f.zuordnung", zuordnung);
        }
        if (cursor != null && !cursor.isEmpty()) {
            builder.queryParam("cursor", cursor);
        }
        if (format != null && !format.isEmpty()) {
            builder.queryParam("format", format);
        }


        ResponseEntity<String> response = restTemplate.getForEntity(builder.toUriString(), String.class);

        try {
            BundestagApiResponse apiResponse = objectMapper.readValue(response.getBody(), BundestagApiResponse.class);
            return apiResponse.getDocuments();
        } catch (Exception e) {
            logger.error("Error parsing API response: {}", e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    public PlenaryProtocolXml fetchXmlDocument(String xmlUrl) {
        try {
            ResponseEntity<String> response = restTemplate.getForEntity(xmlUrl, String.class);
            return DomXmlParser.parsePlenarprotokoll(response.getBody());
        } catch (IOException e) {
            logger.error("Error parsing XML document from {}: {}", xmlUrl, e.getMessage(), e);
            return null;
        } catch (Exception e) {
            logger.error("Error fetching XML document from {}: {}", xmlUrl, e.getMessage(), e);
            return null;
        }
    }

    private PlenaryProtocol extractAndStorePlenaryProtocol(BundestagApiResponse.Document document) {
        try {
            int id;
            try {
                id = Integer.parseInt(document.getId());
            } catch (NumberFormatException e) {
                logger.error("Invalid ID format: {}", document.getId());
                throw e;
            }

            Integer electionPeriod = document.getElectionPeriod();

            int documentNumber;
            try {
                String docNum = document.getDocumentNumber();
                if (docNum.contains("/")) {
                    documentNumber = Integer.parseInt(docNum.split("/")[1]);
                } else {
                    documentNumber = Integer.parseInt(docNum);
                }
            } catch (NumberFormatException e) {
                logger.error("Invalid document number format: {}", document.getDocumentNumber());
                throw e;
            }
            String publisher = document.getPublisher();

            PlenaryProtocol plenaryProtocol = new PlenaryProtocol();
            plenaryProtocol.setId(id);
            plenaryProtocol.setElectionPeriod(electionPeriod);
            plenaryProtocol.setDocumentNumber(documentNumber);
            plenaryProtocol.setPublisher(publisher);
            logger.info("Extracted plenary protocol {} for election period {}", plenaryProtocol.getId(), plenaryProtocol.getElectionPeriod());
            return plenaryProtocolRepository.save(plenaryProtocol);
        } catch (Exception e) {
            logger.error("Error extracting and storing plenary protocol: {}", e.getMessage(), e);
            throw e;
        }
    }

    private void processSpeeches(PlenaryProtocolXml protocolXml, PlenaryProtocol plenaryProtocol) {
        if (protocolXml.getSitzungsverlauf().getTagesordnungspunkte() == null) {
            return;
        }
        for (PlenaryProtocolXml.Tagesordnungspunkt tagesordnungspunkt : protocolXml.getSitzungsverlauf().getTagesordnungspunkte()) {
            for (PlenaryProtocolXml.Rede rede : tagesordnungspunkt.getReden()) {
                try {
                    // Process speaker
                    Person speaker = processSpeaker(rede.getRedner());

                    // Create speech
                    Speech speech = new Speech();
                    try {
                        speech.setId(Integer.parseInt(rede.getId().substring(2)));
                    } catch (NumberFormatException e) {
                        // If the ID is not a valid integer, use a hash code as fallback
                        logger.warn("Speech ID is not a valid integer: {}. Using hash code instead.", rede.getId());
                        speech.setId(rede.getId().hashCode());
                    }
                    speech.setPlenaryProtocol(plenaryProtocol);
                    speech.setPerson(speaker);

//                     Combine all paragraphs to create text_plain
                    String textPlain = rede.getInhalte() != null ?
                            rede.getInhalte().stream()
                                    .filter(x -> x instanceof PlenaryProtocolXml.SpeechParagraph)
                                    .map(x -> ((PlenaryProtocolXml.SpeechParagraph) x))
                                    .map(PlenaryProtocolXml.SpeechParagraph::getText)
                                    .collect(Collectors.joining("\n")) : "";
                    speech.setTextPlain(textPlain);

                    speech = speechRepository.save(speech);

                    // Process speech chunks
                    processSpeechChunks(rede, speech);
                    logger.info("Processed speech {} for plenary protocol {}", speech.getId(), plenaryProtocol.getId());
                } catch (Exception e) {
                    logger.error("Error processing speech {}: {}", rede.getId(), e.getMessage(), e);
                }
            }
        }
    }

    private Person processSpeaker(PlenaryProtocolXml.Redner redner) {
        if (redner == null || redner.getId() == null) {
            return null;
        }

        try {
            Integer id;
            try {
                id = Integer.parseInt(redner.getId());
            } catch (NumberFormatException e) {
                // If the ID is too large for an Integer or contains non-numeric characters,
                // use a hash code of the string as a fallback
                logger.warn("Speaker ID is not a valid integer: {}. Using hash code instead.", redner.getId());
                id = Integer.parseInt(redner.getId());
            }
            Optional<Person> existingPerson = personRepository.findById(id);

            if (existingPerson.isPresent()) {
                return existingPerson.get();
            }

            Person person = new Person();
            person.setId(id);

            if (redner.getName() != null) {
                // Set first name
                person.setFirstName(redner.getName().getVorname() != null ? redner.getName().getVorname() : "");

                // Set last name, including title if available
                String lastName = "";
                if (redner.getName().getTitel() != null && !redner.getName().getTitel().isEmpty()) {
                    lastName = redner.getName().getTitel() + " ";
                }
                lastName += (redner.getName().getNachname() != null ? redner.getName().getNachname() : "");
                person.setLastName(lastName.trim());
            } else {
                person.setFirstName("");
                person.setLastName("");
            }

            // Map faction to party
            String party = redner.getName().getFraktion();
            person.setParty(party);

            return personRepository.save(person);
        } catch (Exception e) {
            logger.error("Error processing speaker: {}", e.getMessage(), e);
            throw e;
        }
    }


    private void processSpeechChunks(PlenaryProtocolXml.Rede rede, Speech speech) {
        List<SpeechChunk> processedSpeechChunks = new ArrayList<>();
        if (rede.getInhalte() != null) {
            int index = 0;
            for (PlenaryProtocolXml.SpeechContent speechContent : rede.getInhalte()) {
                try {
                    SpeechChunk chunk = new SpeechChunk();
                    chunk.setIndex(index);
                    chunk.setSpeech(speech);
                    String type = speechContent instanceof PlenaryProtocolXml.SpeechParagraph ? "SPEECH" : "COMMENT";
                    chunk.setType(type);
                    chunk.setText(speechContent.getText());
                    processedSpeechChunks.add(chunk);

                } catch (Exception e) {
                    logger.error("Error processing speech chunk for speech {}: {}", speech.getId(), e.getMessage(), e);
                }
                index++;
            }
        }
        speechChunkRepository.saveAll(processedSpeechChunks);
        logger.info("Processed {} speech chunks for speech {}", processedSpeechChunks.size(), speech.getId());
    }
}
