package dk.via.pro3.slaughterhouse.service;

import dk.via.pro3.generated.Animal;
import dk.via.pro3.generated.AnimalsResponse;
import dk.via.pro3.generated.GetAnimalsInProductRequest;
import dk.via.pro3.generated.GetProductsForAnimalRequest;
import dk.via.pro3.generated.Product;
import dk.via.pro3.generated.ProductsResponse;
import dk.via.pro3.generated.TraceabilityServiceGrpc;
import dk.via.pro3.slaughterhouse.entity.AnimalEntity;
import dk.via.pro3.slaughterhouse.entity.ProductEntity;
import dk.via.pro3.slaughterhouse.repository.TraceabilityRepository;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TraceabilityServiceImpl extends TraceabilityServiceGrpc.TraceabilityServiceImplBase {

    private final TraceabilityRepository repository;

    public TraceabilityServiceImpl(TraceabilityRepository repository) {
        this.repository = repository;
    }

    @Override
    public void getAnimalsInProduct(GetAnimalsInProductRequest request,
                                    StreamObserver<AnimalsResponse> responseObserver) {
        String productId = request.getProductId();

        if (productId.isBlank()) {
            responseObserver.onError(Status.INVALID_ARGUMENT
                    .withDescription("product_id must not be empty")
                    .asRuntimeException());
            return;
        }
        if (!repository.productExists(productId)) {
            responseObserver.onError(Status.NOT_FOUND
                    .withDescription("No product with id " + productId)
                    .asRuntimeException());
            return;
        }

        List<AnimalEntity> animals = repository.findAnimalsByProductId(productId);

        AnimalsResponse.Builder response = AnimalsResponse.newBuilder();
        for (AnimalEntity animal : animals) {
            response.addAnimals(toProto(animal));
        }

        responseObserver.onNext(response.build());
        responseObserver.onCompleted();
    }

    @Override
    public void getProductsForAnimal(GetProductsForAnimalRequest request,
                                     StreamObserver<ProductsResponse> responseObserver) {
        String registrationNumber = request.getRegistrationNumber();

        if (registrationNumber.isBlank()) {
            responseObserver.onError(Status.INVALID_ARGUMENT
                    .withDescription("registration_number must not be empty")
                    .asRuntimeException());
            return;
        }
        if (!repository.animalExists(registrationNumber)) {
            responseObserver.onError(Status.NOT_FOUND
                    .withDescription("No animal with registration number " + registrationNumber)
                    .asRuntimeException());
            return;
        }

        List<ProductEntity> products = repository.findProductsByAnimal(registrationNumber);

        ProductsResponse.Builder response = ProductsResponse.newBuilder();
        for (ProductEntity product : products) {
            response.addProducts(toProto(product));
        }

        responseObserver.onNext(response.build());
        responseObserver.onCompleted();
    }

    //mapping from database entities to gRPC messages

    private static Animal toProto(AnimalEntity animal) {
        return Animal.newBuilder()
                .setRegistrationNumber(animal.registrationNumber())
                .setWeight(animal.weight())
                .setArrivalTime(animal.arrivalTime().toString())
                .build();
    }

    private static Product toProto(ProductEntity product) {
        return Product.newBuilder()
                .setId(product.id())
                .setType(product.type())
                .setCreatedAt(product.createdAt().toString())
                .build();
    }
}
