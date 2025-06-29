package com.example.data_fetching_service.model;

import jakarta.persistence.*;

@Entity
@Table(name = "agenda_item")
public class AgendaItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name")
    private String name;

    @Column(name = "title")
    private String title;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plenary_protocol_id")
    private PlenaryProtocol plenaryProtocol;

    @Column(name = "pdf_link")
    private String pdfLink;

    // Getters and Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public PlenaryProtocol getPlenaryProtocol() {
        return plenaryProtocol;
    }

    public void setPlenaryProtocol(PlenaryProtocol plenaryProtocol) {
        this.plenaryProtocol = plenaryProtocol;
    }

    public String getPdfLink() {
        return pdfLink;
    }

    public void setPdfLink(String pdfLink) {
        this.pdfLink = pdfLink;
    }
}
