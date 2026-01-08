package sample.testing.api.service.order;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import sample.testing.api.client.mail.MailSendClient;
import sample.testing.domain.history.mail.MailSendHistory;
import sample.testing.domain.history.mail.MailSendHistoryRepository;
import sample.testing.domain.order.Order;
import sample.testing.domain.order.OrderRepository;
import sample.testing.domain.order.OrderStatus;
import sample.testing.domain.orderproduct.OrderProductRepository;
import sample.testing.domain.product.Product;
import sample.testing.domain.product.ProductRepository;
import sample.testing.domain.product.ProductSellingStatus;
import sample.testing.domain.product.ProductType;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static sample.testing.domain.product.ProductSellingStatus.SELLING;
import static sample.testing.domain.product.ProductType.HANDMADE;

@SpringBootTest
@ActiveProfiles("test")
class OrderStatisticsServiceTest {

    @Autowired
    private OrderStatisticsService orderStatisticsService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private MailSendHistoryRepository mailSendHistoryRepository;

    @Autowired
    private OrderProductRepository orderProductRepository;

    @MockitoBean
    private MailSendClient mailSendClient;

    @AfterEach
    void tearDown() {
        orderProductRepository.deleteAll();
        orderRepository.deleteAllInBatch();
        productRepository.deleteAllInBatch();
        mailSendHistoryRepository.deleteAllInBatch();
    }

    @Test
    @DisplayName("결제완료 주문들을 조회하여 매출 통계 메일을 전송한다.")
    void sendOrderStatisticsMail() {
        // given
        LocalDateTime now = LocalDateTime.of(2023, 3, 5, 0, 0);

        Product product1 = createProduct("001", "아메리카노", 1000, SELLING, HANDMADE);
        Product product2 = createProduct("002", "아메리카노", 2000, SELLING, HANDMADE);
        Product product3 = createProduct("003", "아메리카노", 3000, SELLING, HANDMADE);

        List<Product> products = List.of(product1, product2, product3);
        productRepository.saveAll(products);

        createPaymentCompletedOrder(products, LocalDateTime.of(2023, 3, 4, 23, 59, 59));
        createPaymentCompletedOrder(products, now);
        createPaymentCompletedOrder(products, LocalDateTime.of(2023, 3, 5, 23, 59, 59));
        createPaymentCompletedOrder(products, LocalDateTime.of(2023, 3, 6, 0, 0));


        // stubbing
        Mockito.when(mailSendClient.sendEmail(any(String.class), any(String.class), any(String.class), any(String.class)))
                .thenReturn(true);


        // when
        boolean result = orderStatisticsService.sendOrderStatisticsMail(LocalDate.of(2023, 3, 5), "test@test.com");

        // then
        assertThat(result).isTrue();

        List<MailSendHistory> histories = mailSendHistoryRepository.findAll();
        assertThat(histories).hasSize(1)
                .extracting("content")
                .contains("총 매출 합계는 12000원 입니다.");

    }

    private Order createPaymentCompletedOrder(List<Product> products, LocalDateTime now) {
        Order order = Order.builder()
                .products(products)
                .orderStatus(OrderStatus.PAYMENT_COMPLETED)
                .registeredDateTime(now)
                .build();

        return orderRepository.save(order);
    }

    private Product createProduct(String productNumber, String name, int price, ProductSellingStatus productSellingStatus, ProductType productType) {
        return Product.builder()
                .productNumber(productNumber)
                .type(productType)
                .sellingStatus(productSellingStatus)
                .name(name)
                .price(price)
                .build();
    }

}