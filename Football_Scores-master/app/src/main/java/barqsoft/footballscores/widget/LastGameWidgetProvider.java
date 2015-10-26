package barqsoft.footballscores.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.widget.RemoteViews;

import barqsoft.footballscores.R;
import barqsoft.footballscores.service.ScoresService;

/**
 * Provider for a widget showing last game.
 *
 * Delegates widget updating to {@link LastGameWidgetIntentService} to ensure that
 * data retrieval is done on a background thread
 */
public class LastGameWidgetProvider extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        context.startService(new Intent(context, LastGameWidgetIntentService.class));

        //update also from Api if need
        Intent service_start = new Intent(context, ScoresService.class);
        service_start.setAction(ScoresService.FETCH_SCORES);
        context.startService(service_start);
    }

    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager,
                                          int appWidgetId, Bundle newOptions) {
        context.startService(new Intent(context, LastGameWidgetIntentService.class));
    }

    @Override
    public void onReceive(@NonNull Context context, @NonNull Intent intent) {
        super.onReceive(context, intent);
        if (ScoresService.ACTION_DATA_UPDATED.equals(intent.getAction())) {
            context.startService(new Intent(context, LastGameWidgetIntentService.class));
        }
    }

}

