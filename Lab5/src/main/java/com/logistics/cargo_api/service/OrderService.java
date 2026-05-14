package com.logistics.cargo_api.service;

import com.logistics.cargo_api.entity.Order;
import com.logistics.cargo_api.exception.EntityNotFoundException;
import com.logistics.cargo_api.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public Order getOrderById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Замовлення з ID " + id + " не знайдено"));
    }

    @Transactional
    public Order advanceOrder(Long id) {
        Order order = getOrderById(id);

        order.getOrderState().next(order);

        return orderRepository.save(order);
    }

    @Transactional
    public Order cancelOrder(Long id) {
        Order order = getOrderById(id);

        order.getOrderState().cancel(order);

        return orderRepository.save(order);
    }

    @Transactional
    public void deleteOrder(Long id) {
        if (!orderRepository.existsById(id)) {
            throw new EntityNotFoundException("Неможливо видалити: замовлення не знайдено");
        }
        orderRepository.deleteById(id);
    }

    @Transactional
    public Order saveOrder(Order order) {
        return orderRepository.save(order);
    }
}