package com.computerapplicationtechnologycnus.sakura_anime.common;

import com.alibaba.fastjson.JSONObject;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.extern.slf4j.Slf4j;

/**
 * 接口返回消息格式
 *
 * @param <T>
 */
@Slf4j
@Schema(description = "返回JSON对象")
public class ResultMessage<T> {

    @Schema(description = "返回信息状态[true成功、false失败]")
    private final boolean status;

    @Schema(description = "返回数据")
    private final T data;

    @Schema(description = "返回信息描述")
    private final String message;

    @Schema(description = "异常信息")
    private final String error;

    private ResultMessage(ResultBuilder<T> message) {
        this.status = message.status;
        this.data = message.data;
        this.message = message.message;
        this.error = message.error;
    }

    public static <T> ResultBuilder<T> builder() {
        return new ResultBuilder<>();
    }

    /**
     * @param data    数据
     * @param status  状态
     * @param message 消息
     * @return ResultMessage T Model
     */
    public static <T> ResultMessage<T> message(T data, boolean status, String message) {
        return ResultMessage.<T>builder().data(data).status(status).message(message).build();
    }

    /**
     * @param status  状态
     * @param message 消息
     * @return ResultMessage T Model
     */
    public static <T> ResultMessage<T> message(boolean status, String message) {
        return ResultMessage.<T>builder().status(status).message(message).build();
    }

    public static <T> ResultMessage<T> message(boolean status, String message, String error) {
        return ResultMessage.<T>builder().status(status).message(message).error(error).build();
    }

    @Override
    public String toString() {
        return JSONObject.toJSONString(this);
    }

    public boolean isStatus() {
        return status;
    }

    public T getData() {
        return data;
    }

    public String getMessage() {
        return message;
    }

    public String getError() {
        return error;
    }

    public static final class ResultBuilder<T> {

        private boolean status = false;

        private T data;

        private String message = "";

        private String error;

        private ResultBuilder() {

        }

        /**
         * 返回信息描述<br>
         * 默认为空
         *
         * @param message string
         * @return ResultMessage T Model
         */
        public ResultBuilder<T> message(String message) {
            this.message = message;
            return this;
        }

        /**
         * 返回数据<br>
         * 默认为null
         *
         * @param data model
         * @return ResultMessage T Model
         */
        public ResultBuilder<T> data(T data) {
            this.data = data;
            return this;
        }

        /**
         * 返回信息状态<br>
         * true成功 & false失败<br>
         * 默认为false
         *
         * @param status boolean
         * @return ResultMessage T Model
         */
        public ResultBuilder<T> status(boolean status) {
            this.status = status;
            return this;
        }

        /**
         * 返回信息描述<br>
         * 默认会返回数据，除非你给它点个null
         *
         * @return ResultMessage T Model
         */
        public ResultBuilder<T> error(String error) {
             this.error = error;
            log.error(error);
            return this;
        }

        public ResultMessage<T> build() {
            return new ResultMessage<>(this);
        }

    }
}
