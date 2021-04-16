package ru.bpaxio.playground.cashier;

import java.time.Instant;
import java.util.Objects;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
public class CashRegistryImpl implements CashRegistry {
    private static final int DEFAULT_SIZE = 20;

    private final int name;
    private final Cashier cashier;

    private final BlockingQueue<String> queue;
    private final ReentrantLock lock;
    private boolean isOpened;

    public CashRegistryImpl(int name, Cashier cashier) {
        this.name = name;
        this.cashier = cashier;
        this.queue = new LinkedBlockingQueue<>(DEFAULT_SIZE);
        this.lock = new ReentrantLock();
    }

    @Override
    public boolean addBuyer(String name) {
        try {
            this.lock.lock();
            final String shopperName = name + '-' + this.name + '-' + queue.size();
            if (queue.remainingCapacity() == 0) {
                log.warn("Waiting time is so much. {} can't wait", shopperName);
                return false;
            }
            try {
                queue.put(shopperName);
                log.info("{} has {} places", this.name, queue.remainingCapacity());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return true;
        } finally {
            this.lock.unlock();
        }
    }

    @Override
    public long waitingTimeFrom(long now) {
        try {
            this.lock.lock();
            return (long) (cashier.getEndTime() + (queue.size() + 1) * cashier.getSpeed());
        } finally {
            this.lock.unlock();
        }
    }

    @Override
    public int waiterLeft() {
        try {
            this.lock.lock();
            final int size = queue.size();
            if (size == 0) {
                throw new RuntimeException("NoBuyersMoreLeftException");
            }
            final String poll = queue.poll();
            log.info("{} left {} registry. left {}", poll, this.getName(), size - 1);
            return this.getName();
        } finally {
            this.lock.unlock();
        }
    }

    @Override
    public void open() {
        log.info("{} registry was opened", this.getName());
        this.isOpened = true;
        run();
        log.info("{} registry was closed", this.getName());
    }

    public void run() {
        try {
            while (this.isOpened) {
                if (cashier.getEndTime() <= Instant.now().toEpochMilli()) {
                    final String poll = queue.poll(5, TimeUnit.SECONDS);
                    if (Objects.nonNull(poll)) {
                        cashier.startServe(poll);
                        log.info("{} became smaller: {}", this.name, queue.size());
                        Thread.sleep(cashier.getEndTime() - Instant.now().plusMillis(200).toEpochMilli());
                    } else {
                        log.info("cashier [{}] says: 'Free cash registryyyy!'", this.name);
                    }
                }
            }
        } catch (InterruptedException e) {
            log.error("Interrupted", e);
        }
    }

    @Override
    public void close() {
        try {
            this.lock.lock();
            this.isOpened = false;
        } finally {
            this.lock.unlock();
        }
    }
}
