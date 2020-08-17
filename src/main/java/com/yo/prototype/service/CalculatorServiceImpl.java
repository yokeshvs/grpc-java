package com.yo.prototype.service;

import com.yo.prototype.CalculatorRequest;
import com.yo.prototype.CalculatorResponse;
import com.yo.prototype.CalculatorServiceGrpc;
import io.grpc.stub.StreamObserver;

public class CalculatorServiceImpl extends CalculatorServiceGrpc.CalculatorServiceImplBase {
    @Override
    public void add(CalculatorRequest request, StreamObserver<CalculatorResponse> responseObserver) {
        responseObserver.onNext(CalculatorResponse.newBuilder()
                .setResult(request.getValue1() + request.getValue2()).build());
        responseObserver.onCompleted();
    }

    @Override
    public void subtract(CalculatorRequest request, StreamObserver<CalculatorResponse> responseObserver) {
        responseObserver.onNext(CalculatorResponse.newBuilder()
                .setResult(Math.abs(Math.subtractExact(request.getValue1(), request.getValue2()))).build());
        responseObserver.onCompleted();
    }

    @Override
    public void multiply(CalculatorRequest request, StreamObserver<CalculatorResponse> responseObserver) {
        responseObserver.onNext(CalculatorResponse.newBuilder()
                .setResult(Math.multiplyExact(request.getValue1(), request.getValue2())).build());
        responseObserver.onCompleted();
    }
}
