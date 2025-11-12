package com.commerce.hhplus_e_commerce.tdd.service;

import com.commerce.hhplus_e_commerce.domain.Product;
import com.commerce.hhplus_e_commerce.domain.enums.ProductStatus;
import com.commerce.hhplus_e_commerce.dto.OrderCreateRequest;
import com.commerce.hhplus_e_commerce.repository.ProductRepository;
import com.commerce.hhplus_e_commerce.service.ProductService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("상품 재고 확인하는 로직")
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    Long productId1 = 1L;
    Long productId2 = 2L;
    LocalDate date1 = LocalDate.now();
    LocalDate date2 = LocalDate.now();


    @Test
    @DisplayName("상품에 대한 validate 확인")
    void validateProduct()  {

        Product product1 = new Product(productId1
                ,"상품1"
                ,10
                ,10_000
                , ProductStatus.SALE
                ,25
                ,date1);

        Product product2 = new Product(productId2
                ,"상품2"
                ,5
                ,15_000
                , ProductStatus.SALE
                ,50
                ,date2);


        when(productRepository.selectByProductId(productId1)).thenReturn(product1);
        when(productRepository.selectByProductId(productId2)).thenReturn(product2);

        List<OrderCreateRequest.Item> items = new ArrayList<>();
        items.add(new OrderCreateRequest.Item(productId1,5));
        items.add(new OrderCreateRequest.Item(productId2,1));

        List<Product> products = productService.validateProducts(items);

        assertThat(products).containsExactly(product1,product2);
    }

    @Test
    @DisplayName("재고가 부족한 경우")
    void stockLake(){
        Product product1 = new Product(productId1
                ,"상품1"
                ,2
                ,10_000
                , ProductStatus.SALE
                ,25
                ,date1);

        when(productRepository.selectByProductId(productId1)).thenReturn(product1);
        List<OrderCreateRequest.Item> items = new ArrayList<>();
        items.add(new OrderCreateRequest.Item(productId1,5));

        assertThatThrownBy(() -> productService.validateProducts(items))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining( "재고 부족: " + product1.getProduct_name() +
                        " (요청: " + items.get(0).quantity() + ", 보유: " + product1.getStock() + ")");

    }

    @Test
    @DisplayName("전체 금액 계산하기")
    void calculateTotalPrice(){
        Product product1 = new Product(productId1
                ,"상품1"
                ,10
                ,10_000
                , ProductStatus.SALE
                ,25
                ,date1);

        Product product2 = new Product(productId2
                ,"상품2"
                ,5
                ,15_000
                , ProductStatus.SALE
                ,50
                ,date2);

        List<OrderCreateRequest.Item> items = List.of(
                 new OrderCreateRequest.Item(productId1,2)
                ,new OrderCreateRequest.Item(productId2,5)
        );
        List<Product> products = List.of(product1,product2);

        int totalAmount = productService.calculateTotalPrice(products, items);

        int expectedTotal = product1.getPrice() * items.get(0).quantity() +  product2.getPrice() * items.get(1).quantity();

        assertThat(totalAmount).isEqualTo(expectedTotal);

    }

}
