package com.gruppo42.restapi.models;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;

@Entity
@Table(name = "movies")
@EntityListeners(AuditingEntityListener.class)
public class Movie
{
    @Id
    @Column(unique = true)
    private String id;

    public Movie() {}

    public Movie(String id)
    {
        this.id = id;
    }

    public String getId() { return id; }

    public void setId(String id) { this.id = id; }
}