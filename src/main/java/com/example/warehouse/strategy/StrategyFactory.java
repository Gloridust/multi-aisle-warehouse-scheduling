package com.example.warehouse.strategy;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class StrategyFactory {
    private final Map<String, AllocationStrategy> strategies = new HashMap<>();

    public StrategyFactory() {
        register(new CategoryStrategy());
        register(new NearestStrategy());
        register(new GeneticStrategy());
    }

    private void register(AllocationStrategy strategy) {
        strategies.put(strategy.getType(), strategy);
    }

    public AllocationStrategy get(String type) {
        AllocationStrategy strategy = strategies.get(type);
        if (strategy == null) {
            throw new IllegalArgumentException("Unsupported strategy: " + type);
        }
        return strategy;
    }
}
