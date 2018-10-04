package com.khoj.stock.dbservice.repository;

import com.khoj.stock.dbservice.model.Quote;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuoteRepository extends JpaRepository<Quote,Integer>
{
    public List<Quote> findByUserName(String username);
}
