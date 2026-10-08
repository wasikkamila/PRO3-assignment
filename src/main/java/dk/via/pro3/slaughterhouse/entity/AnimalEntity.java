package dk.via.pro3.slaughterhouse.entity;

import java.time.LocalDateTime;

public record AnimalEntity(String registrationNumber, double weight, LocalDateTime arrivalTime) {
}
