package dk.via.pro3.slaughterhouse;

import dk.via.pro3.generated.Animal;
import dk.via.pro3.generated.AnimalsResponse;
import dk.via.pro3.generated.GetAnimalsInProductRequest;
import dk.via.pro3.generated.GetProductsForAnimalRequest;
import dk.via.pro3.generated.Product;
import dk.via.pro3.generated.ProductsResponse;
import dk.via.pro3.generated.TraceabilityServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class TraceabilityGrpcClientTest {

    @Value("${spring.grpc.server.port}")
    private int port;

    private ManagedChannel channel;
    private TraceabilityServiceGrpc.TraceabilityServiceBlockingStub stub;

    @BeforeEach
    void setUp() {
        channel = ManagedChannelBuilder.forAddress("localhost", port)
                .usePlaintext()
                .build();
        stub = TraceabilityServiceGrpc.newBlockingStub(channel);
    }

    @AfterEach
    void tearDown() {
        channel.shutdownNow();
    }

    @Test
    void getAnimalsInProduct() {
        AnimalsResponse response = stub.getAnimalsInProduct(
                GetAnimalsInProductRequest.newBuilder().setProductId("P-5003").build());

        List<String> registrationNumbers = response.getAnimalsList().stream()
                .map(Animal::getRegistrationNumber)
                .toList();
        assertEquals(List.of("A-1001", "A-1002", "A-1003"), registrationNumbers);
    }

    @Test
    void getProductsForAnimal() {
        ProductsResponse response = stub.getProductsForAnimal(
                GetProductsForAnimalRequest.newBuilder().setRegistrationNumber("A-1003").build());

        List<String> productIds = response.getProductsList().stream()
                .map(Product::getId)
                .toList();
        assertEquals(List.of("P-5003"), productIds);
    }

    @Test
    void unknownAnimalIsReturnedAsNotFoundStatus() {
        StatusRuntimeException exception = assertThrows(StatusRuntimeException.class,
                () -> stub.getProductsForAnimal(
                        GetProductsForAnimalRequest.newBuilder().setRegistrationNumber("A-9999").build()));

        assertEquals(Status.Code.NOT_FOUND, exception.getStatus().getCode());
    }
}
