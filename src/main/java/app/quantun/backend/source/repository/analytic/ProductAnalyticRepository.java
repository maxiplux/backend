package app.quantun.backend.source.repository.analytic;

import app.quantun.backend.models.analytic.entity.ProductAnalytic;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;


/**
 * Repository interface for managing Product entities.
 * This interface provides methods for CRUD operations and custom queries on Product entities.
 */
@Repository
public interface ProductAnalyticRepository extends PagingAndSortingRepository<ProductAnalytic, Long> {


}
