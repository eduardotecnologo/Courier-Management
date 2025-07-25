package com.edudeveloper.delivery.Delivery.Trackin.domain.model;

import java.util.Arrays;
import java.util.List;

public enum DeliveryStatus {
    DRAFT,
    WAITING_FOR_COURIER(DRAFT),
    IN_TRANSIT(WAITING_FOR_COURIER),
    DELIVERY(IN_TRANSIT);

    private final List<DeliveryStatus> previousStatus;

    DeliveryStatus(DeliveryStatus... previousStatus) {
        this.previousStatus = Arrays.asList(previousStatus);
    }

    public boolean canNotCahngeTo(DeliveryStatus newStatus) {
        DeliveryStatus current = this;
        return !newStatus.previousStatus.contains(current);
    }

    public boolean canChangeTo(DeliveryStatus newStatus) {
        return !canNotCahngeTo(newStatus);
    }
}
    