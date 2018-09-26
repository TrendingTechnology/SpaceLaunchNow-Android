package me.calebjones.spacelaunchnow.content.data.callbacks;


import java.util.List;

import androidx.annotation.Nullable;
import io.realm.RealmResults;
import me.calebjones.spacelaunchnow.data.models.main.Launch;
import me.calebjones.spacelaunchnow.data.models.main.LaunchList;

public class Callbacks {

    public interface NextNetworkCallback {
        void onSuccess();
        void onNetworkFailure(int code);
        void onFailure(Throwable throwable);
    }

    public interface NextLaunchesCallback {
        void onLaunchesLoaded(RealmResults<Launch> launches);
        void onNetworkStateChanged(boolean refreshing);
        void onError(String message, @Nullable Throwable throwable);
    }

    public interface ListNetworkCallback {
        void onSuccess(List<Launch> launches, int next);
        void onNetworkFailure(int code);
        void onFailure(Throwable throwable);
    }

    public interface ListNetworkCallbackMini {
        void onSuccess(List<LaunchList> launches, int next, int total);
        void onNetworkFailure(int code);
        void onFailure(Throwable throwable);
    }

    public interface ListCallback {
        void onLaunchesLoaded(List<Launch> launches, int nextOffset);
        void onNetworkStateChanged(boolean refreshing);
        void onError(String message, @Nullable Throwable throwable);
    }

    public interface ListCallbackMini {
        void onLaunchesLoaded(List<LaunchList> launches, int nextOffset, int total);
        void onNetworkStateChanged(boolean refreshing);
        void onError(String message, @Nullable Throwable throwable);
    }
}
