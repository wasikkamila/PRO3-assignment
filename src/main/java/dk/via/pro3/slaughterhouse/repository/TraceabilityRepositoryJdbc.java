package dk.via.pro3.slaughterhouse.repository;

import dk.via.pro3.slaughterhouse.entity.AnimalEntity;
import dk.via.pro3.slaughterhouse.entity.ProductEntity;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class TraceabilityRepositoryJdbc implements TraceabilityRepository {

    private final JdbcClient jdbcClient;

    public TraceabilityRepositoryJdbc(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    @Override
    public boolean animalExists(String registrationNumber) {
        Long count = jdbcClient.sql("SELECT COUNT(*) FROM animal WHERE registration_number = :regNo")
                .param("regNo", registrationNumber)
                .query(Long.class)
                .single();
        return count > 0;
    }

    @Override
    public boolean productExists(String productId) {
        Long count = jdbcClient.sql("SELECT COUNT(*) FROM product WHERE id = :productId")
                .param("productId", productId)
                .query(Long.class)
                .single();
        return count > 0;
    }

    @Override
    public List<AnimalEntity> findAnimalsByProductId(String productId) {
        // DISTINCT: one product can contain several parts from the same animal
        String sql = """
                SELECT DISTINCT a.registration_number, a.weight, a.arrival_time
                FROM animal a
                    JOIN animal_part ap ON ap.animal_registration_number = a.registration_number
                    JOIN product_part pp ON pp.part_id = ap.id
                WHERE pp.product_id = :productId
                ORDER BY a.registration_number
                """;

        return jdbcClient.sql(sql)
                .param("productId", productId)
                .query((rs, rowNum) -> new AnimalEntity(
                        rs.getString("registration_number"),
                        rs.getDouble("weight"),
                        rs.getTimestamp("arrival_time").toLocalDateTime()))
                .list();
    }

    @Override
    public List<ProductEntity> findProductsByAnimal(String registrationNumber) {
        // DISTINCT: one animal can have several parts in the same product
        String sql = """
                SELECT DISTINCT p.id, p.type, p.created_at
                FROM product p
                    JOIN product_part pp ON pp.product_id = p.id
                    JOIN animal_part ap ON ap.id = pp.part_id
                WHERE ap.animal_registration_number = :regNo
                ORDER BY p.id
                """;

        return jdbcClient.sql(sql)
                .param("regNo", registrationNumber)
                .query((rs, rowNum) -> new ProductEntity(
                        rs.getString("id"),
                        rs.getString("type"),
                        rs.getTimestamp("created_at").toLocalDateTime()))
                .list();
    }
}
