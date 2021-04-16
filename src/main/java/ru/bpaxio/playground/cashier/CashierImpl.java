package ru.bpaxio.playground.cashier;

import java.time.Instant;
import java.util.concurrent.TimeUnit;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
@RequiredArgsConstructor
public class CashierImpl implements Cashier {
    static final long DEFAULT_TIME_PERIOD = TimeUnit.MINUTES.toMillis(5);

    private final int countForTimePeriod;
    private long endTime;

    @Override
    public void startServe(String buyer) {
        this.endTime = Instant.now().toEpochMilli() + ((long) getSpeed());
        log.info("Started serve {} and will end on {}", buyer, this.endTime);
    }

    public double getSpeed() {
        return DEFAULT_TIME_PERIOD / countForTimePeriod;
    }
}
