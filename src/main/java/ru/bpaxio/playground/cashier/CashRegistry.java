package ru.bpaxio.playground.cashier;

public interface CashRegistry {

    boolean addBuyer(String name);

    void open();

    Cashier getCashier();

    int getName();

    int waiterLeft();

    long waitingTimeFrom(long now);

    void close();
}
