package com.example.product_management.controller;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.validation.Valid;

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

import com.example.product_management.entity.Category;
import com.example.product_management.entity.QCategory;
import com.example.product_management.entity.baseresponse.BaseResponse;
import com.querydsl.jpa.impl.JPAQueryFactory;

@RestController
@RequestMapping("/api/v1/category")
public class CategoryController {

    private JPAQueryFactory jpaQuery;

    @PersistenceContext
    EntityManager entityManager;

    private QCategory qCategory = QCategory.category;

    public CategoryController() {
        this.jpaQuery = new JPAQueryFactory(entityManager);
    }

    @GetMapping
    public ResponseEntity<BaseResponse> index() {
        return ResponseEntity.status(HttpStatus.OK)
                .body(new BaseResponse(this.jpaQuery.selectFrom(this.qCategory).fetch()));
    }

    @GetMapping
    public ResponseEntity<BaseResponse> findById(@RequestParam(required = true, name = "id") long id) {

        Category category = this.jpaQuery.selectFrom(this.qCategory).where(this.qCategory.id.eq(id)).fetchFirst();

        if (!(category == null)) {
            return ResponseEntity.status(HttpStatus.OK).body(new BaseResponse(category));
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new BaseResponse("Category not exist"));
    }

    @PostMapping
    public ResponseEntity<BaseResponse> store(@Valid @RequestBody(required = true) Category newCategory,
            BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {

            Map<String, String> errors = new HashMap<>();

            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new BaseResponse(bindingResult.getAllErrors().stream().map((error) -> {
                        errors.put(((FieldError) error).getField(), error.getDefaultMessage());

                        return errors;
                    }), "Validation faild"));
        } else {

            Category category = this.jpaQuery.selectFrom(this.qCategory)
                    .where(this.qCategory.name.eq(newCategory.getName().trim()))
                    .fetchFirst();

            if (!(category == null)) {
                return ResponseEntity.status(HttpStatus.OK)
                        .body(new BaseResponse(
                                this.jpaQuery.insert(this.qCategory).columns(this.qCategory.name, this.qCategory.avatar)
                                        .values(newCategory.getName(), newCategory.getAvatar()).execute()));
            }

            return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(new BaseResponse("Product exist"));
        }
    }

    @PutMapping
    public ResponseEntity<BaseResponse> update(@RequestBody(required = true) Category newCategory) {
        Category category = this.jpaQuery.selectFrom(this.qCategory)
                .where(this.qCategory.name.eq(newCategory.getName()))
                .fetchFirst();

        if (!(category == null)) {

            Category findCategoryById = this.jpaQuery.selectFrom(this.qCategory)
                    .where(this.qCategory.id.eq(newCategory.getId())).fetchOne();

            if (findCategoryById != null) {
                return ResponseEntity.status(HttpStatus.OK)
                        .body(new BaseResponse(
                                // categoryRepository.findById(newCategory.getId()).map(item -> {
                                // item.setName(newCategory.getName());
                                // item.setAvatar(newCategory.getAvatar());
                                // return categoryRepository.save(item);
                                // }).orElseGet(() -> {
                                // return categoryRepository.save(newCategory);
                                // })
                                this.jpaQuery.update(this.qCategory).where(this.qCategory.id.eq(newCategory.getId()))
                                        .set(this.qCategory.name, newCategory.getName())
                                        .set(this.qCategory.avatar, newCategory.getAvatar())
                                        .execute()));
            }

            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new BaseResponse("Category not found"));
        }

        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(new BaseResponse("Product exist"));
    }

    @DeleteMapping
    public ResponseEntity<BaseResponse> destroy(@RequestParam(required = true) long id) {
        Category findCategory = this.jpaQuery.selectFrom(this.qCategory).where(this.qCategory.id.eq(id))
                .fetchOne();

        if (findCategory != null) {
            return ResponseEntity.status(HttpStatus.OK).body(new BaseResponse(
                    this.jpaQuery.delete(this.qCategory).where(this.qCategory.id.eq(id)).execute(), "Delete success"));
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new BaseResponse("Category not found"));
    }
}
