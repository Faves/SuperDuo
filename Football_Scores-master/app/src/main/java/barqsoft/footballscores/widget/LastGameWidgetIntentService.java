package barqsoft.footballscores.widget;

import android.annotation.TargetApi;
import android.app.IntentService;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.widget.RemoteViews;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import barqsoft.footballscores.DatabaseContract;
import barqsoft.footballscores.MainActivity;
import barqsoft.footballscores.MatchesOfDayAdapter;
import barqsoft.footballscores.R;
import barqsoft.footballscores.Utilities;


/**
 * IntentService which handles updating all LastGame widgets with the latest data
 */
public class LastGameWidgetIntentService extends IntentService {
    public static final String LOG_TAG = "LastGameWidgetIS";

    public LastGameWidgetIntentService() {
        super("LastGameWidgetIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        // Retrieve all of the LastGame widget ids: these are the widgets we need to update
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this,
                LastGameWidgetProvider.class));

        // Get last game's data from the ContentProvider
        Cursor data = getContentResolver().query(
                DatabaseContract.scores_table.buildScoreLastGame(),
                null, null,
                null, null);
        if (data == null) {
            return;
        }
        if (!data.moveToFirst()) {
            data.close();
            return;
        }

        // Extract the last game data from the Cursor
        String homeName = data.getString(MatchesOfDayAdapter.COL_HOME);
        String score = Utilities.getScores(data.getInt(MatchesOfDayAdapter.COL_HOME_GOALS), data.getInt(MatchesOfDayAdapter.COL_AWAY_GOALS));
        String awayName = data.getString(MatchesOfDayAdapter.COL_AWAY);
        String dateFromBdd = data.getString(MatchesOfDayAdapter.COL_DATE);
        String matchTime = data.getString(MatchesOfDayAdapter.COL_MATCHTIME);
        data.close();

        //format date
        String dateToShow=dateFromBdd;
        SimpleDateFormat mformat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        try {
            Date dateOfGame=mformat.parse(dateFromBdd);
            dateToShow=SimpleDateFormat.getDateInstance().format(dateOfGame);
        } catch (ParseException e) {
            Log.e(LOG_TAG, e.getMessage());
        }

        // Perform this loop procedure for each LastGame widget
        for (int appWidgetId : appWidgetIds) {
            // Find the correct layout based on the widget's height
            int widgetHeight = getWidgetHeight(appWidgetManager, appWidgetId);
            int defaultHeight = getResources().getDimensionPixelSize(R.dimen.widget_last_game_default_height);
            int layoutId;
            if (widgetHeight >= defaultHeight) {
                layoutId = R.layout.last_game_widget;
            } else {
                layoutId = R.layout.last_game_widget_small;
            }
            RemoteViews views = new RemoteViews(getPackageName(), layoutId);

            views.setTextViewText(R.id.appwidget_date, dateToShow + " " + matchTime);
            views.setTextViewText(R.id.appwidget_home, homeName);
            views.setTextViewText(R.id.appwidget_score, score);
            views.setTextViewText(R.id.appwidget_away, awayName);

            // Create an Intent to launch MainActivity
            Intent launchIntent = new Intent(this, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, launchIntent, 0);
            views.setOnClickPendingIntent(R.id.widget, pendingIntent);

            // Tell the AppWidgetManager to perform an update on the current app widget
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }

    private int getWidgetHeight(AppWidgetManager appWidgetManager, int appWidgetId) {
        // Prior to Jelly Bean, widgets were always their default size
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            return getResources().getDimensionPixelSize(R.dimen.widget_last_game_default_height);
        }
        // For Jelly Bean and higher devices, widgets can be resized - the current size can be
        // retrieved from the newly added App Widget Options
        return getWidgetHeightFromOptions(appWidgetManager, appWidgetId);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private int getWidgetHeightFromOptions(AppWidgetManager appWidgetManager, int appWidgetId) {
        Bundle options = appWidgetManager.getAppWidgetOptions(appWidgetId);
        if (options.containsKey(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT)) {
            int minHeightDp = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT);
            // The Height returned is in dp, but we'll convert it to pixels to match the other heights
            DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
            return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, minHeightDp,
                    displayMetrics);
        }
        return  getResources().getDimensionPixelSize(R.dimen.widget_last_game_default_height);
    }
}
