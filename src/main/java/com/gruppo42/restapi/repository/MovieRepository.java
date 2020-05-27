package com.gruppo42.restapi.repository;

import com.gruppo42.restapi.models.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MovieRepository extends JpaRepository<Movie, String>
{
    Optional<Movie> findById(String id);
}
