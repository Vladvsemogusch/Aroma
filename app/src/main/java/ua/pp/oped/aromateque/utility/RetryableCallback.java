package ua.pp.oped.aromateque.utility;

import android.util.Log;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public abstract class RetryableCallback<T> implements Callback<T> {
    private static final String TAG = "RetryableCallback";
    private int retryCount = 0;
    private int maxRetries = 3;
    private static final int MAX_RETRIES = 100;
    private static final int WAIT_TIME = 1000;

    public RetryableCallback() {
        this.maxRetries = MAX_RETRIES;
    }

    public RetryableCallback(int maxRetries) {
        this.maxRetries = maxRetries;
    }

    @Override
    public void onResponse(Call<T> call, Response<T> response) {
        if (!response.isSuccessful()) {
            if (retryCount++ < maxRetries) {
                Log.v(TAG, "Retrying API Call -  (" + retryCount + " / " + maxRetries + ")");
                try {
                    wait(WAIT_TIME);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                call.clone().enqueue(this);
            } else {
                onFinalResponse(call, response);
            }
        } else {
            onFinalResponse(call, response);
        }
    }

    @Override
    public void onFailure(Call<T> call, Throwable t) {
        Log.e(TAG, t.getMessage());
        if (retryCount++ < maxRetries) {
            Log.v(TAG, "Retrying API Call -  (" + retryCount + " / " + maxRetries + ")");
            try {
                wait(WAIT_TIME);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            call.clone().enqueue(this);
        } else
            onFinalFailure(call, t);
    }


    public abstract void onFinalResponse(Call<T> call, Response<T> response);


    public abstract void onFinalFailure(Call<T> call, Throwable t);
}
