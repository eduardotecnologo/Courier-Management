package com.edudeveloper.delivery.Delivery.Trackin.domain.model;

import com.edudeveloper.delivery.Delivery.Trackin.domain.model.exception.DomainException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class DeliveryTest {

    private Delivery delivery;
    private UUID courierId;
    private ContactPoint sender;
    private ContactPoint recipient;

    @BeforeEach
    void setUp() {
        delivery = Delivery.draft();
        courierId = UUID.randomUUID();
        
        sender = ContactPoint.builder()
                .name("João Silva")
                .phone("11999999999")
                .zipCode("01234-567")
                .street("Rua A")
                .number("123")
                .complement("Apto 1")
                .build();
        
        recipient = ContactPoint.builder()
                .name("Maria Santos")
                .phone("11888888888")
                .zipCode("04567-890")
                .street("Rua B")
                .number("456")
                .complement("Casa")
                .build();
    }

    @Test
    void shouldCreateDraftDelivery() {
        // When
        Delivery draft = Delivery.draft();

        // Then
        assertNotNull(draft.getId());
        assertEquals(DeliveryStatus.DRAFT, draft.getStatus());
        assertEquals(0, draft.getTotalItems());
        assertEquals(BigDecimal.ZERO, draft.getTotalCost());
        assertEquals(BigDecimal.ZERO, draft.getCourierPayout());
        assertEquals(BigDecimal.ZERO, draft.getDistanceFee());
        assertTrue(draft.getItems().isEmpty());
    }

    @Test
    void shouldAddItemToDelivery() {
        // When
        UUID itemId = delivery.addItem("Produto Teste", 2);

        // Then
        assertNotNull(itemId);
        assertEquals(1, delivery.getItems().size());
        assertEquals(2, delivery.getTotalItems());
        
        Item item = delivery.getItems().get(0);
        assertEquals("Produto Teste", item.getName());
        assertEquals(2, item.getQuantity());
    }

    @Test
    void shouldRemoveItemFromDelivery() {
        // Given
        UUID itemId = delivery.addItem("Produto Teste", 2);
        delivery.addItem("Outro Produto", 1);

        // When
        delivery.removeItem(itemId);

        // Then
        assertEquals(1, delivery.getItems().size());
        assertEquals(1, delivery.getTotalItems());
        assertFalse(delivery.getItems().stream().anyMatch(item -> item.getId().equals(itemId)));
    }

    @Test
    void shouldChangeItemQuantity() {
        // Given
        UUID itemId = delivery.addItem("Produto Teste", 2);

        // When
        delivery.changeItemQuantity(itemId, 5);

        // Then
        assertEquals(5, delivery.getTotalItems());
        Item item = delivery.getItems().get(0);
        assertEquals(5, item.getQuantity());
    }

    @Test
    void shouldRemoveAllItems() {
        // Given
        delivery.addItem("Produto 1", 2);
        delivery.addItem("Produto 2", 1);

        // When
        delivery.removeItems();

        // Then
        assertTrue(delivery.getItems().isEmpty());
        assertEquals(0, delivery.getTotalItems());
    }

    @Test
    void shouldPlaceDelivery() {
        // Given
        setupDeliveryForPlacement();

        // When
        delivery.place();

        // Then
        assertEquals(DeliveryStatus.WAITING_FOR_COURIER, delivery.getStatus());
        assertNotNull(delivery.getPlacedAt());
    }

    @Test
    void shouldNotPlaceDeliveryWithoutRequiredFields() {
        // When & Then
        assertThrows(DomainException.class, () -> delivery.place());
    }

    @Test
    void shouldNotPlaceDeliveryThatIsNotDraft() {
        // Given
        setupDeliveryForPlacement();
        delivery.place();

        // When & Then
        assertThrows(DomainException.class, () -> delivery.place());
    }

    @Test
    void shouldEditPreparationDetails() {
        // Given
        Delivery.PreparationDetails details = Delivery.PreparationDetails.builder()
                .sender(sender)
                .recipient(recipient)
                .distanceFee(new BigDecimal("10.00"))
                .courierPayout(new BigDecimal("15.00"))
                .expectedDeliveryTime(Duration.ofHours(2))
                .build();

        // When
        delivery.editPreparationDetails(details);

        // Then
        assertEquals(sender, delivery.getSender());
        assertEquals(recipient, delivery.getRecipient());
        assertEquals(new BigDecimal("10.00"), delivery.getDistanceFee());
        assertEquals(new BigDecimal("15.00"), delivery.getCourierPayout());
        assertEquals(new BigDecimal("25.00"), delivery.getTotalCost());
        assertNotNull(delivery.getExpectedDeliveryAt());
    }

    @Test
    void shouldNotEditPreparationDetailsWhenNotDraft() {
        // Given
        setupDeliveryForPlacement();
        delivery.place();

        Delivery.PreparationDetails details = Delivery.PreparationDetails.builder()
                .sender(sender)
                .recipient(recipient)
                .distanceFee(new BigDecimal("10.00"))
                .courierPayout(new BigDecimal("15.00"))
                .expectedDeliveryTime(Duration.ofHours(2))
                .build();

        // When & Then
        assertThrows(DomainException.class, () -> delivery.editPreparationDetails(details));
    }

    @Test
    void shouldNotChangeItemQuantityWhenNotDraft() {
        // Given
        UUID itemId = delivery.addItem("Produto Teste", 2);
        setupDeliveryForPlacement();
        delivery.place();

        // When & Then
        assertThrows(DomainException.class, () -> delivery.changeItemQuantity(itemId, 5));
    }

    @Test
    void shouldPickUpDelivery() {
        // Given
        setupDeliveryForPlacement();
        delivery.place();

        // When
        delivery.pickUp(courierId);

        // Then
        assertEquals(courierId, delivery.getCourierId());
        assertEquals(DeliveryStatus.IN_TRANSIT, delivery.getStatus());
        assertNotNull(delivery.getAssignedAt());
    }

    @Test
    void shouldMarkAsDelivered() {
        // Given
        setupDeliveryForPlacement();
        delivery.place();
        delivery.pickUp(courierId);

        // When
        delivery.markAsDelivery();

        // Then
        assertEquals(DeliveryStatus.DELIVERY, delivery.getStatus());
        assertNotNull(delivery.getFulfilledAt());
    }

    @Test
    void shouldReturnUnmodifiableItemsList() {
        // Given
        delivery.addItem("Produto Teste", 1);

        // When & Then
        List<Item> items = delivery.getItems();
        assertThrows(UnsupportedOperationException.class, () -> {
            // Tentativa de adicionar um item diretamente na lista (que deve falhar)
            items.add(Item.brandNew("Outro Produto", 1));
        });
    }

    @Test
    void shouldCalculateTotalItemsCorrectly() {
        // Given
        delivery.addItem("Produto 1", 2);
        delivery.addItem("Produto 2", 3);
        delivery.addItem("Produto 3", 1);

        // Then
        assertEquals(6, delivery.getTotalItems());
    }

    @Test
    void shouldHandleMultipleItemsCorrectly() {
        // Given
        UUID item1Id = delivery.addItem("Produto 1", 2);
        UUID item2Id = delivery.addItem("Produto 2", 1);

        // When
        delivery.changeItemQuantity(item1Id, 5);
        delivery.removeItem(item2Id);

        // Then
        assertEquals(1, delivery.getItems().size());
        assertEquals(5, delivery.getTotalItems());
        assertEquals("Produto 1", delivery.getItems().get(0).getName());
    }

    @Test
    void shouldChangeStatusToPlaced() {
        // Given
        setupDeliveryForPlacement();
        OffsetDateTime beforePlacement = OffsetDateTime.now();

        // When
        delivery.place();

        // Then
        assertEquals(DeliveryStatus.WAITING_FOR_COURIER, delivery.getStatus());
        assertNotNull(delivery.getPlacedAt());
        assertTrue(delivery.getPlacedAt().isAfter(beforePlacement) || 
                  delivery.getPlacedAt().isEqual(beforePlacement));
        
        // Verificar que outros campos não foram alterados
        assertNotNull(delivery.getId());
        assertEquals(sender, delivery.getSender());
        assertEquals(recipient, delivery.getRecipient());
        assertEquals(new BigDecimal("10.00"), delivery.getDistanceFee());
        assertEquals(new BigDecimal("15.00"), delivery.getCourierPayout());
        assertEquals(new BigDecimal("25.00"), delivery.getTotalCost());
    }

    @Test
    void shouldThrowExceptionWhenChangingQuantityOfNonExistentItem() {
        // Given
        UUID nonExistentItemId = UUID.randomUUID();

        // When & Then
        assertThrows(RuntimeException.class, () -> delivery.changeItemQuantity(nonExistentItemId, 5));
    }

    @Test
    void shouldNotThrowExceptionWhenRemovingNonExistentItem() {
        // Given
        UUID nonExistentItemId = UUID.randomUUID();

        // When & Then
        assertDoesNotThrow(() -> delivery.removeItem(nonExistentItemId));
    }

    @Test
    void shouldThrowExceptionWhenPickupFromDraftStatus() {
        // When & Then
        assertThrows(DomainException.class, () -> delivery.pickUp(courierId));
    }

    @Test
    void shouldThrowExceptionWhenPickupFromDeliveredStatus() {
        // Given
        setupDeliveryForPlacement();
        delivery.place();
        delivery.pickUp(courierId);
        delivery.markAsDelivery();

        // When & Then
        assertThrows(DomainException.class, () -> delivery.pickUp(UUID.randomUUID()));
    }

    @Test
    void shouldThrowExceptionWhenMarkAsDeliveredFromDraftStatus() {
        // When & Then
        assertThrows(DomainException.class, () -> delivery.markAsDelivery());
    }

    @Test
    void shouldThrowExceptionWhenMarkAsDeliveredFromWaitingStatus() {
        // Given
        setupDeliveryForPlacement();
        delivery.place();

        // When & Then
        assertThrows(DomainException.class, () -> delivery.markAsDelivery());
    }

    @Test
    void shouldAddItemInAnyStatus() {
        // Given
        setupDeliveryForPlacement();
        delivery.place();

        // When
        UUID itemId = delivery.addItem("Produto em Trânsito", 1);

        // Then
        assertNotNull(itemId);
        assertEquals(1, delivery.getTotalItems());
    }

    @Test
    void shouldRemoveItemInAnyStatus() {
        // Given
        UUID itemId = delivery.addItem("Produto Teste", 2);
        setupDeliveryForPlacement();
        delivery.place();

        // When
        delivery.removeItem(itemId);

        // Then
        assertEquals(0, delivery.getTotalItems());
        assertTrue(delivery.getItems().isEmpty());
    }

    @Test
    void shouldRemoveAllItemsInAnyStatus() {
        // Given
        delivery.addItem("Produto 1", 2);
        delivery.addItem("Produto 2", 1);
        setupDeliveryForPlacement();
        delivery.place();

        // When
        delivery.removeItems();

        // Then
        assertEquals(0, delivery.getTotalItems());
        assertTrue(delivery.getItems().isEmpty());
    }

    @Test
    void shouldCalculateTotalCostCorrectly() {
        // Given
        Delivery.PreparationDetails details = Delivery.PreparationDetails.builder()
                .sender(sender)
                .recipient(recipient)
                .distanceFee(new BigDecimal("25.50"))
                .courierPayout(new BigDecimal("30.75"))
                .expectedDeliveryTime(Duration.ofHours(3))
                .build();

        // When
        delivery.editPreparationDetails(details);

        // Then
        assertEquals(new BigDecimal("56.25"), delivery.getTotalCost());
    }

    @Test
    void shouldSetExpectedDeliveryAtCorrectly() {
        // Given
        Duration expectedTime = Duration.ofHours(2);
        Delivery.PreparationDetails details = Delivery.PreparationDetails.builder()
                .sender(sender)
                .recipient(recipient)
                .distanceFee(new BigDecimal("10.00"))
                .courierPayout(new BigDecimal("15.00"))
                .expectedDeliveryTime(expectedTime)
                .build();

        OffsetDateTime beforeEdit = OffsetDateTime.now();

        // When
        delivery.editPreparationDetails(details);

        // Then
        assertNotNull(delivery.getExpectedDeliveryAt());
        assertTrue(delivery.getExpectedDeliveryAt().isAfter(beforeEdit));
        assertTrue(delivery.getExpectedDeliveryAt().isBefore(beforeEdit.plus(expectedTime.plus(Duration.ofMinutes(1)))));
    }

    @Test
    void shouldValidateTimestampsOrder() {
        // Given
        setupDeliveryForPlacement();
        OffsetDateTime beforePlace = OffsetDateTime.now();

        // When
        delivery.place();
        OffsetDateTime afterPlace = OffsetDateTime.now();
        delivery.pickUp(courierId);
        OffsetDateTime afterPickup = OffsetDateTime.now();
        delivery.markAsDelivery();

        // Then
        assertTrue(delivery.getPlacedAt().isAfter(beforePlace) || delivery.getPlacedAt().isEqual(beforePlace));
        assertTrue(delivery.getPlacedAt().isBefore(afterPlace) || delivery.getPlacedAt().isEqual(afterPlace));
        assertTrue(delivery.getAssignedAt().isAfter(delivery.getPlacedAt()));
        assertTrue(delivery.getFulfilledAt().isAfter(delivery.getAssignedAt()));
    }

    @Test
    void shouldHandleZeroQuantity() {
        // Given
        UUID itemId = delivery.addItem("Produto Teste", 5);

        // When
        delivery.changeItemQuantity(itemId, 0);

        // Then
        assertEquals(0, delivery.getTotalItems());
        assertEquals(0, delivery.getItems().get(0).getQuantity());
    }

    @Test
    void shouldHandleNegativeQuantity() {
        // Given
        UUID itemId = delivery.addItem("Produto Teste", 5);

        // When
        delivery.changeItemQuantity(itemId, -2);

        // Then
        assertEquals(-2, delivery.getTotalItems());
        assertEquals(-2, delivery.getItems().get(0).getQuantity());
    }

    @Test
    void shouldHandleLargeQuantities() {
        // Given
        UUID itemId = delivery.addItem("Produto Teste", 1);

        // When
        delivery.changeItemQuantity(itemId, 999999);

        // Then
        assertEquals(999999, delivery.getTotalItems());
        assertEquals(999999, delivery.getItems().get(0).getQuantity());
    }

    @Test
    void shouldHandleZeroValuesInPreparationDetails() {
        // Given
        Delivery.PreparationDetails details = Delivery.PreparationDetails.builder()
                .sender(sender)
                .recipient(recipient)
                .distanceFee(BigDecimal.ZERO)
                .courierPayout(BigDecimal.ZERO)
                .expectedDeliveryTime(Duration.ZERO)
                .build();

        // When
        delivery.editPreparationDetails(details);

        // Then
        assertEquals(BigDecimal.ZERO, delivery.getTotalCost());
        assertNotNull(delivery.getExpectedDeliveryAt());
    }

    @Test
    void shouldHandleNullPreparationDetails() {
        // When & Then
        assertThrows(NullPointerException.class, () -> delivery.editPreparationDetails(null));
    }

    @Test
    void shouldHandleEmptyItemName() {
        // When
        UUID itemId = delivery.addItem("", 1);

        // Then
        assertNotNull(itemId);
        assertEquals("", delivery.getItems().get(0).getName());
        assertEquals(1, delivery.getTotalItems());
    }

    @Test
    void shouldHandleNullItemName() {
        // When
        UUID itemId = delivery.addItem(null, 1);

        // Then
        assertNotNull(itemId);
        assertNull(delivery.getItems().get(0).getName());
        assertEquals(1, delivery.getTotalItems());
    }

    @Test
    void shouldMaintainItemOrder() {
        // Given
        UUID item1Id = delivery.addItem("Primeiro", 1);
        UUID item2Id = delivery.addItem("Segundo", 2);
        UUID item3Id = delivery.addItem("Terceiro", 3);

        // When
        List<Item> items = delivery.getItems();

        // Then
        assertEquals(3, items.size());
        assertEquals("Primeiro", items.get(0).getName());
        assertEquals("Segundo", items.get(1).getName());
        assertEquals("Terceiro", items.get(2).getName());
    }

    @Test
    void shouldHandleMultipleItemsWithSameName() {
        // Given
        UUID item1Id = delivery.addItem("Produto", 1);
        UUID item2Id = delivery.addItem("Produto", 2);

        // When
        List<Item> items = delivery.getItems();

        // Then
        assertEquals(2, items.size());
        assertEquals(3, delivery.getTotalItems());
        assertNotEquals(item1Id, item2Id);
    }

    private void setupDeliveryForPlacement() {
        delivery.editPreparationDetails(Delivery.PreparationDetails.builder()
                .sender(sender)
                .recipient(recipient)
                .distanceFee(new BigDecimal("10.00"))
                .courierPayout(new BigDecimal("15.00"))
                .expectedDeliveryTime(Duration.ofHours(2))
                .build());
    }
} 