package com.example.data_fetching_service.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlText;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PlenaryProtocolXml {

    private Sitzungsverlauf sitzungsverlauf;

    private Date date;

    public Sitzungsverlauf getSitzungsverlauf() {
        return sitzungsverlauf;
    }

    public void setSitzungsverlauf(Sitzungsverlauf sitzungsverlauf) {
        this.sitzungsverlauf = sitzungsverlauf;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public static class Sitzungsverlauf {
        private List<Tagesordnungspunkt> tagesordnungspunkte;

        private Object sitzungsbeginn;

        private Object sitzungsende;

        private String text;

        public List<Tagesordnungspunkt> getTagesordnungspunkte() {
            return tagesordnungspunkte;
        }

        public void setTagesordnungspunkte(List<Tagesordnungspunkt> tagesordnungspunkte) {
            this.tagesordnungspunkte = tagesordnungspunkte;
        }

        public Object getSitzungsbeginn() {
            return sitzungsbeginn;
        }

        public void setSitzungsbeginn(Object sitzungsbeginn) {
            this.sitzungsbeginn = sitzungsbeginn;
        }

        public Object getSitzungsende() {
            return sitzungsende;
        }

        public void setSitzungsende(Object sitzungsende) {
            this.sitzungsende = sitzungsende;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }
    }

    public static class Tagesordnungspunkt {
        private String topId;

        private String title;

        private String pdfUrl;

        private List<Rede> reden;

        private List<Paragraph> paragraphs;

        private List<Kommentar> kommentare;


        public String getTopId() {
            return topId;
        }

        public void setTopId(String topId) {
            this.topId = topId;
        }

        public List<Rede> getReden() {
            return reden;
        }

        public void setReden(List<Rede> reden) {
            this.reden = reden;
        }

        public List<Paragraph> getParagraphs() {
            return paragraphs;
        }

        public void setParagraphs(List<Paragraph> paragraphs) {
            this.paragraphs = paragraphs;
        }

        public List<Kommentar> getKommentare() {
            return kommentare;
        }

        public void setKommentare(List<Kommentar> kommentare) {
            this.kommentare = kommentare;
        }


        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getPdfUrl() {
            return pdfUrl;
        }

        public void setPdfUrl(String pdfUrl) {
            this.pdfUrl = pdfUrl;
        }
    }

    public static class Rede {
        private String id;

        private Redner redner;

        private List<SpeechContent> inhalte;


        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public Redner getRedner() {
            return redner;
        }

        public void setRedner(Redner redner) {
            this.redner = redner;
        }

        public List<SpeechContent> getInhalte() {
            return inhalte;
        }

        public void setInhalte(List<SpeechContent> inhalte) {
            this.inhalte = inhalte;
        }
    }

    public static class Redner {
        private String id;

        private Name name;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public Name getName() {
            return name;
        }

        public void setName(Name name) {
            this.name = name;
        }
    }

    public static class Name {
        private String titel;

        private String vorname;

        private String nachname;

        private String fraktion;


        public String getTitel() {
            return titel;
        }

        public void setTitel(String titel) {
            this.titel = titel;
        }

        public String getVorname() {
            return vorname;
        }

        public void setVorname(String vorname) {
            this.vorname = vorname;
        }

        public String getNachname() {
            return nachname;
        }

        public void setNachname(String nachname) {
            this.nachname = nachname;
        }

        public String getFraktion() {
            return fraktion;
        }

        public void setFraktion(String fraktion) {
            this.fraktion = fraktion;
        }
    }

    public interface SpeechContent {
        public String getText();
    }

    public static class Paragraph {

        private String klasse;

        private String text;


        public String getKlasse() {
            return klasse;
        }

        public void setKlasse(String klasse) {
            this.klasse = klasse;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

    }

    public static class SpeechParagraph implements SpeechContent {

        private String klasse;

        private String text;


        public String getKlasse() {
            return klasse;
        }

        public void setKlasse(String klasse) {
            this.klasse = klasse;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

    }

    public static class Kommentar implements SpeechContent {
        private String text;

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }
    }
}
