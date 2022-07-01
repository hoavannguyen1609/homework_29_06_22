package com.example.product_management.entity.baseresponse;

public class BaseResponse {

    private String message;

    private Object data;

    public BaseResponse(Object data) {
        this.data = data;
        this.message = "success";
    }

    public BaseResponse(String message) {
        this.message = message;
    }

    public BaseResponse(Object data, String message) {
        this.message = message;
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
