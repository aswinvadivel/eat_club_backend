package eat_club.backend.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import eat_club.backend.dto.common.PagedResponse;
import eat_club.backend.dto.menu.MenuItemResponse;
import eat_club.backend.dto.restaurant.CreateRestaurantRequest;
import eat_club.backend.dto.restaurant.RestaurantResponse;
import eat_club.backend.dto.restaurant.RestaurantSummaryResponse;
import eat_club.backend.dto.restaurant.UpdateRestaurantRequest;
import eat_club.backend.entity.Restaurant;
import eat_club.backend.entity.User;
import eat_club.backend.exception.ForbiddenException;
import eat_club.backend.exception.ResourceNotFoundException;
import eat_club.backend.mapper.MenuItemMapper;
import eat_club.backend.mapper.RestaurantMapper;
import eat_club.backend.repository.MenuItemRepository;
import eat_club.backend.repository.RestaurantRepository;
import eat_club.backend.util.IdUtils;

@Service
public class RestaurantService {

    private final RestaurantRepository restaurantRepository;
    private final MenuItemRepository menuItemRepository;
    private final UserService userService;
    private final RestaurantMapper restaurantMapper;
    private final MenuItemMapper menuItemMapper;

    public RestaurantService(
        RestaurantRepository restaurantRepository,
        MenuItemRepository menuItemRepository,
        UserService userService,
        RestaurantMapper restaurantMapper,
        MenuItemMapper menuItemMapper
    ) {
        this.restaurantRepository = restaurantRepository;
        this.menuItemRepository = menuItemRepository;
        this.userService = userService;
        this.restaurantMapper = restaurantMapper;
        this.menuItemMapper = menuItemMapper;
    }

    @Transactional
    public RestaurantResponse create(CreateRestaurantRequest request) {
        User admin = userService.getCurrentUser();
        long now = System.currentTimeMillis();

        Restaurant restaurant = new Restaurant();
        restaurant.setRestaurantId(IdUtils.uuid());
        restaurant.setAdmin(admin);
        restaurant.setRestaurantName(request.restaurantName());
        restaurant.setDescription(request.description());
        restaurant.setAddress(request.address());
        restaurant.setPhoneNumber(request.phoneNumber());
        restaurant.setCuisineType(request.cuisineType());
        restaurant.setIsActive(request.isActive() == null ? true : request.isActive());
        restaurant.setCreatedAt(now);
        restaurant.setUpdatedAt(now);

        return restaurantMapper.toResponse(restaurantRepository.save(restaurant), List.of());
    }

    @Transactional(readOnly = true)
    public PagedResponse<RestaurantSummaryResponse> getAll(int page, int size, String cuisineType) {
        List<Restaurant> restaurants = cuisineType == null || cuisineType.isBlank()
            ? restaurantRepository.findAll()
            : restaurantRepository.findByCuisineTypeIgnoreCase(cuisineType);

        List<RestaurantSummaryResponse> mapped = restaurants.stream().map(restaurantMapper::toSummaryResponse).toList();
        return page(mapped, page, size);
    }

    @Transactional(readOnly = true)
    public RestaurantResponse getById(String restaurantId) {
        Restaurant restaurant = getRestaurant(restaurantId);
        List<MenuItemResponse> menuItems = menuItemRepository.findByRestaurantRestaurantId(restaurantId).stream()
            .map(menuItemMapper::toResponse)
            .toList();
        return restaurantMapper.toResponse(restaurant, menuItems);
    }

    @Transactional
    public RestaurantResponse update(String restaurantId, UpdateRestaurantRequest request) {
        Restaurant restaurant = getRestaurant(restaurantId);
        validateOwner(restaurant);

        if (request.restaurantName() != null) {
            restaurant.setRestaurantName(request.restaurantName());
        }
        if (request.description() != null) {
            restaurant.setDescription(request.description());
        }
        if (request.address() != null) {
            restaurant.setAddress(request.address());
        }
        if (request.phoneNumber() != null) {
            restaurant.setPhoneNumber(request.phoneNumber());
        }
        if (request.cuisineType() != null) {
            restaurant.setCuisineType(request.cuisineType());
        }
        if (request.isActive() != null) {
            restaurant.setIsActive(request.isActive());
        }
        restaurant.setUpdatedAt(System.currentTimeMillis());

        Restaurant saved = restaurantRepository.save(restaurant);
        List<MenuItemResponse> menuItems = menuItemRepository.findByRestaurantRestaurantId(restaurantId).stream()
            .map(menuItemMapper::toResponse)
            .toList();
        return restaurantMapper.toResponse(saved, menuItems);
    }

    @Transactional
    public void delete(String restaurantId) {
        Restaurant restaurant = getRestaurant(restaurantId);
        validateOwner(restaurant);
        restaurantRepository.delete(restaurant);
    }

    public Restaurant getRestaurant(String restaurantId) {
        return restaurantRepository.findById(restaurantId)
            .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found"));
    }

    private void validateOwner(Restaurant restaurant) {
        String userId = userService.getCurrentUser().getUserId();
        if (!restaurant.getAdmin().getUserId().equals(userId)) {
            throw new ForbiddenException("You don't have permission to access this resource");
        }
    }

    private <T> PagedResponse<T> page(List<T> values, int page, int size) {
        int safePage = Math.max(page, 0);
        int safeSize = size <= 0 ? 10 : size;
        int start = safePage * safeSize;
        if (start >= values.size()) {
            return new PagedResponse<>(List.of(), values.size(), (int) Math.ceil(values.size() / (double) safeSize), safePage, safeSize);
        }
        int end = Math.min(start + safeSize, values.size());
        List<T> content = new ArrayList<>(values.subList(start, end));
        return new PagedResponse<>(content, values.size(), (int) Math.ceil(values.size() / (double) safeSize), safePage, safeSize);
    }
}
