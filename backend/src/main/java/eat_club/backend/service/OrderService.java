package eat_club.backend.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import eat_club.backend.dto.common.PagedResponse;
import eat_club.backend.dto.order.CancelOrderRequest;
import eat_club.backend.dto.order.CancelOrderResponse;
import eat_club.backend.dto.order.CreateOrderRequest;
import eat_club.backend.dto.order.OrderItemResponse;
import eat_club.backend.dto.order.OrderResponse;
import eat_club.backend.dto.order.OrderSummaryResponse;
import eat_club.backend.dto.order.UpdateOrderStatusRequest;
import eat_club.backend.entity.Cart;
import eat_club.backend.entity.CartItem;
import eat_club.backend.entity.OrderEntity;
import eat_club.backend.entity.OrderItem;
import eat_club.backend.entity.enums.OrderStatus;
import eat_club.backend.entity.enums.PaymentStatus;
import eat_club.backend.exception.BadRequestException;
import eat_club.backend.exception.ForbiddenException;
import eat_club.backend.exception.ResourceNotFoundException;
import eat_club.backend.mapper.OrderMapper;
import eat_club.backend.repository.OrderItemRepository;
import eat_club.backend.repository.OrderRepository;
import eat_club.backend.util.IdUtils;

@Service
public class OrderService {

    private static final BigDecimal TAX_RATE = new BigDecimal("0.18");
    private static final BigDecimal DEFAULT_DELIVERY_CHARGES = new BigDecimal("50.00");

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final CartService cartService;
    private final UserService userService;
    private final OrderMapper orderMapper;

    public OrderService(
        OrderRepository orderRepository,
        OrderItemRepository orderItemRepository,
        CartService cartService,
        UserService userService,
        OrderMapper orderMapper
    ) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.cartService = cartService;
        this.userService = userService;
        this.orderMapper = orderMapper;
    }

    @Transactional
    public OrderResponse create(CreateOrderRequest request) {
        Cart cart = cartService.getOrCreateCart();
        List<CartItem> cartItems = cartService.getCartItems(cart.getCartId());
        if (cartItems.isEmpty()) {
            throw new BadRequestException("Cart is empty");
        }
        if (cart.getRestaurant() == null) {
            throw new BadRequestException("Cart restaurant is missing");
        }

        long now = System.currentTimeMillis();
        BigDecimal subtotal = cartItems.stream()
            .map(ci -> ci.getItem().getPrice().multiply(BigDecimal.valueOf(ci.getQuantity())))
            .reduce(BigDecimal.ZERO, BigDecimal::add)
            .setScale(2, RoundingMode.HALF_UP);
        BigDecimal tax = subtotal.multiply(TAX_RATE).setScale(2, RoundingMode.HALF_UP);
        BigDecimal delivery = DEFAULT_DELIVERY_CHARGES;
        BigDecimal total = subtotal.add(tax).add(delivery).setScale(2, RoundingMode.HALF_UP);

        OrderEntity order = new OrderEntity();
        order.setOrderId(IdUtils.orderId());
        order.setUser(userService.getCurrentUser());
        order.setRestaurant(cart.getRestaurant());
        order.setDeliveryAddress(request.deliveryAddress());
        order.setPhoneNumber(request.phoneNumber());
        order.setSubtotal(subtotal);
        order.setTax(tax);
        order.setDeliveryCharges(delivery);
        order.setDiscountAmount(BigDecimal.ZERO);
        order.setTotal(total);
        order.setOrderStatus(OrderStatus.CONFIRMED);
        order.setPaymentStatus(PaymentStatus.PENDING);
        order.setPaymentMethod(request.paymentMethod());
        order.setSpecialInstructions(request.specialInstructions());
        order.setEstimatedDeliveryTime(now + (45L * 60L * 1000L));
        order.setCreatedAt(now);
        order.setUpdatedAt(now);
        OrderEntity savedOrder = orderRepository.save(order);

        List<OrderItemResponse> itemResponses = new ArrayList<>();
        for (CartItem cartItem : cartItems) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrderItemId(IdUtils.uuid());
            orderItem.setOrder(savedOrder);
            orderItem.setItem(cartItem.getItem());
            orderItem.setItemName(cartItem.getItem().getItemName());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setUnitPrice(cartItem.getItem().getPrice());
            orderItem.setTotalPrice(cartItem.getItem().getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity())).setScale(2, RoundingMode.HALF_UP));
            orderItem.setSpecialInstructions(cartItem.getSpecialInstructions());
            orderItem.setCreatedAt(now);
            itemResponses.add(orderMapper.toOrderItemResponse(orderItemRepository.save(orderItem)));
        }

        cartService.clearCart(cart);
        return orderMapper.toResponse(savedOrder, itemResponses);
    }

    @Transactional(readOnly = true)
    public OrderResponse getById(String orderId) {
        OrderEntity order = getOrderForCurrentUser(orderId);
        List<OrderItemResponse> items = orderItemRepository.findByOrderOrderId(orderId).stream()
            .map(orderMapper::toOrderItemResponse)
            .toList();
        return orderMapper.toResponse(order, items);
    }

    @Transactional(readOnly = true)
    public PagedResponse<OrderSummaryResponse> getCurrentUserOrders(int page, int size, OrderStatus status) {
        String userId = userService.getCurrentUser().getUserId();
        List<OrderEntity> orders = status == null
            ? orderRepository.findByUserUserIdOrderByCreatedAtDesc(userId)
            : orderRepository.findByUserUserIdAndOrderStatusOrderByCreatedAtDesc(userId, status);
        List<OrderSummaryResponse> mapped = orders.stream().map(orderMapper::toSummary).toList();
        return page(mapped, page, size);
    }

    @Transactional
    public CancelOrderResponse cancel(String orderId, CancelOrderRequest request) {
        OrderEntity order = getOrderForCurrentUser(orderId);
        if (order.getOrderStatus() == OrderStatus.DELIVERED || order.getOrderStatus() == OrderStatus.CANCELLED) {
            throw new BadRequestException("Order cannot be cancelled");
        }

        long now = System.currentTimeMillis();
        order.setOrderStatus(OrderStatus.CANCELLED);
        order.setCancelledAt(now);
        order.setCancellationReason(request.reason());
        order.setUpdatedAt(now);
        orderRepository.save(order);

        return new CancelOrderResponse(order.getOrderId(), order.getOrderStatus(), request.reason(), "INITIATED", now);
    }

    @Transactional
    public OrderResponse updateStatus(String orderId, UpdateOrderStatusRequest request) {
        OrderEntity order = orderRepository.findById(orderId)
            .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
        String currentUserId = userService.getCurrentUser().getUserId();
        if (!order.getRestaurant().getAdmin().getUserId().equals(currentUserId)) {
            throw new ForbiddenException("You don't have permission to access this resource");
        }

        long now = System.currentTimeMillis();
        order.setOrderStatus(request.orderStatus());
        if (request.orderStatus() == OrderStatus.DELIVERED) {
            order.setDeliveredAt(now);
            order.setPaymentStatus(PaymentStatus.COMPLETED);
        }
        order.setUpdatedAt(now);
        OrderEntity saved = orderRepository.save(order);
        List<OrderItemResponse> items = orderItemRepository.findByOrderOrderId(orderId).stream()
            .map(orderMapper::toOrderItemResponse)
            .toList();
        return orderMapper.toResponse(saved, items);
    }

    private OrderEntity getOrderForCurrentUser(String orderId) {
        String userId = userService.getCurrentUser().getUserId();
        OrderEntity order = orderRepository.findById(orderId)
            .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
        if (!order.getUser().getUserId().equals(userId)) {
            throw new ResourceNotFoundException("Order not found");
        }
        return order;
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