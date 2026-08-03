package com.campusverse.service;

import com.campusverse.dto.ItemRequest;
import com.campusverse.dto.ItemResponse;
import com.campusverse.exception.ResourceNotFoundException;
import com.campusverse.model.Item;
import com.campusverse.model.ItemStatus;
import com.campusverse.model.User;
import com.campusverse.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;

    public ItemResponse createItem(ItemRequest request, User finder) {
        Item item = Item.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .category(request.getCategory())
                .location(request.getLocation())
                .contactNumber(request.getContactNumber())
                .eventDate(request.getEventDate())
                .user(finder)
                .status(ItemStatus.OPEN)
                .build();

        itemRepository.save(item);
        return ItemResponse.fromEntity(item);
    }

    @Transactional(readOnly = true)
    public ItemResponse getItem(Long id) {
        Item item = findItemOrThrow(id);
        return ItemResponse.fromEntity(item);
    }

    @Transactional(readOnly = true)
    public List<ItemResponse> search(String keyword, String location, String category, LocalDate eventDate) {
        Specification<Item> spec = builderSearchSpec(keyword, location, category, eventDate);

        return itemRepository.findAll(spec).stream()
                .map(ItemResponse::fromEntity)
                .collect(Collectors.toList());
    }

    private Specification<Item> builderSearchSpec(String keyword, String location, String category, LocalDate eventDate) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if(keyword != null && !keyword.isBlank()) {
                String pattern = "%" + keyword.toLowerCase() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("title")), pattern),
                        cb.like(cb.lower(root.get("description")), pattern)
                ));
            }

            if(location != null && !location.isBlank()) {
                predicates.add(cb.like(cb.lower(root.get("location")), "%" + location.toLowerCase() + "%"));
            }

            if(category != null && !category.isBlank()) {
                predicates.add(cb.equal(cb.lower(root.get("category")), category.toLowerCase()));
            }

            if(eventDate != null) {
                predicates.add(cb.equal(root.get("eventDate"), eventDate));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    protected Item findItemOrThrow(Long id) {
        return itemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Item not found withid : " + id));
    }
}
