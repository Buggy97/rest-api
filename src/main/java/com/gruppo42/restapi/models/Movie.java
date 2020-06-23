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

    @Override
    public boolean equals(Object obj)
    {
        // If the object is compared with itself then return true
        if (obj == this) {
            return true;
        }

        /* Check if o is an instance of Complex or not
          "null instanceof [type]" also returns false */
        if (!(obj instanceof Movie)) {
            return false;
        }

        // typecast o to Complex so that we can compare data members
        Movie v = (Movie) obj;

        // Compare the data members and return accordingly
        return this.id.equals(v.id);
    }

    @Override
    public int hashCode()
    {
        return Integer.parseInt(id);
    }

    public Movie(String id)
    {
        this.id = id;
    }

    public String getId() { return id; }

    public void setId(String id) { this.id = id; }
}