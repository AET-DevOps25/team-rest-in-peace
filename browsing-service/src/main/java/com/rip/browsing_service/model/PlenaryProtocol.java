package com.rip.browsing_service.model;

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

    @Column(name = "summary")
    private String summary;

    @Temporal(TemporalType.DATE)
    @Column(name = "date")
    private Date date;

    @OneToMany(mappedBy = "plenaryProtocol", cascade = CascadeType.ALL)
    private List<AgendaItem> agendaItems;

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

    public List<AgendaItem> getAgendaItems() {
        return agendaItems;
    }

    public void setAgendaItems(List<AgendaItem> agendaItems) {
        this.agendaItems = agendaItems;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

}
