package eat_club.backend.controller;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import eat_club.backend.dto.common.PagedResponse;
import eat_club.backend.dto.menu.AvailabilityResponse;
import eat_club.backend.dto.menu.CreateMenuItemRequest;
import eat_club.backend.dto.menu.MenuItemResponse;
import eat_club.backend.dto.menu.UpdateAvailabilityRequest;
import eat_club.backend.dto.menu.UpdateMenuItemRequest;
import eat_club.backend.service.MenuService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/restaurants/{restaurantId}/menu-items")
public class MenuController {

    private final MenuService menuService;

    public MenuController(MenuService menuService) {
        this.menuService = menuService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    public MenuItemResponse create(
        @PathVariable String restaurantId,
        @Valid @RequestBody CreateMenuItemRequest request
    ) {
        return menuService.create(restaurantId, request);
    }

    @GetMapping
    public PagedResponse<MenuItemResponse> getByRestaurant(
        @PathVariable String restaurantId,
        @RequestParam(required = false) String category,
        @RequestParam(required = false) Boolean isVegetarian,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size
    ) {
        return menuService.getByRestaurant(restaurantId, category, isVegetarian, page, size);
    }

    @PutMapping("/{itemId}")
    @PreAuthorize("hasRole('ADMIN')")
    public MenuItemResponse update(
        @PathVariable String restaurantId,
        @PathVariable String itemId,
        @RequestBody UpdateMenuItemRequest request
    ) {
        return menuService.update(restaurantId, itemId, request);
    }

    @DeleteMapping("/{itemId}")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
        @PathVariable String restaurantId,
        @PathVariable String itemId
    ) {
        menuService.delete(restaurantId, itemId);
    }

    @PatchMapping("/{itemId}/availability")
    @PreAuthorize("hasRole('ADMIN')")
    public AvailabilityResponse updateAvailability(
        @PathVariable String restaurantId,
        @PathVariable String itemId,
        @Valid @RequestBody UpdateAvailabilityRequest request
    ) {
        return menuService.updateAvailability(restaurantId, itemId, request);
    }
}
