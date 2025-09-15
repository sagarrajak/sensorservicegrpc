package com.sesor.grpc.sensorservicegrpc.service;

public interface MessageSubscriber<T> {
    void onData(T message);
    void onStop();
}
