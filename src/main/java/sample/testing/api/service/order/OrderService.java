package sample.testing.api.service.order;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sample.testing.api.controller.order.request.OrderCreateRequest;
import sample.testing.api.service.order.response.OrderResponse;
import sample.testing.domain.order.Order;
import sample.testing.domain.order.OrderRepository;
import sample.testing.domain.product.Product;
import sample.testing.domain.product.ProductRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    public OrderResponse createOrder(OrderCreateRequest orderCreateRequest, LocalDateTime registeredDateTime) {
        List<String> productNumbers = orderCreateRequest.getProductNumbers();

        // product
        List<Product> products = findProductsBy(productNumbers);


        // order
        Order order = Order.create(products, registeredDateTime);
        Order savedOrder = orderRepository.save(order);
        return OrderResponse.of(savedOrder);
    }

    private List<Product> findProductsBy(List<String> productNumbers) {
        List<Product> products = productRepository.findAllByProductNumberIn(productNumbers);

        Map<String, Product> productMap = products.stream()
                .collect(Collectors.toMap(Product::getProductNumber, product -> product));

        List<Product> duplicateProducts = productNumbers.stream()
                .map(productMap::get)
                .toList();
        return duplicateProducts;
    }
}
