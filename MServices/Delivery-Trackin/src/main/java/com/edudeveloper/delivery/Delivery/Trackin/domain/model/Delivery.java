package com.edudeveloper.delivery.Delivery.Trackin.domain.model;

import lombok.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@NoArgsConstructor(access = AccessLevel.PACKAGE)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Setter(AccessLevel.PRIVATE)
public class Delivery {

    @EqualsAndHashCode.Include
    private UUID id;

    private UUID courierId;

    // ENUM Delivery STATUS
    private DeliveryStatus status;

    private OffsetDateTime placedAt;
    private OffsetDateTime assignedAt;
    private OffsetDateTime expectedDeliveryAt;
    private OffsetDateTime fulfilledAt;

    private BigDecimal distanceFee;
    private BigDecimal courierPayout;
    private BigDecimal totalCost;

    private Integer totalItems;

    // import ContactPoint
    private ContactPoint sender;
    private ContactPoint recipient;

    // Import Item
    private List<Item> items = new ArrayList<>();

    // Static Factory - Iniciando com um "rascunho" vazio
    public static Delivery draft(){
        Delivery delivery = new Delivery();
        delivery.setId(UUID.randomUUID());
        delivery.setStatus(DeliveryStatus.DRAFT);
        delivery.setTotalItems(0);
        delivery.setTotalCost(BigDecimal.ZERO);
        delivery.setCourierPayout(BigDecimal.ZERO);
        delivery.setDistanceFee(BigDecimal.ZERO);

        return delivery;
    }

    // Gett Customer
    public List<Item> getItems() {
        return Collections.unmodifiableList(this.items);
        }
    }
