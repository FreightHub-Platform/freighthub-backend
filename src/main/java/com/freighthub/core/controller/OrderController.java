package com.freighthub.core.controller;

import com.freighthub.core.dto.GetAnyId;
import com.freighthub.core.dto.OrderDto;
import com.freighthub.core.service.OrderService;
import com.freighthub.core.util.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping("/save")
    public ResponseEntity<ApiResponse<?>> createOrder(@RequestBody OrderDto orderDto) {
        try{
            orderService.saveOrder(orderDto);
            ApiResponse<?> response = new ApiResponse<>(HttpStatus.OK.value(), "Order Saved Successfully");
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/all")
    public ResponseEntity<ApiResponse<?>> getAllOrders(){
        try{
            ApiResponse<?> response = new ApiResponse<>(HttpStatus.OK.value(), "Get all orders", orderService.getAllOrders());
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/consigner")
    public ResponseEntity<ApiResponse<?>> getOrdersForConsigner(@RequestBody GetAnyId user){
        try{
            ApiResponse<?> response = new ApiResponse<>(HttpStatus.OK.value(), "Get all orders for consigner", orderService.getOrdersForConsigner(user));
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // view order in order table
    @PostMapping("/view-order")
    public ResponseEntity<ApiResponse<?>> viewOrder(@RequestBody GetAnyId order){
        try{
            ApiResponse<?> response = new ApiResponse<>(HttpStatus.OK.value(), "View order", orderService.viewOrder(order));
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/single")
    public ResponseEntity<ApiResponse<?>> getOrderById(@RequestBody GetAnyId order){
        try{
            ApiResponse<?> response = new ApiResponse<>(HttpStatus.OK.value(), "Get order by id", orderService.getOrderById(order));
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    //cancel order
    @PostMapping("/cancel")
    public ResponseEntity<ApiResponse<?>> cancelOrder(@RequestBody GetAnyId order){
        try{
            orderService.cancelOrder(order);
            ApiResponse<?> response = new ApiResponse<>(HttpStatus.OK.value(), "Order cancelled");
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // get distinct months for consigner orderspickups
    @PostMapping("/months")
    public ResponseEntity<ApiResponse<?>> getDistinctMonths(@RequestBody GetAnyId consigner){
        try{
            ApiResponse<?> response = new ApiResponse<>(HttpStatus.OK.value(), "Get distinct months for consigner orders", orderService.getDistinctMonths(consigner));
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
