package com.bixuebihui.cgrpcdemo;

public class CalculatorClient {
    public static void main(String[] args) {
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 8080).usePlaintext().build();
        CalculatorServiceGrpc.CalculatorServiceBlockingStub stub = CalculatorServiceGrpc.newBlockingStub(channel);
        CalculatorRequest request = CalculatorRequest.newBuilder().setA(10).setB(20).build();
        CalculatorResponse response = stub.add(request);
        System.out.println(response.getResult());
        channel.shutdown();
    }
}
