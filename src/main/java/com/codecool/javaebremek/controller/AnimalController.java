package com.codecool.javaebremek.controller;

import com.codecool.javaebremek.model.Animal;
import com.codecool.javaebremek.service.AnimalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/animals")
public class AnimalController {

    @Autowired
    private AnimalService animalService;

    @GetMapping
    public List<Animal> findAll() {
        return animalService.findAll();
    }

    @PostMapping
    public Animal add(@RequestBody Animal animal) {
        return animalService.add(animal);
    }

    @PutMapping("/{id}")
    public Animal updateById(@PathVariable Long id, @RequestBody Animal animal) {
        return animalService.updateById(id, animal);
    }

    @GetMapping("/{id}")
    public Optional<Animal> findById(@PathVariable Long id) {
        return animalService.findById(id);
    }

    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable Long id) {
        animalService.deleteById(id);
    }
}
