package com.elecom.greenhouse.service;

import com.elecom.greenhouse.entities.CultureData;
import com.elecom.greenhouse.repositories.CultureDataRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

@Service
@Slf4j
public class RequestSchedulerService {

    private final Map<Long, ScheduledFuture<?>> scheduledTasks = new ConcurrentHashMap<>();
    private final ThreadPoolTaskScheduler scheduler;
    private final CultureDataRepository cultureDataRepository;

    private final RestTemplate template = new RestTemplate();

    public RequestSchedulerService(ThreadPoolTaskScheduler scheduler,
                                   CultureDataRepository cultureDataRepository) {
        this.scheduler = scheduler;
        this.cultureDataRepository = cultureDataRepository;
    }

    // Метод для планирования первого запроса
    public void scheduleFirstRequest(Long entityId, Instant scheduleTime) {
        log.info("Scheduling first request for entityId: {}", entityId);
        Runnable task = () -> {
            sendHttpRequest(entityId); // Отправка первого запроса
            scheduleSecondRequest(entityId); // После отправки планируем второй запрос
        };

        ScheduledFuture<?> future = scheduler.schedule(task,
                scheduleTime);
        scheduledTasks.put(entityId, future);
    }

    // Метод для планирования второго запроса
    public void scheduleSecondRequest(Long entityId) {
        log.info("Scheduling second request for entityId: {}", entityId);
        int workSeconds = getActiveSecondsFromDatabase(entityId);
        int pauseSeconds = getPauseSecondsFromDatabase(entityId);
        Runnable task = () -> {
            sendSecondHttpRequest(entityId);
            scheduleFirstRequest(entityId,
                    Instant.now().plusSeconds(pauseSeconds));
        };
        ScheduledFuture<?> future = scheduler.schedule(task,
                Instant.now().plusSeconds(workSeconds));
        scheduledTasks.put(entityId, future);
    }

    private int getPauseSecondsFromDatabase(Long entityId) {
        CultureData culture = cultureDataRepository.findById(entityId)
                                                   .orElseGet(() -> new CultureData(entityId,
                                                           "",
                                                           0.0,
                                                           0.0,
                                                           0.0,
                                                           Set.of(),
                                                           "",
                                                           0,
                                                           "",
                                                           0,
                                                           0,
                                                           0));
        return culture.getLightExposurePauseSeconds();
    }

    // Методы для отправки HTTP-запросов
    private void sendHttpRequest(Long entityId) {
        log.info("first request executed");
    }

    private void sendSecondHttpRequest(Long entityId) {
        log.info("second request executed");
    }

    // Метод для получения интервала из БД
    private int getActiveSecondsFromDatabase(Long entityId) {
        CultureData culture = cultureDataRepository.findById(entityId)
                                                   .orElseGet(() -> new CultureData(entityId,
                                                           "",
                                                           0.0,
                                                           0.0,
                                                           0.0,
                                                           Set.of(),
                                                           "",
                                                           0,
                                                           "",
                                                           0,
                                                           0,
                                                           0));
        return culture.getLightExposureSeconds();
    }

    @Scheduled(initialDelay = 10)
    public void checkLightning() {
        cultureDataRepository.findAll().forEach(cultureData -> {
            Instant currentTime = Instant.now();
            log.info("Current time: {}. Light exposure seconds: {}. Light exposure pause seconds: {}",
                    currentTime,
                    cultureData.getLightExposureSeconds(),
                    cultureData.getLightExposurePauseSeconds());
            scheduleFirstRequest(cultureData.getId(), currentTime);
        });
    }

    public void cancelTasks(long entityId) {
        scheduledTasks.forEach((id, future) -> {
            if (id.equals(entityId)) {
                log.info("Canceling scheduled tasks for entityId: {}", entityId);
                future.cancel(true);
            }
        });
    }

}
