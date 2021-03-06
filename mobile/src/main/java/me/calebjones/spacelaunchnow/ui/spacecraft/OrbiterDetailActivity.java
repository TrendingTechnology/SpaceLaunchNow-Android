package me.calebjones.spacelaunchnow.ui.spacecraft;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.appcompat.widget.Toolbar;

import android.view.MenuItem;
import android.view.ViewPropertyAnimator;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.gson.Gson;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;
import io.realm.RealmResults;

import me.calebjones.spacelaunchnow.common.GlideApp;
import me.calebjones.spacelaunchnow.common.R;
import me.calebjones.spacelaunchnow.common.base.BaseActivityOld;
import me.calebjones.spacelaunchnow.common.prefs.ListPreferences;
import me.calebjones.spacelaunchnow.common.ui.settings.SettingsActivity;
import me.calebjones.spacelaunchnow.common.utils.CustomOnOffsetChangedListener;
import me.calebjones.spacelaunchnow.data.models.main.Agency;
import me.calebjones.spacelaunchnow.data.models.main.launcher.LauncherConfig;
import me.calebjones.spacelaunchnow.ui.main.MainActivity;
import me.calebjones.spacelaunchnow.utils.Utils;
import timber.log.Timber;

public class OrbiterDetailActivity extends BaseActivityOld implements AppBarLayout.OnOffsetChangedListener {

    private static final int PERCENTAGE_TO_ANIMATE_AVATAR = 20;
    private boolean mIsAvatarShown = true;
    private Context context;
    private RecyclerView mRecyclerView;
    private TextView toolbarSubTitle, toolbarTitle;
    private ImageView detail_profile_backdrop;
    private CircleImageView detail_profile_image;
    private SpacecraftConfigDetailAdapter adapter;
    private RealmResults<LauncherConfig> rocketLaunches;
    private AppBarLayout appBarLayout;
    private CollapsingToolbarLayout collapsingToolbar;
    private int mMaxScrollSize;
    private ListPreferences sharedPreference;
    private int statusColor;
    private CoordinatorLayout coordinatorLayout;
    private CustomOnOffsetChangedListener customOnOffsetChangedListener;

    public OrbiterDetailActivity() {
        super("Launcher Detail Activity");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.context = getApplicationContext();
        setTheme(R.style.BaseAppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehicle_list);

        sharedPreference = ListPreferences.getInstance(this.context);

//        statusColor = getCyanea().getPrimaryDark();

        Toolbar toolbar = findViewById(R.id.detail_toolbar);
        toolbarSubTitle = findViewById(R.id.detail_sub_title);
        toolbarTitle = findViewById(R.id.detail_title);
        detail_profile_image = findViewById(R.id.detail_profile_image);
        detail_profile_backdrop = findViewById(R.id.detail_profile_backdrop);
        collapsingToolbar = findViewById(R.id.main_collapsing_bar);
        coordinatorLayout = findViewById(R.id.coordinatorLayout);
        appBarLayout = findViewById(R.id.detail_appbar);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            // Fab button
            detail_profile_image.setScaleX(0);
            detail_profile_image.setScaleY(0);

            ViewPropertyAnimator showTitleAnimator = Utils.showViewByScale(detail_profile_image);
            showTitleAnimator.setStartDelay(500);
        } else {
            detail_profile_image.setScaleX(1);
            detail_profile_image.setScaleY(1);
        }
        customOnOffsetChangedListener = new CustomOnOffsetChangedListener(statusColor, getWindow());
        appBarLayout.addOnOffsetChangedListener(this);
        appBarLayout.addOnOffsetChangedListener(customOnOffsetChangedListener);
        mMaxScrollSize = appBarLayout.getTotalScrollRange();


        if (toolbar != null) {
            setSupportActionBar(toolbar);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setHomeButtonEnabled(true);
                getSupportActionBar().setDisplayShowTitleEnabled(false);
            }
        }
        adapter = new SpacecraftConfigDetailAdapter(context, this);
        mRecyclerView = findViewById(R.id.vehicle_detail_list);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setAdapter(adapter);
        displayRockets();
    }

    public void displayRockets() {
        Intent intent = getIntent();
        Gson gson = new Gson();
        final Agency agency = gson.fromJson(intent.getStringExtra("json"), Agency.class);

        if (agency == null) {
            Toast.makeText(context, R.string.error_launch_details, Toast.LENGTH_SHORT).show();
            Timber.e("Error - Unable to load launch details.");
            Intent homeIntent = new Intent(this, MainActivity.class);
            startActivity(homeIntent);
        } else {

            toolbarSubTitle.setText(agency.getType());
//            toolbarSubTitle.setTextColor(me.calebjones.spacelaunchnow.common.utils.Utils.getTitleTextColor(getCyanea().getPrimary()));
            toolbarTitle.setText(agency.getName());
//            toolbarTitle.setTextColor(me.calebjones.spacelaunchnow.common.utils.Utils.getSecondaryTitleTextColor(getCyanea().getPrimary()));


            if (agency.getSpacecraftConfigs() != null) {
                adapter.clear();
                adapter.addItems(agency.getSpacecraftConfigs());
            }

            applyProfileBackdrop(agency.getImageUrl());
            applyProfileLogo(agency.getNationUrl());
        }
    }

    private void applyProfileBackdrop(String drawableURL) {
        Timber.d("OrbiterDetailActivity - Loading Backdrop Image url: %s ", drawableURL);
        GlideApp.with(this)
                .load(drawableURL)
                .centerCrop()
                .into(detail_profile_backdrop);

    }

    private void applyProfileLogo(String url) {
        Timber.d("OrbiterDetailActivity - Loading Profile Image url: %s ", url);

        GlideApp.with(this)
                .load(url)
                .centerCrop()
                .placeholder(R.drawable.icon_international)
                .into(detail_profile_image);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            return true;
        }
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int i) {
        if (mMaxScrollSize == 0)
            mMaxScrollSize = appBarLayout.getTotalScrollRange();

        int percentage = (Math.abs(i)) * 100 / mMaxScrollSize;

        if (percentage >= PERCENTAGE_TO_ANIMATE_AVATAR && mIsAvatarShown) {
            mIsAvatarShown = false;
            detail_profile_image.animate().scaleY(0).scaleX(0).setDuration(200).start();
        }

        if (percentage <= PERCENTAGE_TO_ANIMATE_AVATAR && !mIsAvatarShown) {
            mIsAvatarShown = true;

            detail_profile_image.animate()
                    .scaleY(1).scaleX(1)
                    .start();
        }
    }
}
