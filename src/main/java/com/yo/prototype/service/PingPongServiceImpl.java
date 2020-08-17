package com.yo.prototype.service;

import com.google.protobuf.Empty;
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
}
