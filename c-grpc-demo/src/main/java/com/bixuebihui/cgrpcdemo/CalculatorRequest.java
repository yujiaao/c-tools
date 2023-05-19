package com.bixuebihui.cgrpcdemo;

public class CalculatorRequest {
    public int getA() {
        return a;
    }

    public void setA(int a) {
        this.a = a;
    }

    public int getB() {
        return b;
    }

    public void setB(int b) {
        this.b = b;
    }

    private int a;
    private int b;

    private CalculatorRequest(Builder builder) {
        a = builder.a;
        b = builder.b;
    }

    public static Builder newBuilder() {
        return new Builder();
    }


    public static final class Builder {
        private int a;
        private int b;

        private Builder() {
        }

        public Builder setA(int val) {
            a = val;
            return this;
        }

        public Builder setB(int val) {
            b = val;
            return this;
        }

        public CalculatorRequest build() {
            return new CalculatorRequest(this);
        }
    }
}
