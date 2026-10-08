package dk.via.pro3.slaughterhouse.entity;

import java.time.LocalDateTime;

public record ProductEntity(String id, String type, LocalDateTime createdAt) {
}
