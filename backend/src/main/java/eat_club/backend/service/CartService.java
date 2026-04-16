package eat_club.backend.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import eat_club.backend.dto.cart.CartItemResponse;
import eat_club.backend.dto.cart.CartResponse;
import eat_club.backend.dto.cart.CreateCartItemRequest;
import eat_club.backend.dto.cart.UpdateCartItemRequest;
import eat_club.backend.entity.Cart;
import eat_club.backend.entity.CartItem;
import eat_club.backend.entity.MenuItem;
import eat_club.backend.entity.Restaurant;
import eat_club.backend.entity.User;
import eat_club.backend.exception.BadRequestException;
import eat_club.backend.exception.ResourceNotFoundException;
import eat_club.backend.repository.CartItemRepository;
import eat_club.backend.repository.CartRepository;
import eat_club.backend.util.IdUtils;

@Service
public class CartService {

    private static final BigDecimal TAX_RATE = new BigDecimal("0.18");
    private static final BigDecimal DEFAULT_DELIVERY_CHARGES = new BigDecimal("50.00");

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final UserService userService;
    private final RestaurantService restaurantService;
    private final MenuService menuService;

    public CartService(
        CartRepository cartRepository,
        CartItemRepository cartItemRepository,
        UserService userService,
        RestaurantService restaurantService,
        MenuService menuService
    ) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.userService = userService;
        this.restaurantService = restaurantService;
        this.menuService = menuService;
    }

    @Transactional
    public CartResponse initialize() {
        return toResponse(getOrCreateCart());
    }

    @Transactional(readOnly = true)
    public CartResponse getCurrentCart() {
        return toResponse(getOrCreateCart());
    }

    @Transactional
    public CartItemResponse addItem(CreateCartItemRequest request) {
        Cart cart = getOrCreateCart();
        MenuItem item = menuService.getItem(request.itemId());
        if (!item.getRestaurant().getRestaurantId().equals(request.restaurantId())) {
            throw new BadRequestException("Menu item does not belong to provided restaurant");
        }
        if (!Boolean.TRUE.equals(item.getIsAvailable())) {
            throw new BadRequestException("Menu item is not available");
        }

        if (cart.getRestaurant() == null) {
            Restaurant restaurant = restaurantService.getRestaurant(request.restaurantId());
            cart.setRestaurant(restaurant);
        } else if (!cart.getRestaurant().getRestaurantId().equals(request.restaurantId())) {
            throw new BadRequestException("Cart already contains items from another restaurant");
        }

        CartItem cartItem = cartItemRepository.findByCartCartIdAndItemItemId(cart.getCartId(), request.itemId()).orElse(null);
        long now = System.currentTimeMillis();
        if (cartItem == null) {
            cartItem = new CartItem();
            cartItem.setCartItemId(IdUtils.uuid());
            cartItem.setCart(cart);
            cartItem.setItem(item);
            cartItem.setQuantity(request.quantity());
            cartItem.setSpecialInstructions(request.specialInstructions());
            cartItem.setAddedAt(now);
        } else {
            cartItem.setQuantity(cartItem.getQuantity() + request.quantity());
            cartItem.setSpecialInstructions(request.specialInstructions());
        }
        cart.setUpdatedAt(now);
        cartRepository.save(cart);
        CartItem saved = cartItemRepository.save(cartItem);
        return toCartItemResponse(saved);
    }

    @Transactional
    public CartItemResponse updateItem(String cartItemId, UpdateCartItemRequest request) {
        Cart cart = getOrCreateCart();
        CartItem item = cartItemRepository.findById(cartItemId)
            .orElseThrow(() -> new ResourceNotFoundException("Cart item not found"));
        if (!item.getCart().getCartId().equals(cart.getCartId())) {
            throw new ResourceNotFoundException("Cart item not found");
        }

        item.setQuantity(request.quantity());
        item.setSpecialInstructions(request.specialInstructions());
        cart.setUpdatedAt(System.currentTimeMillis());
        cartRepository.save(cart);
        return toCartItemResponse(cartItemRepository.save(item));
    }

    @Transactional
    public void removeItem(String cartItemId) {
        Cart cart = getOrCreateCart();
        CartItem item = cartItemRepository.findById(cartItemId)
            .orElseThrow(() -> new ResourceNotFoundException("Cart item not found"));
        if (!item.getCart().getCartId().equals(cart.getCartId())) {
            throw new ResourceNotFoundException("Cart item not found");
        }
        cartItemRepository.delete(item);

        if (cartItemRepository.findByCartCartId(cart.getCartId()).isEmpty()) {
            cart.setRestaurant(null);
        }
        cart.setUpdatedAt(System.currentTimeMillis());
        cartRepository.save(cart);
    }

    @Transactional
    public void clear() {
        Cart cart = getOrCreateCart();
        cartItemRepository.deleteByCartCartId(cart.getCartId());
        cart.setRestaurant(null);
        cart.setUpdatedAt(System.currentTimeMillis());
        cartRepository.save(cart);
    }

    public Cart getOrCreateCart() {
        User user = userService.getCurrentUser();
        return cartRepository.findByUserUserId(user.getUserId()).orElseGet(() -> {
            long now = System.currentTimeMillis();
            Cart cart = new Cart();
            cart.setCartId(IdUtils.uuid());
            cart.setUser(user);
            cart.setRestaurant(null);
            cart.setCreatedAt(now);
            cart.setUpdatedAt(now);
            return cartRepository.save(cart);
        });
    }

    public List<CartItem> getCartItems(String cartId) {
        return cartItemRepository.findByCartCartId(cartId);
    }

    public void clearCart(Cart cart) {
        cartItemRepository.deleteByCartCartId(cart.getCartId());
        cart.setRestaurant(null);
        cart.setUpdatedAt(System.currentTimeMillis());
        cartRepository.save(cart);
    }

    public CartResponse toResponse(Cart cart) {
        List<CartItem> cartItems = cartItemRepository.findByCartCartId(cart.getCartId());
        List<CartItemResponse> itemResponses = cartItems.stream().map(this::toCartItemResponse).toList();

        BigDecimal subtotal = itemResponses.stream()
            .map(CartItemResponse::totalPrice)
            .reduce(BigDecimal.ZERO, BigDecimal::add)
            .setScale(2, RoundingMode.HALF_UP);
        BigDecimal tax = subtotal.multiply(TAX_RATE).setScale(2, RoundingMode.HALF_UP);
        BigDecimal delivery = subtotal.compareTo(BigDecimal.ZERO) > 0 ? DEFAULT_DELIVERY_CHARGES : BigDecimal.ZERO;
        BigDecimal total = subtotal.add(tax).add(delivery).setScale(2, RoundingMode.HALF_UP);

        return new CartResponse(
            cart.getCartId(),
            cart.getUser().getUserId(),
            cart.getRestaurant() == null ? null : cart.getRestaurant().getRestaurantId(),
            itemResponses,
            subtotal,
            tax,
            delivery,
            total,
            cart.getUpdatedAt()
        );
    }

    private CartItemResponse toCartItemResponse(CartItem item) {
        BigDecimal totalPrice = item.getItem().getPrice()
            .multiply(BigDecimal.valueOf(item.getQuantity()))
            .setScale(2, RoundingMode.HALF_UP);
        return new CartItemResponse(
            item.getCartItemId(),
            item.getItem().getItemId(),
            item.getItem().getItemName(),
            item.getItem().getRestaurant().getRestaurantId(),
            item.getQuantity(),
            item.getItem().getPrice(),
            totalPrice,
            item.getSpecialInstructions(),
            item.getAddedAt()
        );
    }
}
