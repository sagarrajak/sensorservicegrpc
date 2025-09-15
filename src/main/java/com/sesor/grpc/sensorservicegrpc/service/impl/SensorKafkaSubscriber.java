package com.sesor.grpc.sensorservicegrpc.service.impl;

import com.sesor.grpc.sensorservicegrpc.model.SensorData;
import com.sesor.grpc.sensorservicegrpc.service.MessagePublisher;
import com.sesor.grpc.sensorservicegrpc.service.MessageSubscriber;

public class SensorKafkaSubscriber implements MessageSubscriber<SensorData> {
    @Override
    public void onData(SensorData message) {
    }

    @Override
    public void onStop() {
    }
}
