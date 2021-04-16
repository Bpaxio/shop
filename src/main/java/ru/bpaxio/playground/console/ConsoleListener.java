package ru.bpaxio.playground.console;

import java.util.Scanner;

import ru.bpaxio.playground.ShopImpl;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ConsoleListener {
    private static final String STOP_WORD = "exit";

    public void manageShop(ShopImpl shop) {
        shop.open();
        try (Scanner scanner = new Scanner(System.in)) {
            while (true) {
                final String input = scanner.nextLine();
                if (STOP_WORD.equalsIgnoreCase(input)) {
                    shop.close();
                    System.out.println("Good bye! Shop was closed");
                    return;
                }
                try {
                    processInput(shop, input);
                } catch (RuntimeException e) {
                    log.error("Failed: {}", e.getMessage(), e);
                }
            }
        }
    }

    private void processInput(ShopImpl shop, String input) {
        if ("A".equals(input)) {
            System.out.println(shop.goToRegistry());
            return;
        }
        try {
            final int i = Integer.parseInt(input);
            shop.buyerLeft(i);
        } catch (NumberFormatException nfe) {
            log.error("Invalid Input!");
        }
    }
}
