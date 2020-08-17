package com.yo.prototype.client;

import com.google.protobuf.Empty;
import com.yo.prototype.*;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class GrpcPrototypeClient {

    ManagedChannel channel;
    UserServiceGrpc.UserServiceBlockingStub userServiceBlockingStub;
    CalculatorServiceGrpc.CalculatorServiceBlockingStub calculatorServiceBlockingStub;
    PingPongServiceGrpc.PingPongServiceBlockingStub pingPongServiceBlockingStub;
    PingPongServiceGrpc.PingPongServiceStub pingPongServiceAsyncStub; //Async stub for client streaming

    private void initialize() {
        //Create Channel
        System.out.println("Creating channel");
        channel = ManagedChannelBuilder.forAddress("localhost", 50051)
                .usePlaintext() // For development purpose, disabling SSL
                .build();

        //Create Stub
        System.out.println("Creating blocking Stubs");
        //Sync Clients
        userServiceBlockingStub = UserServiceGrpc.newBlockingStub(channel);
        calculatorServiceBlockingStub = CalculatorServiceGrpc.newBlockingStub(channel);
        pingPongServiceBlockingStub = PingPongServiceGrpc.newBlockingStub(channel);


        //Create Stub
        System.out.println("Creating async Stubs");
        //Async Clients
        //UserServiceGrpc.UserServiceFutureStub futureStub = UserServiceGrpc.newFutureStub(channel);

        //Below is an async client for Client Streaming connection
        pingPongServiceAsyncStub = PingPongServiceGrpc.newStub(channel);
    }

    private void exit() {
        //Shutdown Channel
        System.out.println("Shutting down channel");
        channel.shutdown();
    }

    private void unaryUserCreate() {
        UserRequest userRequest = UserRequest.newBuilder()
                .setUser(User.newBuilder()
                        .setFirstName("Test")
                        .setLastName("User")
                        .build())
                .build();
        UserResponse userResponse = userServiceBlockingStub.createUser(userRequest);
        System.out.println(userResponse.toString());
    }

    private void unaryCalculations() {
        int input1 = 10;
        int input2 = 5;
        System.out.println("Addition: " + calculatorServiceBlockingStub.add(CalculatorRequest.newBuilder()
                .setValue1(input1)
                .setValue2(input2)
                .build()));

        System.out.println("Subtraction: " + calculatorServiceBlockingStub.subtract(CalculatorRequest.newBuilder()
                .setValue1(input1)
                .setValue2(input2)
                .build()));

        System.out.println("Multiplication: " + calculatorServiceBlockingStub.multiply(CalculatorRequest.newBuilder()
                .setValue1(input1)
                .setValue2(input2)
                .build()));
    }

    private void unaryServerStreaming() {
        pingPongServiceBlockingStub.ping(Empty.getDefaultInstance()).forEachRemaining(pong -> System.out.println(pong.getMessage()));

        calculatorServiceBlockingStub.primeDecompose(SingleInputRequest.newBuilder().setValue(120).build())
                .forEachRemaining(calculatorResponse -> System.out.println(calculatorResponse.getResult()));
    }

    private void unaryClientStreaming() {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        StreamObserver<Ping> pingStreamObserver = pingPongServiceAsyncStub.streamingPing(new StreamObserver<>() {
            @Override
            public void onNext(Pong value) {
                // this will be called once, after the client has sent all the data and server returns the response
                System.out.println("Server has sent some response");
                System.out.println(value.getMessage());
            }

            @Override
            public void onError(Throwable t) {
                // called when there is an error in server response
            }

            @Override
            public void onCompleted() {
                // this will be called right after onNext
                System.out.println("Server has completed sending the response");
                countDownLatch.countDown();
            }
        });

        pingStreamObserver.onNext(Ping.newBuilder().setMessage("Hi").build());
        pingStreamObserver.onNext(Ping.newBuilder().setMessage("my").build());
        pingStreamObserver.onNext(Ping.newBuilder().setMessage("name").build());
        pingStreamObserver.onNext(Ping.newBuilder().setMessage("is").build());
        pingStreamObserver.onNext(Ping.newBuilder().setMessage("Yo!").build());
        pingStreamObserver.onCompleted();
        try {
            //countDownLatch.await(3L, TimeUnit.MILLISECONDS); This would not print the response, cuz the wait time is very low
            countDownLatch.await(3L, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        GrpcPrototypeClient grpcPrototypeClient = new GrpcPrototypeClient();

        grpcPrototypeClient.initialize();

        grpcPrototypeClient.unaryUserCreate();

        grpcPrototypeClient.unaryCalculations();

        grpcPrototypeClient.unaryServerStreaming();

        grpcPrototypeClient.unaryClientStreaming();

        grpcPrototypeClient.exit();
    }
}
