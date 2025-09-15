package com.sesor.grpc.sensorservicegrpc.service.impl;


import com.sesor.grpc.sensorservicegrpc.TemperatureRequest;
import com.sesor.grpc.sensorservicegrpc.TemperatureResponse;
import com.sesor.grpc.sensorservicegrpc.TemperatureServiceGrpc;
import com.sesor.grpc.sensorservicegrpc.mapper.SensorMapper;
import com.sesor.grpc.sensorservicegrpc.model.SensorData;
import com.sesor.grpc.sensorservicegrpc.service.MessageSubscriber;
import io.grpc.stub.ServerCallStreamObserver;
import io.grpc.stub.StreamObserver;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.grpc.server.service.GrpcService;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@GrpcService
@Slf4j
public class SensorGrpcService extends TemperatureServiceGrpc.TemperatureServiceImplBase implements
        MessageSubscriber<SensorData> {

    private final SensorMockService service;
    private final Set<StreamObserver<TemperatureResponse>> observer = ConcurrentHashMap.newKeySet();

    public SensorGrpcService(SensorMockService service) {
        this.service = service;
    }

    @PostConstruct
    void onInit() {
        this.service.addSubscriber(this);
        this.service.startDataGenerator();
        log.info("added listener to the service and started");
    }

    @PreDestroy
    void onDestroy() {
        this.service.stopDataGenerator();
    }

    @Override
    public void streamTemperature(TemperatureRequest request, StreamObserver<TemperatureResponse> responseObserver) {
       observer.add(responseObserver);
       if (responseObserver instanceof ServerCallStreamObserver<TemperatureResponse> serverObserver) {
           serverObserver.setOnCancelHandler(() -> {
               observer.remove(responseObserver);
               System.out.println("Client disconnected");
           });
       }
    }

    @Override
    public void onData(SensorData message) {
        TemperatureResponse tempratureReponse = SensorMapper.getTempratureReponse(message);
        for (StreamObserver<TemperatureResponse> obser: observer) {
            try {
                obser.onNext(tempratureReponse);
            } catch (Exception e) {
                observer.remove(obser);
            }
        }
    }

    @Override
    public void onStop() {
        observer.forEach(StreamObserver::onCompleted);
    }
}
