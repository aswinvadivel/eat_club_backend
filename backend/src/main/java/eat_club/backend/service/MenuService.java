package eat_club.backend.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import eat_club.backend.dto.common.PagedResponse;
import eat_club.backend.dto.menu.AvailabilityResponse;
import eat_club.backend.dto.menu.CreateMenuItemRequest;
import eat_club.backend.dto.menu.MenuItemResponse;
import eat_club.backend.dto.menu.UpdateAvailabilityRequest;
import eat_club.backend.dto.menu.UpdateMenuItemRequest;
import eat_club.backend.entity.MenuItem;
import eat_club.backend.entity.Restaurant;
import eat_club.backend.exception.ForbiddenException;
import eat_club.backend.exception.ResourceNotFoundException;
import eat_club.backend.mapper.MenuItemMapper;
import eat_club.backend.repository.MenuItemRepository;
import eat_club.backend.util.IdUtils;

@Service
public class MenuService {

    private final MenuItemRepository menuItemRepository;
    private final RestaurantService restaurantService;
    private final UserService userService;
    private final MenuItemMapper menuItemMapper;

    public MenuService(
        MenuItemRepository menuItemRepository,
        RestaurantService restaurantService,
        UserService userService,
        MenuItemMapper menuItemMapper
    ) {
        this.menuItemRepository = menuItemRepository;
        this.restaurantService = restaurantService;
        this.userService = userService;
        this.menuItemMapper = menuItemMapper;
    }

    @Transactional
    public MenuItemResponse create(String restaurantId, CreateMenuItemRequest request) {
        Restaurant restaurant = restaurantService.getRestaurant(restaurantId);
        validateOwner(restaurant);

        long now = System.currentTimeMillis();
        MenuItem item = new MenuItem();
        item.setItemId(IdUtils.uuid());
        item.setRestaurant(restaurant);
        item.setItemName(request.itemName());
        item.setDescription(request.description());
        item.setPrice(request.price());
        item.setCategory(request.category());
        item.setIsVegetarian(Boolean.TRUE.equals(request.isVegetarian()));
        item.setIsSpicy(Boolean.TRUE.equals(request.isSpicy()));
        item.setPreparationTime(request.preparationTime() == null ? 20 : request.preparationTime());
        item.setImageUrl(request.imageUrl());
        item.setIsAvailable(request.isAvailable() == null ? true : request.isAvailable());
        item.setCreatedAt(now);
        item.setUpdatedAt(now);

        return menuItemMapper.toResponse(menuItemRepository.save(item));
    }

    @Transactional(readOnly = true)
    public PagedResponse<MenuItemResponse> getByRestaurant(String restaurantId, String category, Boolean isVegetarian, int page, int size) {
        List<MenuItem> items;
        if (category != null && !category.isBlank()) {
            items = menuItemRepository.findByRestaurantRestaurantIdAndCategoryIgnoreCase(restaurantId, category);
        } else if (isVegetarian != null) {
            items = menuItemRepository.findByRestaurantRestaurantIdAndIsVegetarian(restaurantId, isVegetarian);
        } else {
            items = menuItemRepository.findByRestaurantRestaurantId(restaurantId);
        }

        List<MenuItemResponse> mapped = items.stream().map(menuItemMapper::toResponse).toList();
        return page(mapped, page, size);
    }

    @Transactional
    public MenuItemResponse update(String restaurantId, String itemId, UpdateMenuItemRequest request) {
        MenuItem item = menuItemRepository.findByItemIdAndRestaurantRestaurantId(itemId, restaurantId)
            .orElseThrow(() -> new ResourceNotFoundException("Menu item not found"));
        validateOwner(item.getRestaurant());

        if (request.itemName() != null) {
            item.setItemName(request.itemName());
        }
        if (request.description() != null) {
            item.setDescription(request.description());
        }
        if (request.price() != null) {
            item.setPrice(request.price());
        }
        if (request.category() != null) {
            item.setCategory(request.category());
        }
        if (request.isVegetarian() != null) {
            item.setIsVegetarian(request.isVegetarian());
        }
        if (request.isSpicy() != null) {
            item.setIsSpicy(request.isSpicy());
        }
        if (request.preparationTime() != null) {
            item.setPreparationTime(request.preparationTime());
        }
        if (request.imageUrl() != null) {
            item.setImageUrl(request.imageUrl());
        }
        if (request.isAvailable() != null) {
            item.setIsAvailable(request.isAvailable());
        }
        item.setUpdatedAt(System.currentTimeMillis());

        return menuItemMapper.toResponse(menuItemRepository.save(item));
    }

    @Transactional
    public void delete(String restaurantId, String itemId) {
        MenuItem item = menuItemRepository.findByItemIdAndRestaurantRestaurantId(itemId, restaurantId)
            .orElseThrow(() -> new ResourceNotFoundException("Menu item not found"));
        validateOwner(item.getRestaurant());
        menuItemRepository.delete(item);
    }

    @Transactional
    public AvailabilityResponse updateAvailability(String restaurantId, String itemId, UpdateAvailabilityRequest request) {
        MenuItem item = menuItemRepository.findByItemIdAndRestaurantRestaurantId(itemId, restaurantId)
            .orElseThrow(() -> new ResourceNotFoundException("Menu item not found"));
        validateOwner(item.getRestaurant());

        item.setIsAvailable(request.isAvailable());
        item.setUpdatedAt(System.currentTimeMillis());
        menuItemRepository.save(item);

        return new AvailabilityResponse(item.getItemId(), item.getIsAvailable(), item.getUpdatedAt());
    }

    public MenuItem getItem(String itemId) {
        return menuItemRepository.findById(itemId)
            .orElseThrow(() -> new ResourceNotFoundException("Menu item not found"));
    }

    private void validateOwner(Restaurant restaurant) {
        String userId = userService.getCurrentUser().getUserId();
        if (!restaurant.getAdmin().getUserId().equals(userId)) {
            throw new ForbiddenException("You don't have permission to access this resource");
        }
    }

    private <T> PagedResponse<T> page(List<T> values, int page, int size) {
        int safePage = Math.max(page, 0);
        int safeSize = size <= 0 ? 20 : size;
        int start = safePage * safeSize;
        if (start >= values.size()) {
            return new PagedResponse<>(List.of(), values.size(), (int) Math.ceil(values.size() / (double) safeSize), safePage, safeSize);
        }
        int end = Math.min(start + safeSize, values.size());
        List<T> content = new ArrayList<>(values.subList(start, end));
        return new PagedResponse<>(content, values.size(), (int) Math.ceil(values.size() / (double) safeSize), safePage, safeSize);
    }
}
