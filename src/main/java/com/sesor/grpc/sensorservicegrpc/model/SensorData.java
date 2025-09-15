package com.sesor.grpc.sensorservicegrpc.model;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class SensorData {
    private String sensorId;
    private double value;
    private Instant timestamp;

    public SensorData(String sensorId, double value, Instant timestamp) {
        this.sensorId = sensorId;
        this.value = value;
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return String.format("[%s] %s: %.2f", timestamp, sensorId, value);
    }
}