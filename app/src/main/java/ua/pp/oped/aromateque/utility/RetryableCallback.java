package ua.pp.oped.aromateque.utility;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;


public abstract class RetryableCallback<T> implements Callback<T> {
    private int retryCount = 0;
    private int maxRetries = 3;
    private static final int MAX_RETRIES = 3;
    private static final int WAIT_TIME = 1000;

    public RetryableCallback() {
        this.maxRetries = MAX_RETRIES;
    }

    public RetryableCallback(int maxRetries) {
        this.maxRetries = maxRetries;
    }

    @Override
    public synchronized void onResponse(Call<T> call, Response<T> response) {
        if (!response.isSuccessful()) {
            if (retryCount++ < maxRetries) {
                Timber.d("Retrying API Call -  (" + retryCount + " / " + maxRetries + ")");
                Timber.d(response.errorBody().toString());
                call.clone().enqueue(this);
            } else {
                onFinalResponse(call, response);
            }
        } else {
            onFinalResponse(call, response);
        }
    }

    @Override
    public synchronized void onFailure(Call<T> call, Throwable t) {
        t.printStackTrace();
        if (retryCount++ < maxRetries) {
            Timber.d("Retrying API Call -  (" + retryCount + " / " + maxRetries + ")");
            call.clone().enqueue(this);
        } else
            onFinalFailure(call, t);
    }


    public abstract void onFinalResponse(Call<T> call, Response<T> response);


    public abstract void onFinalFailure(Call<T> call, Throwable t);
}
