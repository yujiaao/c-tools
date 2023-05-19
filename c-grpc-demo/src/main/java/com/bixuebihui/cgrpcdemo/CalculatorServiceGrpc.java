package com.bixuebihui.cgrpcdemo;

public class CalculatorServiceGrpc {
    public static CalculatorServiceBlockingStub newBlockingStub(ManagedChannel channel) {

    }

    public static class CalculatorServiceBlockingStub {
    }

    public abstract static class CalculatorServiceImplBase {
        public abstract void add(CalculatorRequest request, StreamObserver<CalculatorResponse> responseObserver);
    }
}
