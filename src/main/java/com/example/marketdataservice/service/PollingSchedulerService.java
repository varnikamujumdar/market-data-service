package com.example.marketdataservice.service;

import com.example.marketdataservice.models.PollingJobConfig;
import com.example.marketdataservice.repository.PollingJobConfigRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.List;

@Service
@Slf4j
public class PollingSchedulerService {

    @Autowired
    private final PollingJobConfigRepository pollingJobConfigRepository;

    public PollingSchedulerService(PollingJobConfigRepository pollingJobConfigRepository) {
        this.pollingJobConfigRepository = pollingJobConfigRepository;
    }

    @Scheduled(fixedRate = 10000)
    public void scheduledTask() {

        List<PollingJobConfig> jobsWithStatusAsAccepted = pollingJobConfigRepository.findByStatus("accepted");
        for (PollingJobConfig job : jobsWithStatusAsAccepted) {
            if (job.getLastExecutedAt() == null) {
                shouldRun(job);
            } else {
                Duration duration = Duration.between(job.getLastExecutedAt(), ZonedDateTime.now());
                if (duration.getSeconds() >= job.getInterval()) {
                    shouldRun(job);
                }
            }
        }

    }

    public void shouldRun(PollingJobConfig job) {
        log.info("Scheduled in 10sec");
        job.setLastExecutedAt(ZonedDateTime.now());
        job.setStatus("completed");
        pollingJobConfigRepository.save(job);
    }

}
