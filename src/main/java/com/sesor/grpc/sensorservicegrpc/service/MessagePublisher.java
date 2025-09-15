package com.sesor.grpc.sensorservicegrpc.service;

public interface MessagePublisher<T> {
    String addSubscriber(MessageSubscriber<T> subscriber);
    boolean removeSubscriber(String subscriberId);
}
