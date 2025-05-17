package com.elecom.greenhouse.service;

import com.elecom.greenhouse.entities.CultureData;
import com.elecom.greenhouse.repositories.CultureDataRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
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
    public void scheduleFirstRequest(Long entityId, LocalDateTime scheduleTime) {
        log.info("Scheduling first request for entityId: {}", entityId);
        Runnable task = () -> {
            sendHttpRequest(entityId); // Отправка первого запроса
            scheduleSecondRequest(entityId); // После отправки планируем второй запрос
        };

        ScheduledFuture<?> future = scheduler.schedule(task,
                scheduleTime.toInstant(ZoneOffset.UTC));
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
                    LocalDateTime.now().plusSeconds(pauseSeconds));
        };
        ScheduledFuture<?> future = scheduler.schedule(task,
                LocalDateTime.now().plusSeconds(workSeconds)
                             .toInstant(ZoneOffset.UTC));
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
                                                           0));
        return culture.getLightExposurePauseSeconds();
    }

    // Методы для отправки HTTP-запросов
    private void sendHttpRequest(Long entityId) {
        log.error("first request");
    }

    private void sendSecondHttpRequest(Long entityId) {
        log.error("second request");
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
                                                           0));
        return culture.getLightExposureSeconds();
    }

    @Scheduled(cron = "20 * * * * *")
    public void checkLightning() {
        cultureDataRepository.findAll().forEach(cultureData -> {
            LocalDateTime currentTime = LocalDateTime.now();
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
                log.error("Canceling scheduled tasks for entityId: {}", entityId);
                future.cancel(true);
            }
        });
    }

}
