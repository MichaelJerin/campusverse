package com.campusverse.dto;

import com.campusverse.model.Item;
import com.campusverse.model.ItemStatus;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class ItemResponse {

    private Long id;
    private String title;
    private String description;
    private String Category;
    private String location;
    private String contactNumber;
    private LocalDate eventDate;
    private ItemStatus status;
    private String finderName;
    private Long finderId;
    private LocalDateTime createdAt;

    public static ItemResponse fromEntity(Item item) {
        ItemResponse res = new ItemResponse();
        res.setId(item.getId());
        res.setTitle(item.getTitle());
        res.setDescription(item.getDescription());
        res.setCategory(item.getCategory());
        res.setLocation(item.getLocation());
        res.setContactNumber(item.getContactNumber());
        res.setEventDate(item.getEventDate());
        res.setStatus(item.getStatus());
        res.setFinderName(item.getUser().getName());
        res.setFinderId(item.getUser().getId());
        res.setCreatedAt(item.getCreatedAt());
        return res;
    }
}
