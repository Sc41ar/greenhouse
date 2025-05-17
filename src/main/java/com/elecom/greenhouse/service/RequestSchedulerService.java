package com.elecom.greenhouse.service;

import com.elecom.greenhouse.entities.CultureData;
import com.elecom.greenhouse.entities.SensorsData;
import com.elecom.greenhouse.repositories.CultureDataRepository;
import com.elecom.greenhouse.repositories.SensorsDataRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

@Service
@Slf4j
public class RequestSchedulerService {

    private final SensorsDataRepository sensorsDataRepository;

    private final Map<Long, ScheduledFuture<?>> scheduledTasks = new ConcurrentHashMap<>();
    private final ThreadPoolTaskScheduler scheduler;
    private final CultureDataRepository cultureDataRepository;
    private final RestTemplate template = new RestTemplate();
    @Value("${sensors.controller.url}")
    private String sensorsControllerUrl;

    public RequestSchedulerService(ThreadPoolTaskScheduler scheduler,
                                   CultureDataRepository cultureDataRepository,
                                   SensorsDataRepository sensorsDataRepository) {
        this.scheduler = scheduler;
        this.cultureDataRepository = cultureDataRepository;
        this.sensorsDataRepository = sensorsDataRepository;
    }

    // Метод для планирования первого запроса
    public void scheduleLightFirstRequest(Long entityId, Instant scheduleTime) {
        log.info("Scheduling first request for entityId: {}", entityId);
        Runnable task = () -> {
            sendLightOnRequest(entityId); // Отправка первого запроса
            scheduleLightSecondRequest(entityId); // После отправки планируем второй запрос
        };

        ScheduledFuture<?> future = scheduler.schedule(task,
                scheduleTime);
        scheduledTasks.put(entityId, future);
    }

    public void scheduleWaterRequest(Long entityId, Instant scheduleTime) {
        log.info("Scheduling water request for entityId: {}", entityId);
        Runnable task = () -> {
            sendWaterOnRequest(entityId);
            scheduleWaterSecondRequest(entityId);
        };

        ScheduledFuture<?> future = scheduler.schedule(task, scheduleTime);
        scheduledTasks.put(entityId, future);
    }

    private void scheduleWaterSecondRequest(Long entityId) {
        log.info("Scheduling second water request for entityId: {}", entityId);
        int workSeconds = getWaterSeconds(entityId);
        int pauseSeconds = getWaterPauseSeconds(entityId);
        Runnable task = () -> {
            sendWaterOffRequest(entityId);
            scheduleWaterRequest(entityId, Instant.now().plusSeconds(pauseSeconds));
        };

        ScheduledFuture<?> future = scheduler.schedule(task, Instant.now().plusSeconds(workSeconds));
        scheduledTasks.put(entityId, future);
    }

    // Метод для планирования второго запроса
    private void scheduleLightSecondRequest(Long entityId) {
        log.info("Scheduling second request for entityId: {}", entityId);
        int workSeconds = getLightSeconds(entityId);
        int pauseSeconds = getPauseSecondsFromDatabase(entityId);
        Runnable task = () -> {
            sendLightoffRequest(entityId);
            scheduleLightFirstRequest(entityId,
                    Instant.now().plusSeconds(pauseSeconds));
        };
        ScheduledFuture<?> future = scheduler.schedule(task,
                Instant.now().plusSeconds(workSeconds));
        scheduledTasks.put(entityId, future);
    }

    private int getPauseSecondsFromDatabase(Long entityId) {
        CultureData culture = cultureDataRepository.findById(entityId)
                                                   .orElseGet(() -> new CultureData());
        return culture.getLightExposurePauseSeconds();
    }

    // Методы для отправки HTTP-запросов
    private void sendLightOnRequest(Long entityId) {
        log.info("first request executed entityId: {}", entityId);
        try {
            template.getForObject(sensorsControllerUrl + "/light_on", String.class);
        } catch (Exception e) {
            log.error("Error sending first request", e);
        }

    }

    private void sendWaterOnRequest(Long entityId) {
        log.info("water request executed {}", entityId);
        try {
            template.getForObject(sensorsControllerUrl + "/water_on", String.class);
        } catch (Exception e) {
            log.error("Error sending water request", e);
        }
    }

    private void sendLightoffRequest(Long entityId) {
        log.info("second request executed {}", entityId);
        try {
            template.getForObject(sensorsControllerUrl + "/light_off", String.class);

        } catch (Exception e) {
            log.error("Error sending second request", e);
        }
    }

    private void sendWaterOffRequest(Long entityId) {
        log.info("water off request executed {}", entityId);
        try {
            template.getForObject(sensorsControllerUrl + "/water_off", String.class);
        } catch (Exception e) {
            log.error("Error sending water off request", e);
        }
    }

    // Метод для получения интервала из БД
    private int getLightSeconds(Long entityId) {
        CultureData culture = cultureDataRepository.findById(entityId)
                                                   .orElseGet(() -> new CultureData());
        return culture.getLightExposureSeconds();
    }

    private int getWaterSeconds(Long entityId) {
        CultureData culture = cultureDataRepository.findById(entityId)
                                                   .orElseGet(() -> new CultureData());
        return culture.getWateringSeconds();
    }

    private int getWaterPauseSeconds(Long entityId) {
        CultureData culture = cultureDataRepository.findById(entityId)
                                                   .orElseGet(() -> new CultureData());
        return culture.getWateringPauseSeconds();
    }

    @Scheduled(initialDelay = 1000)
    public void checkLightning() {
        cultureDataRepository.findFirstByOrderByUpdatedAtDesc().ifPresent(cultureData -> {
            Instant currentTime = Instant.now();
            log.info(
                    "Current time: {}. Light exposure seconds: {}. Light exposure pause seconds: {}",
                    currentTime,
                    cultureData.getLightExposureSeconds(),
                    cultureData.getLightExposurePauseSeconds());
            scheduleWaterRequest(cultureData.getId(), currentTime);
            scheduleLightFirstRequest(cultureData.getId(), currentTime);
        });
    }

    //    @Scheduled(fixedRate = 5000)
    public void collectSensorsData() {
        String sensorsDataString = template.getForObject(sensorsControllerUrl + "/get_data",
                String.class);

        String[] sensorsDataSplit = sensorsDataString.split(";");
        SensorsData sensorsData = null;
        try {
            sensorsData = new SensorsData();
            sensorsData.setSoilMoisture(Double.parseDouble(sensorsDataSplit[0]));
            sensorsData.setTemperature(Double.parseDouble(sensorsDataSplit[1]));
            sensorsData.setHumidity(Double.parseDouble(sensorsDataSplit[2]));
            sensorsData.setCo2(Double.parseDouble(sensorsDataSplit[3]));
            sensorsData.setWaterTemperature(Double.parseDouble(sensorsDataSplit[4]));
        } catch (Exception e) {
            log.error("Error parsing sensor data", e);
        } finally {
            if (sensorsData != null) {
                sensorsDataRepository.save(sensorsData);
            }
        }

    }

    public void resumeTasks(long entityId) {
        cultureDataRepository.findById(entityId).ifPresent(cultureData -> {
            Instant currentTime = Instant.now();
            log.info(
                    "Current time: {}. Light exposure seconds: {}. Light exposure pause seconds: {}",
                    currentTime,
                    cultureData.getLightExposureSeconds(),
                    cultureData.getLightExposurePauseSeconds());
            scheduleLightFirstRequest(cultureData.getId(), currentTime);
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

    public void resumeTasks() {
        cultureDataRepository.findFirstByOrderByUpdatedAtDesc().ifPresent(cultureData -> {
            Instant currentTime = Instant.now();
            log.info(
                    "Current time: {}. Light exposure seconds: {}. Light exposure pause seconds: {}",
                    currentTime,
                    cultureData.getLightExposureSeconds(),
                    cultureData.getLightExposurePauseSeconds());
            scheduleLightFirstRequest(cultureData.getId(), currentTime);
            scheduleWaterRequest(cultureData.getId(), currentTime);
        });
    }

    public void cancelTasks() {
        scheduledTasks.forEach((id, future) -> {
            log.info("Canceling scheduled tasks ");
            future.cancel(true);
        });
    }

}
