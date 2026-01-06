package sample.testing.api.service.product;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sample.testing.api.controller.product.dto.request.ProductCreateRequest;
import sample.testing.api.service.product.response.ProductResponse;
import sample.testing.domain.product.Product;
import sample.testing.domain.product.ProductRepository;
import sample.testing.domain.product.ProductSellingStatus;

import java.util.List;

/**
 * readOnly = true : 읽기 전용
 * CRUD에서 CUD 동작 x, only read
 * JPA : CUD 스냅샷 저장, 변경감지 X(성능 향상)
 * CQRS(command query response separate) - command / Query
 *
 */

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductService {

    private final ProductRepository productRepository;

    @Transactional
    public ProductResponse createProduct(ProductCreateRequest productCreateRequest) {
        String nextProductNumber = createNextProductNumber();

        Product product = productCreateRequest.toEntity(nextProductNumber);
        Product savedProduct = productRepository.save(product);

        return ProductResponse.of(savedProduct);
    }

    public List<ProductResponse> getSellingProducts() {
        List<Product> products = productRepository.findAllBySellingStatusIn(ProductSellingStatus.forDisplay());

        return products.stream()
                .map(ProductResponse::of)
                .toList();
    }


    private String createNextProductNumber() {
        String lastestProductNumber = productRepository.findLastestProductNumber();

        if (lastestProductNumber == null) {
            return "001";
        }

        int lastestProductNumberInt = Integer.parseInt(lastestProductNumber);
        int nextProductNumber = lastestProductNumberInt + 1;

        return String.format("%03d", nextProductNumber);
    }
}
