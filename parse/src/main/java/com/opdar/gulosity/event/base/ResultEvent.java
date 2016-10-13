package com.opdar.gulosity.event.base;

/**
 * Created by Shey on 2016/8/22.
 */
public abstract class ResultEvent<T> implements Event {
    private T result;

    public T getResult() {
        return result;
    }

    public void setResult(T result) {
        this.result = result;
    }
}
