package com.yo.prototype.service;

import com.yo.prototype.*;
import io.grpc.Status;
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

    @Override
    public void primeDecompose(SingleInputRequest request, StreamObserver<CalculatorResponse> responseObserver) {
        try {
            int input = request.getValue();
            int k = 2;
            while (input > 1) {
                while (input % k == 0 && input > 1) {
                    responseObserver.onNext(CalculatorResponse.newBuilder().setResult(k).build());
                    input = input / k;
                }
                k = k + 1;
            }
        } catch (Exception e) {
            e.printStackTrace();
            responseObserver.onError(e);
        } finally {
            responseObserver.onCompleted();
        }
    }

    @Override
    public StreamObserver<SingleInputRequest> average(StreamObserver<CalculatorResponse> responseObserver) {
        return new StreamObserver<>() {
            int elementCount = 0;
            int total = 0;

            @Override
            public void onNext(SingleInputRequest inputRequest) {
                elementCount++;
                total += inputRequest.getValue();
            }

            @Override
            public void onError(Throwable t) {

            }

            @Override
            public void onCompleted() {
                responseObserver.onNext(CalculatorResponse.newBuilder().setResult(total / elementCount).build());
                responseObserver.onCompleted();
            }
        };
    }

    @Override
    public StreamObserver<SingleInputRequest> findMax(StreamObserver<CalculatorResponse> responseObserver) {
        return new StreamObserver<SingleInputRequest>() {
            int max = Integer.MIN_VALUE;

            @Override
            public void onNext(SingleInputRequest inputRequest) {
                if (inputRequest.getValue() > max) {
                    max = inputRequest.getValue();
                    responseObserver.onNext(CalculatorResponse.newBuilder().setResult(max).build());
                }
            }

            @Override
            public void onError(Throwable t) {

            }

            @Override
            public void onCompleted() {
                responseObserver.onCompleted();
            }
        };
    }

    @Override
    public void findSqrt(SingleInputRequest request, StreamObserver<CalculatorResponseInDouble> responseObserver) {
        if (request.getValue() >= 0) {
            responseObserver.onNext(CalculatorResponseInDouble.newBuilder().setResult(Math.sqrt(request.getValue())).build());
            responseObserver.onCompleted();
        } else {
            responseObserver.onError(Status.INVALID_ARGUMENT
                    .withDescription("The number you have sent is invalid")
                    .augmentDescription("Number sent: " + request.getValue())
                    .asRuntimeException());
        }
    }
}
