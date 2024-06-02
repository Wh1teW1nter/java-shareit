package ru.practicum.shareit.item;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;
import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

    List<Item> findAllByOwnerId(Long userId, Pageable pageable);

    List<Item> findByNameOrDescriptionContainingIgnoreCaseAndAvailableIsTrue(String name,
                                                                             String description,
                                                                             Pageable pageable);

    List<Item> getByRequestIdIn(Collection<Long> ids);
}
