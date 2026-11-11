package com.geofinder.platform.dto.response;

import com.geofinder.platform.entity.DeliveryZone;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryZoneResponse {
    
    private String id;

    private String placeId;

    private Integer minOrderValue;

    private Integer deliveryFee;

    public static DeliveryZoneResponse from(DeliveryZone deliveryZone) {
        return new DeliveryZoneResponse(deliveryZone.getId(), deliveryZone.getPlaceId(),
                deliveryZone.getMinOrderValue(), deliveryZone.getDeliveryFee());
    }
}
