package com.rip.notification_service.model;

import jakarta.persistence.*;

@Entity
@Table(name = "notification_setting")
public class NotificationSetting {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "type", nullable = false)
    private String type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "person_id")
    private Person person;

    @Column(name = "party")
    private String party;

    protected NotificationSetting() {

    }

    public NotificationSetting(String party, String email, String type) {
        this.party = party;
        this.email = email;
        this.type = type;
    }

    public NotificationSetting(String email, String type, Person person) {
        this.email = email;
        this.type = type;
        this.person = person;
    }

    public NotificationSetting(String email, String type) {
        this.email = email;
        this.type = type;
    }

    public String getEmail() {
        return email;
    }

    public String getType() {
        return type;
    }

    public Person getPerson() {
        return person;
    }

    public String getParty() {
        return party;
    }
}
