package com.denka88.ateliergrace.impl;

import com.denka88.ateliergrace.model.Order;
import com.denka88.ateliergrace.model.Status;
import com.denka88.ateliergrace.repo.OrderRepo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock
    private OrderRepo orderRepo;

    @InjectMocks
    private OrderServiceImpl orderService;

    @Test
    void findAll() {
        List<Order> orders = List.of(new Order());
        when(orderRepo.findAllWithEmployeesAndMaterials()).thenReturn(orders);
        assertEquals(orders, orderService.findAll());
    }

    @Test
    void findById() {
        Order order = new Order();
        order.setId(1L);
        when(orderRepo.findById(1L)).thenReturn(Optional.of(order));
        assertEquals(order, orderService.findById(1L).orElse(null));
    }

    @Test
    void save() {
        Order order = new Order();
        Order saved = new Order();
        saved.setId(1L);

        when(orderRepo.save(any())).thenReturn(saved);

        Order result = orderService.save(order);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertNotNull(order.getOrderDate());
    }

    @Test
    void delete() {
        orderService.delete(1L);
        verify(orderRepo).deleteById(1L);
    }

    @Test
    void update() {
        Order order = new Order();
        orderService.update(order);
        verify(orderRepo).save(order);
    }

    @Test
    void findByClientId() {
        List<Order> list = List.of(new Order());
        when(orderRepo.findByClientId(1L)).thenReturn(list);
        assertEquals(list, orderService.findByClientId(1L));
    }

    @Test
    void completeOrder() {
        Order order = new Order();
        order.setId(1L);

        when(orderRepo.findById(1L)).thenReturn(Optional.of(order));

        orderService.completeOrder(1L);

        assertEquals(Status.COMPLETED, order.getStatus());
        verify(orderRepo).save(order);
    }
}