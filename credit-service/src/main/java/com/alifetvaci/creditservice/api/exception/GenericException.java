package com.alifetvaci.creditservice.api.exception;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;


@Getter
@Setter
public class GenericException extends RuntimeException {

    private ErrorCode errorCode;
    private HttpStatus httpStatus;
    private String logMessage;
    public GenericException(Builder builder) {
        this.errorCode = builder.message;
        this.httpStatus = builder.httpStatus;
        this.logMessage = builder.logMessage;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private ErrorCode message;
        private HttpStatus httpStatus;
        private String logMessage;
        public Builder() {

        }

        public Builder message(ErrorCode message) {
            this.message = message;
            return this;
        }

        public Builder httpStatus(HttpStatus httpStatus) {
            this.httpStatus = httpStatus;
            return this;
        }

        public Builder logMessage(String logMessage, Object... params) {
            this.logMessage = params(params, logMessage);
            return this;
        }

        public String params(Object[] params, String logMessage) {
            this.logMessage = logMessage;
            for (int i = 0; i < params.length; i++) {
                this.logMessage = this.logMessage.replace("{" + i + "}", params[i].toString());
            }
            return this.logMessage;
        }

        public GenericException build() {
            return new GenericException(this);
        }


    }

}
