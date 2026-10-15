package com.cafeshop.coffeeshop;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@SpringBootApplication
public class CoffeeshopApplication {
    public static void main(String[] args) {
        SpringApplication.run(CoffeeshopApplication.class, args);
    }
}

@Entity
@Table(name = "coffees")
class Coffee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private Double price;
    private String origin;
    private Integer stock;

    public Coffee() {}

    public Coffee(String name, Double price, String origin, Integer stock) {
        this.name = name;
        this.price = price;
        this.origin = origin;
        this.stock = stock;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }
    public String getOrigin() { return origin; }
    public void setOrigin(String origin) { this.origin = origin; }
    public Integer getStock() { return stock; }
    public void setStock(Integer stock) { this.stock = stock; }
}

record CoffeeDTO(String name, Double price, String origin, Integer stock) {}

interface CoffeeRepository extends JpaRepository<Coffee, Long> {
}

@RestController
@RequestMapping("/api/coffees")
class CoffeeController {
    private final CoffeeRepository coffeeRepository;

    public CoffeeController(CoffeeRepository coffeeRepository) {
        this.coffeeRepository = coffeeRepository;
    }

    // 1. Obtener todos los cafés (GET)
    @GetMapping
    public List<Coffee> getAllCoffees() {
        return coffeeRepository.findAll();
    }

    // 2. Buscar un café por ID (GET)
    @GetMapping("/{id}")
    public ResponseEntity<Coffee> getCoffeeById(@PathVariable Long id) {
        return coffeeRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 3. Crear un nuevo café (POST)
    @PostMapping
    public ResponseEntity<Coffee> createCoffee(@RequestBody CoffeeDTO dto) {
        Coffee coffee = new Coffee(dto.name(), dto.price(), dto.origin(), dto.stock());
        Coffee saved = coffeeRepository.save(coffee);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    // 4. Actualizar un café existente (PUT)
    @PutMapping("/{id}")
    public ResponseEntity<Coffee> updateCoffee(@PathVariable Long id, @RequestBody CoffeeDTO dto) {
        return coffeeRepository.findById(id)
                .map(existingCoffee -> {
                    existingCoffee.setName(dto.name());
                    existingCoffee.setPrice(dto.price());
                    existingCoffee.setOrigin(dto.origin());
                    existingCoffee.setStock(dto.stock());
                    Coffee updated = coffeeRepository.save(existingCoffee);
                    return ResponseEntity.ok(updated);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // 5. Eliminar un café (DELETE)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCoffee(@PathVariable Long id) {
        if (coffeeRepository.existsById(id)) {
            coffeeRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}