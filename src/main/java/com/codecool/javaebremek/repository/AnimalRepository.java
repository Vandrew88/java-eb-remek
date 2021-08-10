package com.codecool.javaebremek.repository;

import com.codecool.javaebremek.model.Animal;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnimalRepository extends JpaRepository<Animal, Long> {
}
