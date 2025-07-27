package com.edudeveloper.delivery.Delivery.Trackin.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

// Value Object - Sem modificações
@EqualsAndHashCode
@AllArgsConstructor
@Builder
@Getter
public class ContactPoint {// Constructor
    private String zipCode;
    private String street;
    private String number;
    private String complement;
    private String name;
    private String phone;
}
