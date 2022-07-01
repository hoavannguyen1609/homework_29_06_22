package com.example.product_management.controller;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.product_management.entity.Product;
import com.example.product_management.entity.QProduct;
import com.example.product_management.entity.baseresponse.BaseResponse;
import com.example.product_management.repository.ProductRepository;
import com.querydsl.jpa.impl.JPAQuery;

@RestController
@RequestMapping("/api/v1/product")
public class ProductController {

    private JPAQuery<Product> jpaQuery;

    @Autowired
    private ProductRepository productRepository;

    @PersistenceContext
    EntityManager entityManager;

    private QProduct qProduct = QProduct.product;

    private ProductController() {
        this.jpaQuery = new JPAQuery<Product>(entityManager);
    }

    @GetMapping
    public ResponseEntity<BaseResponse> index() {

        return ResponseEntity.status(HttpStatus.OK).body(new BaseResponse(this.jpaQuery.from(this.qProduct).fetch()));
    }

    @GetMapping
    public ResponseEntity<BaseResponse> findById(@RequestParam(required = true, name = "id") long id) {

        Product product = this.jpaQuery.from(this.qProduct).where(this.qProduct.id.eq(id)).fetchFirst();

        if (!(product == null)) {
            return ResponseEntity.status(HttpStatus.OK).body(new BaseResponse(product));
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new BaseResponse("Product not exist"));
    }

    @PostMapping
    public ResponseEntity<BaseResponse> store(@Valid @RequestBody(required = true) Product product,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {

            Map<String, String> errors = new HashMap<>();

            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new BaseResponse(bindingResult.getAllErrors().stream().map((error) -> {
                        errors.put(((FieldError) error).getField(), error.getDefaultMessage());

                        return errors;
                    }), "Validation faild"));
        } else {
            Product product2 = this.jpaQuery.from(this.qProduct).where(this.qProduct.name.eq(product.getName().trim()))
                    .fetchFirst();

            if (!(product2 == null)) {
                return ResponseEntity.status(HttpStatus.OK).body(new BaseResponse(productRepository.save(product)));

                // return ResponseEntity.status(HttpStatus.OK).body(new
                // BaseResponse(this.jpaQuery.from(this.qProduct).set(this.qProduct.name,
                // product.getName())));
            }

            return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(new BaseResponse("Product exist"));
        }
    }

    @PutMapping
    public ResponseEntity<BaseResponse> update(@RequestBody(required = true) Product product) {
        Product product2 = this.jpaQuery.from(this.qProduct).where(this.qProduct.name.eq(product.getName()))
                .fetchFirst();

        if (!(product2 == null)) {

            // Product product3 =
            // this.jpaQuery.from(this.qProduct).where(this.qProduct.id.eq(product.getId()))
            // .fetchOne();

            // return ResponseEntity.status(HttpStatus.OK).body(new
            // BaseResponse(this.jpaQuery.from
            // (this.qProduct).where(this.qProduct.id.eq(product.getId())).(this.qProduct.name,
            // product.getName())));

            return ResponseEntity.status(HttpStatus.OK)
                    .body(new BaseResponse(productRepository.findById(product.getId()).map(item -> {
                        item.setName(product.getName());
                        item.setPrice(product.getPrice());
                        item.setAvatar(product.getAvatar());
                        item.setDescription(product.getDescription());
                        item.setCategoryId(product.getCategoryId());
                        return productRepository.save(item);
                    }).orElseGet(() -> {
                        return productRepository.save(product);
                    })));
        }

        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(new BaseResponse("Product exist"));
    }

    @DeleteMapping
    public ResponseEntity<BaseResponse> destroy(@RequestParam(required = true) long id) {
        Product findProduct = this.jpaQuery.from(this.qProduct).where(this.qProduct.id.eq(id))
                .fetchOne();

        if (!(findProduct == null)) {
            productRepository.deleteById(id);

            return ResponseEntity.status(HttpStatus.OK).body(new BaseResponse("Delete success"));
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new BaseResponse("Product not found"));
    }
}
