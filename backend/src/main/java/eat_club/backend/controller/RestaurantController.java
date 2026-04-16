package eat_club.backend.controller;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import eat_club.backend.dto.common.PagedResponse;
import eat_club.backend.dto.restaurant.CreateRestaurantRequest;
import eat_club.backend.dto.restaurant.RestaurantResponse;
import eat_club.backend.dto.restaurant.RestaurantSummaryResponse;
import eat_club.backend.dto.restaurant.UpdateRestaurantRequest;
import eat_club.backend.service.RestaurantService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/restaurants")
public class RestaurantController {

    private final RestaurantService restaurantService;

    public RestaurantController(RestaurantService restaurantService) {
        this.restaurantService = restaurantService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    public RestaurantResponse create(@Valid @RequestBody CreateRestaurantRequest request) {
        return restaurantService.create(request);
    }

    @GetMapping
    public PagedResponse<RestaurantSummaryResponse> getAll(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(required = false) String cuisineType
    ) {
        return restaurantService.getAll(page, size, cuisineType);
    }

    @GetMapping("/{restaurantId}")
    public RestaurantResponse getById(@PathVariable String restaurantId) {
        return restaurantService.getById(restaurantId);
    }

    @PutMapping("/{restaurantId}")
    @PreAuthorize("hasRole('ADMIN')")
    public RestaurantResponse update(
        @PathVariable String restaurantId,
        @RequestBody UpdateRestaurantRequest request
    ) {
        return restaurantService.update(restaurantId, request);
    }

    @DeleteMapping("/{restaurantId}")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String restaurantId) {
        restaurantService.delete(restaurantId);
    }
}
