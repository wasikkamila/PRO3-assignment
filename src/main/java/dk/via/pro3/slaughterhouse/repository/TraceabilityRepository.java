package dk.via.pro3.slaughterhouse.repository;

import dk.via.pro3.slaughterhouse.entity.AnimalEntity;
import dk.via.pro3.slaughterhouse.entity.ProductEntity;

import java.util.List;

//Read access to the Central Traceability Store
public interface TraceabilityRepository {

    boolean animalExists(String registrationNumber);

    boolean productExists(String productId);

    List<AnimalEntity> findAnimalsByProductId(String productId);

    List<ProductEntity> findProductsByAnimal(String registrationNumber);
}
