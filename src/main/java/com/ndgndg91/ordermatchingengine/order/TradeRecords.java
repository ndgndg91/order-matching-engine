package com.ndgndg91.ordermatchingengine.order;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

@Slf4j
public class TradeRecords {
    private final Symbol symbol;
    private final Queue<MatchResult> records = new ConcurrentLinkedQueue<>();

    public TradeRecords(Symbol symbol) {
        this.symbol = symbol;
    }

    public void addMatchResult(MatchResult matchResult) {
        records.add(matchResult);
    }

    public List<MatchResult> findAll() {
        return new ArrayList<>(records);
    }
}
