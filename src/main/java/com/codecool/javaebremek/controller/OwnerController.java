package com.codecool.javaebremek.controller;

import com.codecool.javaebremek.model.Animal;
import com.codecool.javaebremek.model.Owner;
import com.codecool.javaebremek.service.OwnerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/owners")
public class OwnerController {
    
    @Autowired
    private OwnerService ownerService;

    @GetMapping
    public ResponseEntity<?> findAll() {
        return ResponseEntity.ok(ownerService.findAll());
    }

    @PostMapping
    public ResponseEntity<?> add(@RequestBody @Valid Owner owner, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(ownerService.add(owner));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateById(@PathVariable Long id, @RequestBody @Valid Owner owner, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().build();
        }
        try {
            Owner result = ownerService.updateById(id, owner);
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable Long id) {
        return ResponseEntity.ok(ownerService.findById(id));
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
