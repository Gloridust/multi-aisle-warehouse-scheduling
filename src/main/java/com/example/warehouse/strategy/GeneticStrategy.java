package com.example.warehouse.strategy;

import com.example.warehouse.model.AllocationResult;
import com.example.warehouse.model.StorageLocation;
import com.example.warehouse.model.Warehouse;
import com.example.warehouse.service.OrderItemDetail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class GeneticStrategy implements AllocationStrategy {
    private final Random random = new Random();

    @Override
    public String getType() {
        return "GENETIC";
    }

    @Override
    public List<AllocationResult> allocate(Warehouse warehouse, List<OrderItemDetail> items, List<StorageLocation> availableLocations) {
        List<Pallet> pallets = buildPallets(warehouse, items);
        int k = pallets.size();
        if (k > availableLocations.size()) {
            throw new IllegalStateException("Not enough locations");
        }
        int populationSize = 50;
        int generations = 100;
        double crossoverRate = 0.8;
        double mutationRate = 0.1;

        List<int[]> population = new ArrayList<>();
        for (int i = 0; i < populationSize; i++) {
            population.add(randomChromosome(availableLocations.size(), k));
        }

        int[] best = population.get(0);
        double bestFitness = fitness(best, availableLocations, warehouse);
        for (int gen = 0; gen < generations; gen++) {
            List<int[]> newPopulation = new ArrayList<>();
            while (newPopulation.size() < populationSize) {
                int[] parent1 = tournamentSelect(population, availableLocations, warehouse);
                int[] parent2 = tournamentSelect(population, availableLocations, warehouse);
                int[] child1 = Arrays.copyOf(parent1, parent1.length);
                int[] child2 = Arrays.copyOf(parent2, parent2.length);
                if (random.nextDouble() < crossoverRate) {
                    child1 = oxCrossover(parent1, parent2);
                    child2 = oxCrossover(parent2, parent1);
                }
                if (random.nextDouble() < mutationRate) {
                    mutate(child1);
                }
                if (random.nextDouble() < mutationRate) {
                    mutate(child2);
                }
                newPopulation.add(child1);
                if (newPopulation.size() < populationSize) {
                    newPopulation.add(child2);
                }
            }
            population = newPopulation;
            for (int[] chromosome : population) {
                double currentFitness = fitness(chromosome, availableLocations, warehouse);
                if (currentFitness > bestFitness) {
                    bestFitness = currentFitness;
                    best = chromosome;
                }
            }
        }

        List<AllocationResult> results = new ArrayList<>();
        for (int i = 0; i < k; i++) {
            StorageLocation location = availableLocations.get(best[i]);
            Pallet pallet = pallets.get(i);
            AllocationResult result = new AllocationResult();
            result.setLocationId(location.getId());
            result.setSkuId(pallet.skuId);
            result.setAllocatedQty(pallet.quantity);
            result.setAllocatedVolume(pallet.quantity * pallet.unitVolume);
            result.setAccessDistance(AccessTimeCalculator.calcAccessTime(location, warehouse));
            results.add(result);
        }
        return results;
    }

    private List<Pallet> buildPallets(Warehouse warehouse, List<OrderItemDetail> items) {
        List<Pallet> pallets = new ArrayList<>();
        for (OrderItemDetail item : items) {
            int capacity = resolveCapacity(warehouse, item);
            int remaining = item.getQuantity();
            while (remaining > 0) {
                int qty = Math.min(remaining, capacity);
                pallets.add(new Pallet(item.getSkuId(), qty, item.getSku().getUnitVolume()));
                remaining -= qty;
            }
        }
        return pallets;
    }

    private int resolveCapacity(Warehouse warehouse, OrderItemDetail item) {
        double palletVolume = warehouse.getPalletVolume() == null ? 0 : warehouse.getPalletVolume();
        double unitVolume = item.getSku().getUnitVolume() == null ? 0 : item.getSku().getUnitVolume();
        if (palletVolume <= 0) {
            palletVolume = 1;
        }
        if (unitVolume <= 0) {
            unitVolume = 1;
        }
        return Math.max(1, (int) Math.floor(palletVolume / unitVolume));
    }

    private int[] randomChromosome(int availableSize, int k) {
        List<Integer> indices = new ArrayList<>();
        for (int i = 0; i < availableSize; i++) {
            indices.add(i);
        }
        int[] chromosome = new int[k];
        for (int i = 0; i < k; i++) {
            int pick = random.nextInt(indices.size());
            chromosome[i] = indices.remove(pick);
        }
        return chromosome;
    }

    private int[] tournamentSelect(List<int[]> population, List<StorageLocation> locations, Warehouse warehouse) {
        int[] best = null;
        double bestFitness = -1;
        for (int i = 0; i < 3; i++) {
            int[] candidate = population.get(random.nextInt(population.size()));
            double candidateFitness = fitness(candidate, locations, warehouse);
            if (candidateFitness > bestFitness) {
                bestFitness = candidateFitness;
                best = candidate;
            }
        }
        return best;
    }

    private double fitness(int[] chromosome, List<StorageLocation> locations, Warehouse warehouse) {
        double totalDistance = 0;
        for (int gene : chromosome) {
            totalDistance += AccessTimeCalculator.calcAccessTime(locations.get(gene), warehouse);
        }
        return 1.0 / (1.0 + totalDistance);
    }

    private int[] oxCrossover(int[] parent1, int[] parent2) {
        int len = parent1.length;
        int start = random.nextInt(len);
        int end = random.nextInt(len);
        if (start > end) {
            int temp = start;
            start = end;
            end = temp;
        }
        int[] child = new int[len];
        Arrays.fill(child, -1);
        for (int i = start; i <= end; i++) {
            child[i] = parent1[i];
        }
        int current = (end + 1) % len;
        for (int i = 0; i < len; i++) {
            int gene = parent2[(end + 1 + i) % len];
            if (!contains(child, gene)) {
                child[current] = gene;
                current = (current + 1) % len;
            }
        }
        return child;
    }

    private void mutate(int[] chromosome) {
        int i = random.nextInt(chromosome.length);
        int j = random.nextInt(chromosome.length);
        int temp = chromosome[i];
        chromosome[i] = chromosome[j];
        chromosome[j] = temp;
    }

    private boolean contains(int[] array, int value) {
        for (int v : array) {
            if (v == value) {
                return true;
            }
        }
        return false;
    }

    private static class Pallet {
        private final Long skuId;
        private final int quantity;
        private final double unitVolume;

        private Pallet(Long skuId, int quantity, double unitVolume) {
            this.skuId = skuId;
            this.quantity = quantity;
            this.unitVolume = unitVolume;
        }
    }
}
