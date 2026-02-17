package com.linea_desk.rest_linea.common.dto;

public class ExceptionResponse<T> {
    private T data;
    private String message = "Internal Server Error";
    private final boolean success = false;

    public ExceptionResponse(
          String message,
          T data
    ) {
       this.message = message;
       this.data = data;
    }

   public ExceptionResponse(
            String message
   ) {
        this.message = message;
   }

    public ExceptionResponse() { }

    public T getData() {
        return data;
    }

    public String getMessage() {
        return message;
    }

    public void setData(T data) {
        this.data = data;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
