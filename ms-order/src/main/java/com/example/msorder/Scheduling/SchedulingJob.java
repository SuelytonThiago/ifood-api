package com.example.msorder.Scheduling;

import com.example.msorder.rest.services.BagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Component
public class SchedulingJob {

    @Autowired
    private BagService bagService;

    @Scheduled(fixedDelay = 60000)
    public void cleanBag(){
        Instant date = Instant.now().minus(10, ChronoUnit.HOURS);
        bagService.cleanBagAfter10Hours(date);
    }
}
