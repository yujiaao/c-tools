package com.bixuebihui;

public class BaseModel<T> {

    private int returncode;
    private String message;
    private T result;

    public BaseModel() {
    }

    public BaseModel(int returncode) {

        this.returncode = returncode;
    }

    public BaseModel(int returncode, String message) {

        this.returncode = returncode;
        this.message = message;
    }

    public BaseModel(int returncode, String message, T result) {
        this.returncode = returncode;
        this.message = message;
        this.result = result;
    }

    private BaseModel(Builder<T> builder) {
        setReturncode(builder.returncode);
        setMessage(builder.message);
        setResult(builder.result);
    }

    public static Builder newBuilder() {
        return new Builder();
    }


    public Integer getReturncode() {
        return returncode;
    }

    public void setReturncode(int returncode) {
        this.returncode = returncode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getResult() {
        return result;
    }

    public void setResult(T result) {
        this.result = result;
    }

    @Override
    public String toString() {
        return "{" +
                " returncode=" + returncode +
                ", message='" + message + '\'' +
                ", result=" + result +
                '}';
    }

    public static final class Builder<T> {
        private int returncode;
        private String message;
        private T result;

        private Builder() {
        }

        public Builder<T> returncode(int val) {
            returncode = val;
            return this;
        }

        public Builder<T> message(String val) {
            message = val;
            return this;
        }

        public Builder<T> result(T val) {
            result = val;
            return this;
        }

        public BaseModel build() {
            return new BaseModel(this);
        }
    }
}
