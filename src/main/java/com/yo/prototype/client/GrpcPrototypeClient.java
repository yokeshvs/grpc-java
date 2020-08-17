package com.yo.prototype.client;

import com.yo.prototype.*;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class GrpcPrototypeClient {
    public static void main(String[] args) {
        //Create Channel
        System.out.println("Creating channel");
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 50051)
                .usePlaintext() // For development purpose, disabling SSL
                .build();

        //Create Stub
        System.out.println("Creating blocking Stub");
        //Sync Client
        UserServiceGrpc.UserServiceBlockingStub userServiceBlockingStub = UserServiceGrpc.newBlockingStub(channel);
        CalculatorServiceGrpc.CalculatorServiceBlockingStub calculatorServiceBlockingStub = CalculatorServiceGrpc.newBlockingStub(channel);

        //Async Client
        //UserServiceGrpc.UserServiceFutureStub futureStub = UserServiceGrpc.newFutureStub(channel);

        UserRequest userRequest = UserRequest.newBuilder()
                .setUser(User.newBuilder()
                        .setFirstName("Test")
                        .setLastName("User")
                        .build())
                .build();
        UserResponse userResponse = userServiceBlockingStub.createUser(userRequest);
        System.out.println(userResponse.toString());

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
        //Shutdown Channel
        System.out.println("Shutting down channel");
        channel.shutdown();
    }
}
