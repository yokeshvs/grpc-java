package com.yo.prototype.service;

import com.yo.prototype.User;
import com.yo.prototype.UserRequest;
import com.yo.prototype.UserResponse;
import com.yo.prototype.UserServiceGrpc;
import io.grpc.stub.StreamObserver;

import java.util.UUID;

public class UserServiceImpl extends UserServiceGrpc.UserServiceImplBase {
    @Override
    public void createUser(UserRequest request, StreamObserver<UserResponse> responseObserver) {
        try {
            //extract request info
            User user = request.getUser();
            UserResponse userResponse = UserResponse.newBuilder()
                    .setFirstName(user.getFirstName())
                    .setLastName(user.getLastName())
                    .setId(UUID.randomUUID().toString())
                    .build();

            //send the response for gRPC call
            responseObserver.onNext(userResponse);
        } catch (Exception e) {
            responseObserver.onError(e);
        } finally {
            //complete the gRPC call
            responseObserver.onCompleted();
        }
    }
}
