package com.bixuebihui.cgrpcdemo;

import java.io.IOException;

public class CalculatorServer {
    public static void main(String[] args) throws IOException, InterruptedException {
        Server server = ServerBuilder.forPort(8080).addService(new CalculatorServiceImpl()).build();
        server.start();
        server.awaitTermination();
    }
}

class CalculatorServiceImpl extends CalculatorServiceGrpc.CalculatorServiceImplBase {
    @Override
    public void add(CalculatorRequest request, StreamObserver<CalculatorResponse> responseObserver) {
        int result = request.getA() + request.getB();
        CalculatorResponse response = CalculatorResponse.newBuilder().setResult(result).build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
