package com.example.data_fetching_service.model;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "speech")
public class Speech {

    @Id
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "agenda_item_id")
    private AgendaItem agendaItem;

    @ManyToOne
    @JoinColumn(name = "person_id")
    private Person person;

    @Column(name = "text_plain")
    private String textPlain;


    @Transient // Hibernate will ignore this field for database operations
    @Column(name = "text_embedding")
    private float[] textEmbedding;

    @OneToMany(mappedBy = "speech", cascade = CascadeType.ALL)
    private List<SpeechChunk> speechChunks;

    // Getters and Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public AgendaItem getAgendaItem() {
        return agendaItem;
    }

    public void setAgendaItem(AgendaItem plenaryProtocol) {
        this.agendaItem = plenaryProtocol;
    }

    public String getTextPlain() {
        return textPlain;
    }

    public void setTextPlain(String textPlain) {
        this.textPlain = textPlain;
    }

    public float[] getTextEmbedding() {
        return textEmbedding;
    }

    public void setTextEmbedding(float[] textEmbedding) {
        this.textEmbedding = textEmbedding;
    }

    public List<SpeechChunk> getSpeechChunks() {
        return speechChunks;
    }

    public void setSpeechChunks(List<SpeechChunk> speechChunks) {
        this.speechChunks = speechChunks;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }
}
