package info.serxan.trackerprojectmanager.activities;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;

import java.util.Objects;

import info.serxan.trackerprojectmanager.R;
import info.serxan.trackerprojectmanager.fragments.AuthFragment;
import info.serxan.trackerprojectmanager.fragments.MapCurrentSpyFragment;
import info.serxan.trackerprojectmanager.fragments.RegisteredSpyFragment;
import info.serxan.trackerprojectmanager.models.PositionModel;
import info.serxan.trackerprojectmanager.tools.ApplicationTool;

public class MainActivity
        extends AppCompatActivity
        implements
        NavigationView.OnNavigationItemSelectedListener,
        RegisteredSpyFragment.OnListFragmentInteractionListener,
        MapCurrentSpyFragment.OnFragmentInteractionListener,
        AuthFragment.OnFragmentInteractionListener {

    public ApplicationTool app;

    /**
     * Change the view is the user is logged.
     */
    @Override
    public void onUserLogged() {
        changeMainView(new RegisteredSpyFragment(), false);
        app.setMenuLogged(true);
        closeKeyboard();
    }

    /**
     * Change the view is the user is not logged.
     */
    public void onUserLogout() {
        changeMainView(new AuthFragment(), false);
        app.setMenuLogged(false);
    }

    /**
     * Instantiate the view.
     * Set the current menu set.
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        app = (ApplicationTool) getApplicationContext();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        Menu menu = navigationView.getMenu();
        app.setMenu(menu);
        loadDefaultView();
    }

    /**
     * Check if the current user state and show the appropriate view.
     * If logged => Default logged view.
     * If not logged => Login view.
     */
    public void loadDefaultView() {
        Fragment fragment;

        if (app.isUserlogged()) {
            onUserLogged();
        } else {
            onUserLogout();
        }

        addHamburgerIcon();
    }

    /**
     * Change the current fragment view.
     *
     * @param newFragment
     * @param addToBackStack
     */
    public void changeMainView(Fragment newFragment, Boolean addToBackStack) {
        String newFragmentClassName = newFragment.getClass().getName();

        if (!Objects.equals(app.currentView, newFragmentClassName)) {
            app.currentView = newFragmentClassName;


            android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.frame_mainUI, newFragment, "mainUI");

            if (addToBackStack) {
                fragmentTransaction.addToBackStack(null);
            }

            fragmentTransaction.commit();
        }
    }

    /**
     * Handle left menu item click.
     *
     * @param item
     * @return
     */
    public boolean onNavigationItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_auth:

                RegisteredSpyFragment fragment1 = new RegisteredSpyFragment();
                changeMainView(fragment1, false);
                break;
            case R.id.nav_map:
                MapCurrentSpyFragment fragment2 = new MapCurrentSpyFragment();
                changeMainView(fragment2, true);
                break;
            case R.id.nav_log_out:
                onUserLogout();
                break;
            default:
                // --> ?.
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * Here we prevent application closing if there
     * is none back parent when we click on the back button.
     *
     */
    @Override
    public void onBackPressed() {
        android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
        int nbEntry = fragmentManager.getBackStackEntryCount();

        if (nbEntry != 0) {
            super.onBackPressed();
        }
    }

    /**
     * Add the hamburger icon and show the left menu.
     *
     */
    public void addHamburgerIcon() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
    }

    /**
     * Fragment callback method.
     * @param item
     */
    @Override
    public void onListFragmentInteraction(PositionModel item) {

    }

    /**
     * Fragment callback method.
     *
     * @param uri
     */
    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    /**
     * Close the virtual keyboard.
     */
    private void closeKeyboard() {
        InputMethodManager inputManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }
}