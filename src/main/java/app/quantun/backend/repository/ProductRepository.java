package app.quantun.backend.repository;
import app.quantun.backend.models.entity.Product;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    // Custom query methods
    List<Product> findByNameContaining(String name);
    List<Product> findByPriceLessThan(BigDecimal price);
    List<Product> findByInStock(boolean inStock);
}

