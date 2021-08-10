package com.codecool.javaebremek.controller;

import com.codecool.javaebremek.model.Animal;
import com.codecool.javaebremek.model.Owner;
import com.codecool.javaebremek.service.OwnerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/owners")
public class OwnerController {
    
    @Autowired
    private OwnerService ownerService;

    @GetMapping
    public List<Owner> findAll() {
        return ownerService.findAll();
    }

    @PostMapping
    public Owner add(@RequestBody Owner owner) {
        return ownerService.add(owner);
    }

    @PutMapping("/{id}")
    public Owner updateById(@PathVariable Long id, @RequestBody Owner owner) {
        return ownerService.updateById(id, owner);
    }

    @GetMapping("/{id}")
    public Optional<Owner> findById(@PathVariable Long id) {
        return ownerService.findById(id);
    }

    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable Long id) {
        ownerService.deleteById(id);
    }

    @GetMapping("/{ownerId}/animals")
    public List<Animal> getAnimalsByOwnersId(@PathVariable Long ownerId) {
        return ownerService.getAnimalsByOwnersId(ownerId);
    }
}
