package com.edudeveloper.delivery.Delivery.Trackin.domain.model;

import com.edudeveloper.delivery.Delivery.Trackin.domain.model.exception.DomainException;
import lombok.*;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@NoArgsConstructor(access = AccessLevel.PACKAGE)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Setter(AccessLevel.PRIVATE)
@Getter
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

    // Apenas o Delivery pode modificar a lista de Items
    public UUID addItem(String name, int quantity)
    {
        Item item = Item.brandNew(name, quantity);
        items.add(item);
        calculateTotalItems();
        return item.getId();
    }

    public void removeItem(UUID itemId)
    {
        items.removeIf(item -> item.getId().equals(itemId));
        calculateTotalItems();
    }

    public void changeItemQuantity(UUID itemId, int quantity)
    {
        verifyIfCanBeEdited();
        Item item = getItems().stream().filter(i -> i.getId().equals(itemId)).findFirst().orElseThrow();
        item.setQuantity(quantity);
        calculateTotalItems();
    }

    public void removeItems()
    {
        items.clear();
        calculateTotalItems();
    }

    //Métodos que representam itensões
    public void place()
    {
        verifyIfCanBePlaced();
        this.changeStatusTo(DeliveryStatus.WAITING_FOR_COURIER);
        this.setPlacedAt(OffsetDateTime.now());
    }

    public void pickUp(UUID courierId)
    {
        this.setCourierId(courierId);
        this.changeStatusTo(DeliveryStatus.IN_TRANSIT);
        this.setAssignedAt(OffsetDateTime.now());
    }

    public void markAsDelivery()
    {
        this.changeStatusTo(DeliveryStatus.DELIVERY);
        this.setFulfilledAt(OffsetDateTime.now());
    }

    //
    public void editPreparationDetails(PreparationDetails details)
    {
        verifyIfCanBeEdited();
        setSender(details.getSender());
        setRecipient(details.getRecipient());
        setDistanceFee(details.getDistanceFee());
        setCourierPayout(details.getCourierPayout());
        //
        setExpectedDeliveryAt(OffsetDateTime.now().plus(details.getExpectedDeliveryTime()));
        setTotalCost(this.getDistanceFee().add(this.getCourierPayout()));
    }

    // Gett Customer
    public List<Item> getItems()
    {
        return Collections.unmodifiableList(this.items);
    }

    private void calculateTotalItems()
    {
        int totalItems = getItems().stream().mapToInt(Item::getQuantity).sum();
        setTotalItems(totalItems);
    }

    // Verifica se o estado de uma delivery pode ser alterada para Placed
    private void verifyIfCanBePlaced()
    {
        if(!isFilled())
        {
            throw  new DomainException();
        }
        if(!getStatus().equals(DeliveryStatus.DRAFT))
        {
            throw new DomainException();
        }
    }

    private void verifyIfCanBeEdited()
    {
        if(!getStatus().equals(DeliveryStatus.DRAFT))
        {
            throw new DomainException("Não é possível editar uma entrega que não está em rascunho. Status atual: " + getStatus());
        }
    }

    private boolean isFilled()
    {
        return this.getId() != null
                && this.getRecipient() != null
                && this.getTotalCost() != null;
    }

    private void changeStatusTo(DeliveryStatus newStatus)
    {
        if(newStatus != null && this.getStatus().canNotCahngeTo(newStatus))
        {
            throw new DomainException("Não é possível alterar o status da entrega de " 
            + this.getStatus() + " para " + newStatus + " ! ");
        }
        this.setStatus(newStatus);
    }

    // Inner Class
    @Getter
    @AllArgsConstructor
    @Builder
    public static class PreparationDetails {
        private ContactPoint sender;
        private ContactPoint recipient;
        private BigDecimal distanceFee;
        private BigDecimal courierPayout;
        private Duration expectedDeliveryTime;
    }
}
