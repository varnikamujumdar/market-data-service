package com.example.marketdataservice.api;

import com.example.marketdataservice.schemas.PollRequest;
import com.example.marketdataservice.schemas.PollResponse;
import com.example.marketdataservice.schemas.PriceResponse;
import com.example.marketdataservice.service.PriceService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/prices")
public class PriceController {

    @Autowired
    private final PriceService priceService;

    public PriceController(PriceService priceService) {
        this.priceService = priceService;
    }

    @GetMapping("/latest")
    public PriceResponse getLatestPrice(@RequestParam(name = "symbol") String symbol, @RequestParam(name = "provider") String provider) {
        return priceService.getLatestPrice(symbol, provider);
    }

    @PostMapping("/poll")
    public PollResponse pollPrices(@RequestBody(required = true) PollRequest pollRequest) throws JsonProcessingException {
        return priceService.startPolling(pollRequest);
    }

}
