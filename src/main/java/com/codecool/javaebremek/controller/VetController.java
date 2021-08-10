package com.codecool.javaebremek.controller;

import com.codecool.javaebremek.model.Animal;
import com.codecool.javaebremek.model.Vet;
import com.codecool.javaebremek.service.VetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/vets")
public class VetController {
    
    @Autowired
    private VetService vetService;

    @GetMapping
    public List<Vet> findAll() {
        return vetService.findAll();
    }

    @PostMapping
    public Vet add(@RequestBody Vet vet) {
        return vetService.add(vet);
    }

    @PutMapping("/{id}")
    public Vet updateById(@PathVariable Long id, @RequestBody Vet vet) {
        return vetService.updateById(id, vet);
    }

    @GetMapping("/{id}")
    public Optional<Vet> findById(@PathVariable Long id) {
        return vetService.findById(id);
    }

    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable Long id) {
        vetService.deleteById(id);
    }

    @GetMapping("/{vetId}/animals")
    public List<Animal> getAnimalsByVetsId(@PathVariable Long vetId) {
        return vetService.getAnimalsByVetsId(vetId);
    }
}
