package org.example.splitbooks.services;

import org.example.splitbooks.dto.request.QuoteRegisterRequest;
import org.example.splitbooks.dto.response.QuoteRegisterResponse;
import org.example.splitbooks.dto.response.QuoteSetResponse;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface QuoteService {
    public QuoteRegisterResponse registerQuote(QuoteRegisterRequest quoteRegisterRequest);
    public List<QuoteSetResponse> getSwipeCards(int batchSize);
    public void swipe(Long targetProfileId, boolean liked);
    public List<QuoteSetResponse> getIncomingSwipes();
}
