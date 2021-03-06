package fightingpit.VocabBuilder;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import butterknife.BindView;
import butterknife.ButterKnife;
import fightingpit.VocabBuilder.Engine.ContextManager;
import fightingpit.VocabBuilder.Engine.GlobalApplication;
import fightingpit.VocabBuilder.Engine.TextToSpeechManager;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    int mNavigationSelectedId = R.id.word_list; // Keep Track of current navigation item selected by user. TODO: Change to Shared Preference

    @BindView(R.id.toolbar) Toolbar mToolbar;
    public static final int CHECK_TTS_ENGINE = 101;
    public static final int SETTING_ACTIVITY_CODE = 102;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("++ABG++","OnCreateCalled");
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        ContextManager.setCurrentActivityContext(this);

        setSupportActionBar(mToolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        ((GlobalApplication) getApplicationContext()).init();

        // Init with Word List for now
        updateNavigationView(mNavigationSelectedId,true);
    }



    @Override
    protected void onDestroy() {
        Log.d("ABG", "on destroy called");
        TextToSpeechManager aTextToSpeechManager = ((GlobalApplication)
                getApplicationContext()).getTextToSpeechManager();
        if(aTextToSpeechManager!=null){
            aTextToSpeechManager.shutdown();
        }
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent i = new Intent(this, SettingsActivity.class);
            startActivityForResult(i, SETTING_ACTIVITY_CODE);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        updateNavigationView(id, false);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        mNavigationSelectedId = id;
        return true;
    }

    /**
     * Changes the current display based on user action.
     * @param id NavigationItem Id to be displayed/updated.
     * @param forceUpdate if current navigationItemId is same is param id, update will only be done
     *                    if forceUpdate is true. This is prevent unnecessary actions if user
     *                    select same navigationItem again.
     */
    public void updateNavigationView(int id, boolean forceUpdate)
    {
        if(forceUpdate || id != mNavigationSelectedId) {

            if (id == R.id.word_list) {
                getFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fl_cm, new WordListFragment())
                        .commit();
            } else if (id == R.id.flash_card) {
                getFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fl_cm, new FlashCardFragment())
                        .commit();

            } else if (id == R.id.nav_slideshow) {

            } else if (id == R.id.nav_manage) {

            } else if (id == R.id.nav_share) {

            } else if (id == R.id.nav_send) {

            }
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        ContextManager.setCurrentActivityContext(this);

        switch (requestCode) {
            case CHECK_TTS_ENGINE:
                TextToSpeechManager aTextToSpeechManager = ((GlobalApplication)
                        getApplicationContext()).getTextToSpeechManager();
                if(aTextToSpeechManager!=null){
                    aTextToSpeechManager.onActivityResult(resultCode,data);
                }
                break;
            case SETTING_ACTIVITY_CODE:
                ((GlobalApplication) getApplicationContext()).getDatabaseMethods().updateWordList();
                updateNavigationView(mNavigationSelectedId, true);
                break;
            default:
                break;
        }
    }
}
