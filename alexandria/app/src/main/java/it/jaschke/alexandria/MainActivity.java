package it.jaschke.alexandria;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import it.jaschke.alexandria.api.Callback;


public class MainActivity extends ActionBarActivity implements NavigationDrawerFragment.NavigationDrawerCallbacks, Callback {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment navigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence title;
    public static boolean IS_TABLET = false;
    private BroadcastReceiver messageReciever;

    public static final String MESSAGE_EVENT = "MESSAGE_EVENT";
    public static final String MESSAGE_KEY = "MESSAGE_EXTRA";

    private final int POSITION_ITEM_LIST_OF_BOOK = 0;
    private final int POSITION_ITEM_ADD_BOOK = 1;
    private final int POSITION_ITEM_ABOUT = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        IS_TABLET = isTablet();
        if(IS_TABLET){
            setContentView(R.layout.activity_main_tablet);
        }else {
            setContentView(R.layout.activity_main);
        }

        messageReciever = new MessageReciever();
        IntentFilter filter = new IntentFilter(MESSAGE_EVENT);
        LocalBroadcastManager.getInstance(this).registerReceiver(messageReciever,filter);

        navigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        title = getTitle();

        // Set up the drawer.
        navigationDrawerFragment.setUp(R.id.navigation_drawer,
                    (DrawerLayout) findViewById(R.id.drawer_layout));
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {

        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment currentFragment = fragmentManager.findFragmentById(R.id.container);
        Fragment nextFragment = null;

        switch (position){
            default:
            case POSITION_ITEM_LIST_OF_BOOK:
                if (currentFragment == null || !(currentFragment instanceof ListOfBooks)) {
                    nextFragment = new ListOfBooks();
                }
                break;
            case POSITION_ITEM_ADD_BOOK:
                if (currentFragment == null || !(currentFragment instanceof AddBook)) {
                    nextFragment = new AddBook();
                }
                break;
            case POSITION_ITEM_ABOUT:
                if (currentFragment == null || !(currentFragment instanceof About)) {
                    nextFragment = new About();
                }
                break;

        }

        if (nextFragment != null) {
            fragmentManager.beginTransaction()
                    .replace(R.id.container, nextFragment)
                    .addToBackStack((String) title)
                    .commit();

            //if twopane available
            View right_container = findViewById(R.id.right_container);
            if(right_container != null){
                //and if need two pane, show right container
                if (nextFragment instanceof ListOfBooks) {
                    right_container.setVisibility(View.VISIBLE);
                }
                else {
                    right_container.setVisibility(View.GONE);
                }
            }
        }
    }

    public void setTitle(int titleId) {
        title = getString(titleId);

        //FIX : update actionbar
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(title);

        //FIX : update navigationDrawler
        switch (titleId) {
            case R.string.books:
                navigationDrawerFragment.changeCheckedItem(POSITION_ITEM_LIST_OF_BOOK);
                break;
            case R.string.scan:
                navigationDrawerFragment.changeCheckedItem(POSITION_ITEM_ADD_BOOK);
                break;
            case R.string.about:
                navigationDrawerFragment.changeCheckedItem(POSITION_ITEM_ABOUT);
                break;
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(title);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!navigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(messageReciever);
        super.onDestroy();
    }

    @Override
    public void onItemSelected(String ean) {
        //Implements Master/Detail flow
        if(findViewById(R.id.right_container) != null){
            int id = R.id.right_container;
            Fragment fragment = BookDetail.createFragment(
                    ean
            );
            getSupportFragmentManager().beginTransaction()
                    .replace(id, fragment)
                    .addToBackStack(getString(R.string.book_detail))
                    .commit();
        } else {
            BookDetailActivity.startActivity(MainActivity.this, ean);
        }

    }

    private class MessageReciever extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getStringExtra(MESSAGE_KEY)!=null){
                Toast.makeText(MainActivity.this, intent.getStringExtra(MESSAGE_KEY), Toast.LENGTH_LONG).show();
            }
        }
    }

    /*
    //not need : only used with BookDetail and it was replaced by Master/Detail flow
    public void goBack(View view){
        getSupportFragmentManager().popBackStack();
    }*/

    private boolean isTablet() {
        return (getApplicationContext().getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    @Override
    public void onBackPressed() {
        if(getSupportFragmentManager().getBackStackEntryCount()<2){
            finish();
        }
        super.onBackPressed();
    }


}