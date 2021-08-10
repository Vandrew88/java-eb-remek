package com.codecool.javaebremek.service;

import com.codecool.javaebremek.model.Animal;
import com.codecool.javaebremek.repository.AnimalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AnimalService {

    @Autowired
    private AnimalRepository animalRepository;

    public List<Animal> findAll() {
        return animalRepository.findAll();
    }

    public Animal add(Animal animal) {
        animal.setId(null);
        return save(animal);
    }

    public Animal updateById(Long id, Animal animal) {
        if (animalRepository.existsById(id)) {
            animal.setId(id);
            return save(animal);
        } else {
            throw new RuntimeException(
                    String.format("Can not be updated, because the id does not exist", id));
        }
    }

    private Animal save(Animal animal) {
        return animalRepository.save(animal);
    }

    public Optional<Animal> findById(Long aLong) {
        return animalRepository.findById(aLong);
    }

    public void deleteById(Long aLong) {
        animalRepository.deleteById(aLong);
    }
}
