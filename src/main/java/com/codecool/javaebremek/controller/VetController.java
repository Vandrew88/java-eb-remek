package com.codecool.javaebremek.controller;

import com.codecool.javaebremek.model.Animal;
import com.codecool.javaebremek.model.Vet;
import com.codecool.javaebremek.service.VetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/vets")
public class VetController {
    
    @Autowired
    private VetService vetService;

    @GetMapping
    public ResponseEntity<?> findAll() {
        return ResponseEntity.ok(vetService.findAll());
    }

    @PostMapping
    public ResponseEntity<?> add(@RequestBody @Valid Vet vet, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(vetService.add(vet));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateById(@PathVariable Long id, @RequestBody @Valid Vet vet, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().build();
        }
        try {
            Vet result = vetService.updateById(id, vet);
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable Long id) {
        return ResponseEntity.ok(vetService.findById(id));
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
