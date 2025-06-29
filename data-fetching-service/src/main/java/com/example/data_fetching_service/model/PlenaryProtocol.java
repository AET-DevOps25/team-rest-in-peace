package com.example.data_fetching_service.model;

import jakarta.persistence.*;
import java.util.Date; // Import Date
import java.util.List;

@Entity
@Table(name = "plenary_protocol")
public class PlenaryProtocol {

    @Id
    private Integer id;

    @Column(name = "election_period")
    private Integer electionPeriod;

    @Column(name = "document_number")
    private Integer documentNumber;

    @Column(name = "publisher")
    private String publisher;

    @Temporal(TemporalType.DATE)
    @Column(name = "date")
    private Date date;

    @Column(name = "sitzungs_nr")
    private Integer sitzungsNr;

    @OneToMany(mappedBy = "plenaryProtocol", cascade = CascadeType.ALL)
    private List<Speech> speeches;

    // Getters and Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getElectionPeriod() {
        return electionPeriod;
    }

    public void setElectionPeriod(Integer electionPeriod) {
        this.electionPeriod = electionPeriod;
    }

    public Integer getDocumentNumber() {
        return documentNumber;
    }

    public void setDocumentNumber(Integer documentNumber) {
        this.documentNumber = documentNumber;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public List<Speech> getSpeeches() {
        return speeches;
    }

    public void setSpeeches(List<Speech> speeches) {
        this.speeches = speeches;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Integer getSitzungsNr() {
        return sitzungsNr;
    }

    public void setSitzungsNr(Integer sitzungsNr) {
        this.sitzungsNr = sitzungsNr;
    }
}
