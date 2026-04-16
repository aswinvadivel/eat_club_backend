package eat_club.backend.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import eat_club.backend.dto.cart.CartItemResponse;
import eat_club.backend.dto.cart.CartResponse;
import eat_club.backend.dto.cart.CreateCartItemRequest;
import eat_club.backend.dto.cart.UpdateCartItemRequest;
import eat_club.backend.service.CartService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CartResponse initialize() {
        return cartService.initialize();
    }

    @GetMapping
    public CartResponse getCart() {
        return cartService.getCurrentCart();
    }

    @PostMapping("/items")
    @ResponseStatus(HttpStatus.CREATED)
    public CartItemResponse addItem(@Valid @RequestBody CreateCartItemRequest request) {
        return cartService.addItem(request);
    }

    @PutMapping("/items/{cartItemId}")
    public CartItemResponse updateItem(
        @PathVariable String cartItemId,
        @Valid @RequestBody UpdateCartItemRequest request
    ) {
        return cartService.updateItem(cartItemId, request);
    }

    @DeleteMapping("/items/{cartItemId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeItem(@PathVariable String cartItemId) {
        cartService.removeItem(cartItemId);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void clear() {
        cartService.clear();
    }
}
