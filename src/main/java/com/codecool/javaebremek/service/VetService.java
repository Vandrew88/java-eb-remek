package com.codecool.javaebremek.service;

import com.codecool.javaebremek.model.Vet;
import com.codecool.javaebremek.repository.VetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class VetService {
    
    @Autowired
    private VetRepository vetRepository;

    public List<Vet> findAll() {
        return vetRepository.findAll();
    }

    public Vet add(Vet vet) {
        vet.setId(null);
        return save(vet);
    }

    public Vet updateById(Long id, Vet vet) {
        if (vetRepository.existsById(id)) {
            vet.setId(id);
            return save(vet);
        } else {
            throw new RuntimeException(
                    String.format("Can not be updated, because the id does not exist", id));
        }
    }

    private Vet save(Vet vet) {
        return vetRepository.save(vet);
    }

    public Optional<Vet> findById(Long aLong) {
        return vetRepository.findById(aLong);
    }

    public void deleteById(Long aLong) {
        vetRepository.deleteById(aLong);
    }
}
