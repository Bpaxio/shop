package ru.bpaxio.playground;

import ru.bpaxio.playground.console.ConsoleListener;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Application {
    public static void main(String[] args) {
        log.info("Shop is started");
        final ShopImpl shop = new ShopImpl();
        new ConsoleListener()
            .manageShop(shop);
    }
}
