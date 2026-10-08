package dk.via.pro3.slaughterhouse;

import dk.via.pro3.generated.Animal;
import dk.via.pro3.generated.AnimalsResponse;
import dk.via.pro3.generated.GetAnimalsInProductRequest;
import dk.via.pro3.generated.GetProductsForAnimalRequest;
import dk.via.pro3.generated.Product;
import dk.via.pro3.generated.ProductsResponse;
import dk.via.pro3.slaughterhouse.service.TraceabilityServiceImpl;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class TraceabilityServiceImplTest {

    @Autowired
    private TraceabilityServiceImpl service;

    // ---------- GetAnimalsInProduct ----------

    @Test
    void packageWithTwoPartsFromSameAnimalReturnsThatAnimalOnce() {
        TestObserver<AnimalsResponse> observer = new TestObserver<>();

        service.getAnimalsInProduct(productRequest("P-5001"), observer);

        assertNull(observer.error);
        assertTrue(observer.completed);
        assertEquals(List.of("A-1001"), registrationNumbers(observer.value));
    }

    @Test
    void halfAnimalReturnsAllAnimalsInvolved() {
        TestObserver<AnimalsResponse> observer = new TestObserver<>();

        service.getAnimalsInProduct(productRequest("P-5003"), observer);

        assertNull(observer.error);
        assertEquals(List.of("A-1001", "A-1002", "A-1003"), registrationNumbers(observer.value));
    }

    @Test
    void animalDataIsReturnedFromDatabase() {
        TestObserver<AnimalsResponse> observer = new TestObserver<>();

        service.getAnimalsInProduct(productRequest("P-5002"), observer);

        Animal animal = observer.value.getAnimals(0);
        assertEquals("A-1002", animal.getRegistrationNumber());
        assertEquals(108.0, animal.getWeight());
        assertEquals("2026-10-01T07:40", animal.getArrivalTime());
    }

    @Test
    void unknownProductGivesNotFound() {
        TestObserver<AnimalsResponse> observer = new TestObserver<>();

        service.getAnimalsInProduct(productRequest("P-9999"), observer);

        assertNull(observer.value);
        assertEquals(Status.Code.NOT_FOUND, Status.fromThrowable(observer.error).getCode());
    }

    @Test
    void emptyProductIdGivesInvalidArgument() {
        TestObserver<AnimalsResponse> observer = new TestObserver<>();

        service.getAnimalsInProduct(productRequest(""), observer);

        assertEquals(Status.Code.INVALID_ARGUMENT, Status.fromThrowable(observer.error).getCode());
    }

    // ---------- GetProductsForAnimal ----------

    @Test
    void animalInSeveralProductsReturnsAllOfThem() {
        TestObserver<ProductsResponse> observer = new TestObserver<>();

        service.getProductsForAnimal(animalRequest("A-1002"), observer);

        assertNull(observer.error);
        assertTrue(observer.completed);
        assertEquals(List.of("P-5002", "P-5003", "P-5004"), productIds(observer.value));
    }

    @Test
    void animalWithTwoPartsInSameProductReturnsThatProductOnce() {
        TestObserver<ProductsResponse> observer = new TestObserver<>();

        service.getProductsForAnimal(animalRequest("A-1001"), observer);

        // A-1001 has two legs in P-5001 and a rib in P-5003 (its loin, PT-04, is not packed yet)
        assertEquals(List.of("P-5001", "P-5003"), productIds(observer.value));
        assertEquals("HALF_ANIMAL", observer.value.getProducts(1).getType());
    }

    @Test
    void animalNotCutYetReturnsEmptyList() {
        TestObserver<ProductsResponse> observer = new TestObserver<>();

        service.getProductsForAnimal(animalRequest("A-1004"), observer);

        assertNull(observer.error);
        assertEquals(0, observer.value.getProductsCount());
    }

    @Test
    void unknownAnimalGivesNotFound() {
        TestObserver<ProductsResponse> observer = new TestObserver<>();

        service.getProductsForAnimal(animalRequest("A-9999"), observer);

        assertNull(observer.value);
        assertEquals(Status.Code.NOT_FOUND, Status.fromThrowable(observer.error).getCode());
    }

    @Test
    void emptyRegistrationNumberGivesInvalidArgument() {
        TestObserver<ProductsResponse> observer = new TestObserver<>();

        service.getProductsForAnimal(animalRequest(" "), observer);

        assertEquals(Status.Code.INVALID_ARGUMENT, Status.fromThrowable(observer.error).getCode());
    }

    // ---------- helpers ----------

    private static GetAnimalsInProductRequest productRequest(String productId) {
        return GetAnimalsInProductRequest.newBuilder().setProductId(productId).build();
    }

    private static GetProductsForAnimalRequest animalRequest(String registrationNumber) {
        return GetProductsForAnimalRequest.newBuilder().setRegistrationNumber(registrationNumber).build();
    }

    private static List<String> registrationNumbers(AnimalsResponse response) {
        return response.getAnimalsList().stream().map(Animal::getRegistrationNumber).toList();
    }

    private static List<String> productIds(ProductsResponse response) {
        return response.getProductsList().stream().map(Product::getId).toList();
    }

    /**
     * Collects what the service sends back to the client.
     */
    private static class TestObserver<T> implements StreamObserver<T> {
        T value;
        Throwable error;
        boolean completed;

        @Override
        public void onNext(T value) {
            this.value = value;
        }

        @Override
        public void onError(Throwable t) {
            this.error = t;
        }

        @Override
        public void onCompleted() {
            this.completed = true;
        }
    }
}
