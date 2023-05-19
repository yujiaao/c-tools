package com.bixuebihui.cgrpcdemo;

public class CalculatorResponse {
    private CalculatorResponse(Builder builder) {
        setResult(builder.result);
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }

    int result;

    public static final class Builder {
        private int result;

        private Builder() {
        }

        public Builder setResult(int val) {
            result = val;
            return this;
        }

        public CalculatorResponse build() {
            return new CalculatorResponse(this);
        }
    }
}
