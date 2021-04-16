package ru.bpaxio.playground;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import ru.bpaxio.playground.cashier.CashRegistry;
import ru.bpaxio.playground.cashier.CashRegistryImpl;
import ru.bpaxio.playground.cashier.CashierImpl;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ShopImpl implements Shop {

    private final List<CashRegistry> cashRegisters;
    private final ExecutorService executorService;

    public ShopImpl() {
        this.cashRegisters = new ArrayList<>();
        this.executorService = Executors.newFixedThreadPool(4);
    }

    public void open() {
        //create cashRegs
        this.cashRegisters.add(new CashRegistryImpl(1, new CashierImpl(10)));
        this.cashRegisters.add(new CashRegistryImpl(2, new CashierImpl(13)));
        this.cashRegisters.add(new CashRegistryImpl(3, new CashierImpl(15)));
        this.cashRegisters.add(new CashRegistryImpl(4, new CashierImpl(17)));


        this.cashRegisters.forEach(reg -> executorService.submit(reg::open));
        log.info("Started shop with {} cash registers", cashRegisters.size());
    }

    public int goToRegistry() {
        long now = Instant.now().toEpochMilli();
        final CashRegistry registry = cashRegisters.stream()
            .min(Comparator.comparingLong(cashRegistry -> cashRegistry.waitingTimeFrom(now)))
            .orElseThrow(() -> new RuntimeException("ShopRegistriesAreBrokenException"));
        registry.addBuyer("John");
        return registry.getName();
    }

    public int buyerLeft(int i) {
        return cashRegisters.stream()
            .filter(cashRegistry -> i == cashRegistry.getName())
            .findAny()
            .orElseThrow(() -> new RuntimeException("NoSuchRegistryException"))
            .waiterLeft();
    }

    public void close() {
        log.info("Started Shop closing");
        try {
            cashRegisters.forEach(CashRegistry::close);
            if (!this.executorService.awaitTermination(5, TimeUnit.SECONDS)) {
                List<Runnable> droppedTasks = executorService.shutdownNow();
                log.warn("Executor was abruptly shut down. {} tasks will not be executed.", droppedTasks.size());
            }
        } catch (InterruptedException e) {
            log.error("Shop closing was interrupted", e);
        }
        log.info("Shop is closed now");
    }
}
