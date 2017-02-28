package com.richdroid.memorygame.rest;

import retrofit2.Response;

/**
 * Represents an interface that should be implemented by clients that ask for Data from {@code ApiManager}
 */
public interface ApiRequester {

    /**
     * Fetch request failed.
     *
     * @param error
     */
    void onFailure(Throwable error);

    /**
     * Fetch request succeeded.
     *
     */
    void onSuccess(Response response);

}
