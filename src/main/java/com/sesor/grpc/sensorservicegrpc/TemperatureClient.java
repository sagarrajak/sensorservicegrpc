package com.sesor.grpc.sensorservicegrpc;

import com.sesor.grpc.sensorservicegrpc.TemperatureServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

import java.util.concurrent.CountDownLatch;

public class TemperatureClient {
    public static void main(String[] args) throws InterruptedException {
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 8080)
                .usePlaintext()
                .build();
        TemperatureServiceGrpc.TemperatureServiceStub stub = TemperatureServiceGrpc.newStub(channel);

        com.sesor.grpc.sensorservicegrpc.TemperatureRequest request = com.sesor.grpc.sensorservicegrpc.TemperatureRequest.newBuilder()
                .setSensorId("sensor-1")
                .build();
        CountDownLatch latch = new CountDownLatch(1);
        stub.streamTemperature(request, new StreamObserver<com.sesor.grpc.sensorservicegrpc.TemperatureResponse>() {
            @Override
            public void onNext(com.sesor.grpc.sensorservicegrpc.TemperatureResponse response) {
                System.out.printf("Sensor: %s, Temperature: %.2f°C, Time: %s%n",
                        response.getSensorId(),
                        response.getTemperature(),
                        response.getTimestamp());
            }

            @Override
            public void onError(Throwable t) {
                System.err.println("Error: " + t.getMessage());
                latch.countDown();
            }

            @Override
            public void onCompleted() {
                System.out.println("Stream completed");
                channel.shutdown();
            }
        });
        latch.await();
        channel.shutdown();

    }
}
