package com.yo.prototype.service;

import com.google.protobuf.Empty;
import com.yo.prototype.Ping;
import com.yo.prototype.PingPongServiceGrpc;
import com.yo.prototype.Pong;
import io.grpc.stub.StreamObserver;

public class PingPongServiceImpl extends PingPongServiceGrpc.PingPongServiceImplBase {
    @Override
    public void ping(Empty request, StreamObserver<Pong> responseObserver) {
        try {
            for (int i = 0; i < 3; i++) {
                responseObserver.onNext(Pong.newBuilder().setMessage("Hello there! Now the time is " + String.valueOf(System.currentTimeMillis())).build());
                Thread.sleep(1000);
            }
        } catch (Exception e) {
            responseObserver.onError(e);
        } finally {
            responseObserver.onCompleted();
        }
    }

    @Override
    public StreamObserver<Ping> streamingPing(StreamObserver<Pong> responseObserver) {
        StringBuilder stringBuilder = new StringBuilder();
        return new StreamObserver<Ping>() {
            @Override
            public void onNext(Ping value) {
                stringBuilder.append(value.getMessage()).append(" ");
            }

            @Override
            public void onError(Throwable t) {

            }

            @Override
            public void onCompleted() {
                responseObserver.onNext(Pong.newBuilder().setMessage(stringBuilder.toString()).build());
                responseObserver.onCompleted();
            }
        };
    }
}
