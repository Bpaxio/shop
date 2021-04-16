package ru.bpaxio.playground;

public interface Shop {

    void open();
    void close();
    int goToRegistry();

    /**
     * Shopper can't wait.
     * @param name cash registry name.
     * @return cash registry name.
     */
    int buyerLeft(int name);
}
