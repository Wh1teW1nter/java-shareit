package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class ItemForRequestDto {

    private Long id;
    private Long requestId;
    private String name;
    private Long ownerId;
    private String description;
    private boolean available;
}