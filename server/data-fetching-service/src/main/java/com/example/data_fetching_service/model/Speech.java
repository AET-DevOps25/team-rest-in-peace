package com.example.data_fetching_service.model;

import jakarta.persistence.*;
import org.hibernate.annotations.ColumnTransformer;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.List;

@Entity
@Table(name = "speech")
public class Speech {

    @Id
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "plenary_protocol_id")
    private PlenaryProtocol plenaryProtocol;

    @ManyToOne
    @JoinColumn(name = "speaker_id")
    private Person speaker;

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

    public PlenaryProtocol getPlenaryProtocol() {
        return plenaryProtocol;
    }

    public void setPlenaryProtocol(PlenaryProtocol plenaryProtocol) {
        this.plenaryProtocol = plenaryProtocol;
    }

    public Person getSpeaker() {
        return speaker;
    }

    public void setSpeaker(Person speaker) {
        this.speaker = speaker;
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
}
