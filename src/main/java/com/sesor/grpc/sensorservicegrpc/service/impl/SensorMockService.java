package com.sesor.grpc.sensorservicegrpc.service.impl;


import com.sesor.grpc.sensorservicegrpc.model.SensorData;
import com.sesor.grpc.sensorservicegrpc.service.MessagePublisher;
import com.sesor.grpc.sensorservicegrpc.service.MessageSubscriber;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
public class SensorMockService implements MessagePublisher<SensorData> {
    private final String sesnorId;
    private static final Random random = new Random();
    private final ScheduledExecutorService scheduledExecutorService;
    private final Map<String, MessageSubscriber<SensorData>> hasmMap;

    public SensorMockService(@Value("${sensor.id}") String sesnorId) {
        this.sesnorId = sesnorId;
        this.scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        hasmMap = new ConcurrentHashMap<>();
    }

    private void generateData() {
        double sampleData = 20 + 10 * random.nextDouble();
        SensorData sensorData = SensorData.builder()
                .sensorId(sesnorId)
                .value(sampleData)
                .timestamp(Instant.now())
                .build();
        this.publishData(sensorData);
    }

    public void startDataGenerator() {
        scheduledExecutorService.scheduleAtFixedRate(this::generateData,0, 10, TimeUnit.SECONDS);
    }

    public void stopDataGenerator() {
        if (scheduledExecutorService != null) {
            scheduledExecutorService.shutdown();
            try {
                if (!scheduledExecutorService.awaitTermination(5, TimeUnit.SECONDS)) {
                    scheduledExecutorService.shutdownNow();
                }
            } catch (InterruptedException e) {
                scheduledExecutorService.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
    }

    @Override
    public String addSubscriber(MessageSubscriber<SensorData> subscriber) {
        String id = UUID.randomUUID().toString();
        hasmMap.put(id, subscriber);
        return id;
    }

    @Override
    public boolean removeSubscriber(String subscriberId) {
        if (!this.hasmMap.containsKey(subscriberId))
            return false;
        this.hasmMap.remove(subscriberId);
        return true;
    }

    private void publishData(SensorData data) {
        this.hasmMap.values().forEach(x -> {
            x.onData(data);
        });
    }
}
