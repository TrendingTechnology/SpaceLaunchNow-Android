package me.calebjones.spacelaunchnow.content.loader;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import java.util.List;

import me.calebjones.spacelaunchnow.content.database.ListPreferences;
import me.calebjones.spacelaunchnow.content.models.Launch;

public class UpcomingLoader extends AsyncTaskLoader<List<Launch>> {

    private ListPreferences listPreferences;

    public UpcomingLoader(Context context) {
        super(context);
        listPreferences = ListPreferences.getInstance(context);
    }

    @Override
    public List<Launch> loadInBackground() {
        return listPreferences.getLaunchesUpcoming();
    }
}