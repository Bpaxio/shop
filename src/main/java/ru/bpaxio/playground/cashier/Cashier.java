package ru.bpaxio.playground.cashier;

public interface Cashier {

    double getSpeed();

    void startServe(String buyer);

    long getEndTime();
}
