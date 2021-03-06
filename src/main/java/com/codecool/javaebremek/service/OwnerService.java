package com.codecool.javaebremek.service;

import com.codecool.javaebremek.model.Animal;
import com.codecool.javaebremek.model.Owner;
import com.codecool.javaebremek.repository.OwnerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class OwnerService {
    
    @Autowired
    private OwnerRepository ownerRepository;

    public List<Owner> findAll() {
        return ownerRepository.findAll();
    }

    public Owner add(Owner owner) {
        owner.setId(null);
        return save(owner);
    }

    public Owner updateById(Long id, Owner owner) {
        if (ownerRepository.existsById(id)) {
            owner.setId(id);
            return save(owner);
        } else {
            throw new RuntimeException(
                    String.format("Can not be updated, because the id does not exist", id));
        }
    }

    private Owner save(Owner owner) {
        return ownerRepository.save(owner);
    }

    public Optional<Owner> findById(Long id) {
        return ownerRepository.findById(id);
    }

    public void deleteById(Long id) {
        ownerRepository.deleteById(id);
    }

    public List<Animal> getAnimalsByOwnersId(Long ownerId) {
        return ownerRepository.findById(ownerId).orElseThrow().getAnimals();
    }
}
