package com.example.product_management.repository;

// import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.example.product_management.entity.Category;

@Repository
public interface CategoryRepository extends
        // JpaRepository<Category, Long>,
        QuerydslPredicateExecutor<Category>,
        CrudRepository<Category, Long> {
}
