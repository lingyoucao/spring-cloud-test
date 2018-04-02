package com.newland.bd.ms.learning.dashboard.model;

/**
 * Created by lcs on 2018/3/30.
 *
 * @author lcs
 */
public class Result {
    private long success;
    private long fail;
    private long fallbackSuccess;

    public long getSuccess() {
        return success;
    }

    public void setSuccess(long success) {
        this.success = success;
    }

    public long getFail() {
        return fail;
    }

    public void setFail(long fail) {
        this.fail = fail;
    }

    public long getFallbackSuccess() {
        return fallbackSuccess;
    }

    public void setFallbackSuccess(long fallbackSuccess) {
        this.fallbackSuccess = fallbackSuccess;
    }

    @Override
    public String toString() {
        return "Result{" +
                "success=" + success +
                ", fail=" + fail +
                ", fallbackSuccess=" + fallbackSuccess +
                '}';
    }
}
