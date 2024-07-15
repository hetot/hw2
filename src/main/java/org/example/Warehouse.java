package org.example;

import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.example.queue.Queue;
import org.example.queue.NonBlockingQueue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Log4j2
@Getter
public class Warehouse extends Thread {
    private final Queue<Block> storage = new NonBlockingQueue<>();

    public Warehouse(String name) {
        super(name);
    }

    public Warehouse(String name, Collection<Block> initialStorage) {
        this(name);
        initialStorage.forEach(storage::add);
    }

    @Override
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                sleep(100);
            }
        } catch (InterruptedException e) {
            log.info("{} thread interrupted", this.getName());
        }
    }

    public void arrive(Truck truck) {
        if (truck.getBlocks().isEmpty()) {
            loadTruck(truck);
        } else {
            unloadTruck(truck);
        }
    }

    private void loadTruck(Truck truck) {
        log.info("Loading truck {}", truck.getName());
        Collection<Block> blocksToLoad = getFreeBlocks(truck.getCapacity());
        try {
            sleep(10L * blocksToLoad.size());
        } catch (InterruptedException e) {
            log.error("Interrupted while loading truck", e);
        }
        truck.getBlocks().addAll(blocksToLoad);
        log.info("Truck loaded {}", truck.getName());
    }

    private Collection<Block> getFreeBlocks(int maxItems) {
        List<Block> blocks = new ArrayList<>();
        for (int i = 0; i < maxItems; i++) {
            blocks.add(storage.get());
        }
        return blocks;
    }

    private void returnBlocksToStorage(List<Block> returnedBlocks) {
        returnedBlocks.forEach(storage::add);
    }

    private void unloadTruck(Truck truck) {
        log.info("Unloading truck {}", truck.getName());
        List<Block> arrivedBlocks = truck.getBlocks();
        try {
            sleep(100L * arrivedBlocks.size());
        } catch (InterruptedException e) {
            log.error("Interrupted while unloading truck", e);
        }
        returnBlocksToStorage(arrivedBlocks);
        truck.getBlocks().clear();
        log.info("Truck unloaded {}", truck.getName());
    }
}
