package com.example.bookshopwebapplication.entities;

import lombok.*;

import java.sql.Timestamp;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventoryHistory {
    private Long id;
    private Long productId;
    private Integer quantityChange;
    private Integer previousQuantity;
    private Integer currentQuantity;
    private ActionType actionType;
    private String reason;
    private Long referenceId;
    private String referenceType;
    private String notes;
    private Long createdBy;
    private Timestamp createdAt;

    // Enum cho trường actionType
    @Getter
    public enum ActionType {
        IMPORT("import"),
        EXPORT("export"),
        ADJUSTMENT("adjustment");

        private final String value;

        ActionType(String value) {
            this.value = value;
        }

        public static ActionType fromValue(String value) {
            for (ActionType type : ActionType.values()) {
                if (type.value.equalsIgnoreCase(value)) {
                    return type;
                }
            }
            throw new IllegalArgumentException("Unknown ActionType value: " + value);
        }
    }
}