package app.quantun.backend.source.repository.snowflake;

import app.quantun.backend.models.snowflake.entity.ProductSnowFlake;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

/**
 * Repository interface for managing Product entities.
 * This interface provides methods for CRUD operations and custom queries on Product entities.
 */
@Repository
public interface ProductSnowFlakeRepository extends PagingAndSortingRepository<ProductSnowFlake, Long> {


}
