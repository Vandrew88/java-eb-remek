package com.codecool.javaebremek.repository;

import com.codecool.javaebremek.model.Owner;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OwnerRepository extends JpaRepository<Owner, Long> {
}
