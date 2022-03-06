package com.ndgndg91.ordermatchingengine.order.service;

import com.ndgndg91.ordermatchingengine.order.MatchResult;
import com.ndgndg91.ordermatchingengine.order.TradeRecords;
import com.ndgndg91.ordermatchingengine.order.Symbol;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class MatchedOrders {

    private final Map<Symbol, TradeRecords> m;

    public MatchedOrders() {
        this.m = new ConcurrentHashMap<>();
        for (var s : Symbol.values()) {
            m.put(s, new TradeRecords(s));
        }
    }

    public void addMatchedOrder(MatchResult matchResult) {
        TradeRecords tradeRecord = m.get(matchResult.getSymbol());
        if (tradeRecord == null) {
            log.error("{}", matchResult);
            log.error("trade record not found");
            return;
        }

        tradeRecord.addMatchResult(matchResult);
    }

    public List<MatchResult> findAll(Symbol symbol) {
        return m.get(symbol).findAll();
    }
}
