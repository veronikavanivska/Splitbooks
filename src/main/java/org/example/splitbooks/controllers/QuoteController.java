package org.example.splitbooks.controllers;

import org.example.splitbooks.dto.request.QuoteRegisterRequest;
import org.example.splitbooks.dto.request.SwipeRequest;
import org.example.splitbooks.dto.response.QuoteRegisterResponse;
import org.example.splitbooks.dto.response.QuoteSetResponse;
import org.example.splitbooks.services.impl.QuoteServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/game")
public class QuoteController {


    private final QuoteServiceImpl quoteService;

    public QuoteController(QuoteServiceImpl quoteService) {
        this.quoteService = quoteService;
    }

    @PostMapping("/register")
    public ResponseEntity<QuoteRegisterResponse> registerQuote(@RequestBody QuoteRegisterRequest quoteRegisterRequest) {
        QuoteRegisterResponse response = quoteService.registerQuote(quoteRegisterRequest);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/swipe")
    public ResponseEntity<List<QuoteSetResponse>> getSwipes( @RequestParam int batchSize){
        List<QuoteSetResponse> response = quoteService.getSwipeCards(batchSize);
        return ResponseEntity.ok(response);
    }
//
//    @GetMapping("/load-more")
//    public List<QuoteSetResponse> loadMoreSwipeCards(
//            @RequestParam int batchSize,
//            @RequestParam int offset
//    ) {
//        return quoteService.loadMoreCards(batchSize, offset);
//    }

    @PostMapping("/swipe")
    public ResponseEntity<Void> swipe(@RequestBody SwipeRequest request) {

        quoteService.swipe(request.getTargetProfileId(), request.isLiked());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/incoming-swipes")
    public ResponseEntity<List<QuoteSetResponse>> getIncomingSwipes() {

        List<QuoteSetResponse> incomingCards = quoteService.getIncomingSwipes();
        return ResponseEntity.ok(incomingCards);
    }

}
