package com.yo.prototype.server;

import com.yo.prototype.service.CalculatorServiceImpl;
import com.yo.prototype.service.UserServiceImpl;
import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;

public class GrpcPrototypeServer {
    public static void main(String[] args) throws InterruptedException, IOException {
        System.out.println("Initiating UserServer");
        Server server = ServerBuilder
                .forPort(50051)
                .addService(new UserServiceImpl())
                .addService(new CalculatorServiceImpl())
                .build();

        server.start();
        System.out.println("Successfully started UserServer");

        Runtime.getRuntime().addShutdownHook(new Thread( () -> {
            System.out.println("Received Shutdown request for UserServer");
            server.shutdown();
            System.out.println("UserServer successfully terminated");
        }));

        server.awaitTermination();
    }
}
