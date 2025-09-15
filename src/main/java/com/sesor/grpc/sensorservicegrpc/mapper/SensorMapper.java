package com.sesor.grpc.sensorservicegrpc.mapper;

import com.sesor.grpc.sensorservicegrpc.TemperatureResponse;
import com.sesor.grpc.sensorservicegrpc.model.SensorData;

public class SensorMapper {
    public static TemperatureResponse getTempratureReponse(SensorData data) {
            return TemperatureResponse.newBuilder()
                    .setSensorId(data.getSensorId())
                    .setTemperature(data.getValue())
                    .setTimestamp(data.getTimestamp().toString())
                    .build();
    }
}
