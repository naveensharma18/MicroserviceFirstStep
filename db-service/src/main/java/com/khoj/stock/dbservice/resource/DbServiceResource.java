package com.khoj.stock.dbservice.resource;

import com.khoj.stock.dbservice.model.Quote;
import com.khoj.stock.dbservice.model.Quotes;
import com.khoj.stock.dbservice.repository.QuoteRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/rest/db")
public class DbServiceResource {

    private QuoteRepository quoteRepository;

    public DbServiceResource(QuoteRepository quoteRepository) {
        this.quoteRepository = quoteRepository;
    }

    @GetMapping("/{username}")
    public List<String> getQuotes(@PathVariable("username") String username){

        return getQuotesByUserName(username);
    }

    @PostMapping("/add")
    public List<String> add(@RequestBody final Quotes quotes){
        quotes.getQuotes()
                .stream()
                .map(quote-> new Quote(quotes.getUserName(),quote))
                .forEach(quote->quoteRepository.save(quote));
        return getQuotesByUserName(quotes.getUserName());
    }

    @PostMapping("/delete/{username}")
    public List<String> delete(@PathVariable("username") final String userName){
        List<Quote> quotes = quoteRepository.findByUserName(userName);
        quoteRepository.deleteAll(quotes);
        return getQuotesByUserName(userName);
    }


    private List<String> getQuotesByUserName(String userName){
        return quoteRepository.findByUserName(userName)
                .stream()
                .map(Quote::getQuote)
                .collect(Collectors.toList());
    }





}
