package me.calebjones.spacelaunchnow.events.data;


import android.content.Context;
import android.net.Uri;

import me.calebjones.spacelaunchnow.data.models.main.Event;
import me.calebjones.spacelaunchnow.data.models.main.Launch;
import me.calebjones.spacelaunchnow.data.networking.DataClient;
import me.calebjones.spacelaunchnow.data.networking.error.ErrorUtil;
import me.calebjones.spacelaunchnow.data.networking.error.SpaceLaunchNowError;
import me.calebjones.spacelaunchnow.data.networking.responses.base.EventResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

public class EventDataLoader {

    private Context context;

    public EventDataLoader(Context context) {
        this.context = context;
    }

    public void getEventList(int limit, int offset, final Callbacks.EventListNetworkCallback networkCallback) {
        Timber.i("Running getUpcomingLaunchesList");

        DataClient.getInstance().getUpcomingEvents(limit, offset, new Callback<EventResponse>() {
            @Override
            public void onResponse(Call<EventResponse> call, Response<EventResponse> response) {
                if (response.isSuccessful()) {
                    EventResponse responseBody = response.body();

                    Timber.v("Astronauts returned Count: %s", responseBody.getCount());

                    if (responseBody.getNext() != null) {
                        Uri uri = Uri.parse(responseBody.getNext());
                        String limit = uri.getQueryParameter("limit");
                        String nextOffset = uri.getQueryParameter("offset");
                        String total = uri.getQueryParameter("offset");
                        int next = Integer.valueOf(nextOffset);
                        networkCallback.onSuccess(responseBody.getEvents(), next, responseBody.getCount(), true);
                    } else {
                        networkCallback.onSuccess(responseBody.getEvents(), 0, responseBody.getCount(), false);
                    }
                } else {
                    SpaceLaunchNowError error = ErrorUtil.parseSpaceLaunchNowError(response);
                    Timber.e(error.getMessage());
                    networkCallback.onNetworkFailure(response.code());

                }
            }

            @Override
            public void onFailure(Call<EventResponse> call, Throwable t) {
                networkCallback.onFailure(t);
            }
        });
    }

    public void getEvent(int id, final Callbacks.EventNetworkCallback networkCallback) {
        Timber.i("Running getUpcomingLaunchesList");
        DataClient.getInstance().getEventById(id, new Callback<Event>() {
            @Override
            public void onResponse(Call<Event> call, Response<Event> response) {
                if (response.isSuccessful()) {
                    Event event = response.body();
                    networkCallback.onSuccess(event);

                } else {
                    networkCallback.onNetworkFailure(response.code());

                }
            }

            @Override
            public void onFailure(Call<Event> call, Throwable t) {
                networkCallback.onFailure(t);
            }
        });
    }

    public void getEventBySlug(String slug, final Callbacks.EventNetworkCallback networkCallback) {
        Timber.i("Running get event by slug %s", slug);
        DataClient.getInstance().getEventBySlug(slug, new Callback<EventResponse>() {
            @Override
            public void onResponse(Call<EventResponse> call, Response<EventResponse> response) {
                if (response.isSuccessful()) {
                    EventResponse events = response.body();
                    if (events != null && events.getCount() == 1){
                        Event event = events.getEvents().get(0);
                        Timber.v("Even: %s", event.getName() );
                        networkCallback.onSuccess(event);
                    } else {
                        networkCallback.onNetworkFailure(404);
                    }

                } else {
                    networkCallback.onNetworkFailure(response.code());

                }
            }

            @Override
            public void onFailure(Call<EventResponse> call, Throwable t) {
                networkCallback.onFailure(t);
            }
        });
    }

}
