package eat_club.backend.controller;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
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
import eat_club.backend.dto.order.CancelOrderRequest;
import eat_club.backend.dto.order.CancelOrderResponse;
import eat_club.backend.dto.order.CreateOrderRequest;
import eat_club.backend.dto.order.OrderResponse;
import eat_club.backend.dto.order.OrderSummaryResponse;
import eat_club.backend.dto.order.UpdateOrderStatusRequest;
import eat_club.backend.entity.enums.OrderStatus;
import eat_club.backend.service.OrderService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrderResponse create(@Valid @RequestBody CreateOrderRequest request) {
        return orderService.create(request);
    }

    @GetMapping("/{orderId}")
    public OrderResponse getById(@PathVariable String orderId) {
        return orderService.getById(orderId);
    }

    @GetMapping
    public PagedResponse<OrderSummaryResponse> getMyOrders(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(required = false) OrderStatus status
    ) {
        return orderService.getCurrentUserOrders(page, size, status);
    }

    @PutMapping("/{orderId}/cancel")
    public CancelOrderResponse cancel(
        @PathVariable String orderId,
        @Valid @RequestBody CancelOrderRequest request
    ) {
        return orderService.cancel(orderId, request);
    }

    @PatchMapping("/{orderId}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public OrderResponse updateStatus(
        @PathVariable String orderId,
        @Valid @RequestBody UpdateOrderStatusRequest request
    ) {
        return orderService.updateStatus(orderId, request);
    }
}
