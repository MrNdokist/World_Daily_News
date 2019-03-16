package com.mrndokist.app.worlddailynews.activities;

import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.telephony.PhoneNumberUtils;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.mrndokist.app.worlddailynews.R;
import com.mrndokist.app.worlddailynews.fragments.NewsFragment;
import com.mrndokist.app.worlddailynews.helper.Helper;

public class MainActivity extends AppCompatActivity {

    //Images social networks
    ImageView imgFacebook;
    ImageView imgTwitter;
    ImageView imgYoutube;
    private long exitTime = 0L;
    //Nav header
    ImageView imageViewProfil;
    TextView textName, textEmail;

    Dialog localDialog;

    // tags used to attach the fragments
    private static final String TAG_HOME = "News";
    public static String CURRENT_TAG = TAG_HOME;

    //Drawer composant
    Toolbar toolbar;
    private NavigationView navigationView;
    private DrawerLayout drawer;

    // flag to load home fragment when user presses back key
    private boolean shouldLoadHomeFragOnBackPress = true;

    // toolbar titles respected to selected nav menu item
    private String[] activityTitles;
    // index to identify current nav menu item
    public static int navItemIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        activityTitles = getResources().getStringArray(R.array.nav_item_activity_titles);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        initSocialBouton();
        initComposant();

        Typeface localTypeface1 = Typeface.createFromAsset(this.getAssets(), "fonts/OpenSans-Bold.ttf");



        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NewsFragment fragment = new NewsFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frag, fragment);
        fragmentTransaction.commit();

        //Click Bouton reseaux Sociaux
        //Gestion des click reseau sociaux
        //Facebook
        imgFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.this.startActivity(new Intent("android.intent.action.VIEW",
                        Uri.parse("https://www.facebook.com/michel.madzou?ref=bookmarks")));
            }
        });

        //Twitter
        imgTwitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "Twitter", Toast.LENGTH_SHORT).show();
            }
        });

        //Youtube
        imgYoutube.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.this.startActivity(new Intent("android.intent.action.VIEW",
                        Uri.parse("https://www.youtube.com/channel/UCN5zcMb5iidFYNKaQf5Q6SQ/featured?view_as=subscriber")));

            }
        });

        // initializing navigation menu
        setUpNavigationView();

        if (savedInstanceState == null) {
            navItemIndex = 0;
            CURRENT_TAG = TAG_HOME;
            loadHomeFragment();
        }
    }


    //methode qui charge le fragment categorie
    private void getMoveToFrag(Fragment fragment, String TAG) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frag, fragment, TAG);
        fragmentTransaction.commit();
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawers();
            return;
        }

        // This code loads home fragment when back key is pressed
        // when user is in other fragment than home
        if (shouldLoadHomeFragOnBackPress) {
            // checking if user is on other navigation menu
            // rather than home
            if (navItemIndex != 0) {
                navItemIndex = 0;
                CURRENT_TAG = TAG_HOME;
                loadHomeFragment();
                getMoveToFrag(new NewsFragment(), TAG_HOME);
                return;
            }
        }

        exitApp();
    }


    public void exitApp() {
        if (System.currentTimeMillis() - this.exitTime > 2000L) {
            Toast.makeText(this, R.string.toast_exit_message, Toast.LENGTH_SHORT).show();
            this.exitTime = System.currentTimeMillis();
            return;
        }
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        MenuItem shareItem = menu.findItem(R.id.action_share);
        ShareActionProvider myShareActionProvider =
                (ShareActionProvider) MenuItemCompat.getActionProvider(shareItem);

        Intent ishare = new Intent(Intent.ACTION_SEND);
        ishare.setType("text/plain");
        ishare.putExtra(Intent.EXTRA_TEXT, getString(R.string.message) + "\nhttp://play.google.com/store/apps/details?id=" + getPackageName());
        myShareActionProvider.setShareIntent(ishare);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_about) {
            aboutDialog();
            return true;
        }

        if (id == R.id.action_moreapps) {
            moreApplicatiions();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //Chargement du home
    private void loadHomeFragment() {
        // selecting appropriate nav menu item
        selectNavMenu();

        // set toolbar title
        setToolbarTitle();

        // if user select the current navigation menu again, don't do anything
        // just close the navigation drawer
        if (getSupportFragmentManager().findFragmentByTag(CURRENT_TAG) != null) {
            drawer.closeDrawers();

            return;
        }

        drawer.closeDrawers();

        // refresh toolbar menu
        invalidateOptionsMenu();
    }

    //Sous titres de la Toolbars
    private void setToolbarTitle() {
        //getSupportActionBar().setTitle(activityTitles[navItemIndex]);
        getSupportActionBar().setTitle(activityTitles[navItemIndex]);
    }

    private void selectNavMenu() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.CUPCAKE) {
            navigationView.getMenu().getItem(navItemIndex).setChecked(true);
        }
    }

    //Reglage Drawer
    private void setUpNavigationView() {
        //Setting Navigation View Item Selected Listener to handle the item click of the navigation menu
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            // This method will trigger on item Click of navigation menu
            @RequiresApi(api = Build.VERSION_CODES.DONUT)
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {

                //Check to see which item was being clicked and perform appropriate action
                switch (menuItem.getItemId()) {
                    //Replacing the main content with ContentFragment Which is our Inbox View;
                    case R.id.nav_news:
                        navItemIndex = 0;
                        getMoveToFrag(new NewsFragment(), TAG_HOME);
                        CURRENT_TAG = TAG_HOME;
                        break;
                    case R.id.nav_contact_us:
                        // launch new intent instead of loading fragment
                        sendWhatsapppMessage(Helper.NOTRE_NUMERO);
                        drawer.closeDrawers();
                        return true;

                    case R.id.nav_scode:
                        // get source code
                        MainActivity.this.startActivity(new Intent("android.intent.action.VIEW",
                                Uri.parse("https://github.com/MrNdokist/World-Daily-News")));
                        drawer.closeDrawers();
                        return true;
                    case R.id.nav_rate_us:

                        MainActivity.this.startActivity(new Intent("android.intent.action.VIEW",
                                Uri.parse("http://play.google.com/store/apps/details?id=" + getPackageName())));
                        drawer.closeDrawers();
                        return true;
                    case R.id.nav_share:
                        // launch new intent instead of loading fragment
                        shareApp();
                        drawer.closeDrawers();
                        return true;

                    case R.id.nav_info:
                        aboutDialog();
                        drawer.closeDrawers();
                        return true;
                    default:
                        navItemIndex = 0;
                }

                //Checking if the item is in checked state or not, if not make it in checked state
                if (menuItem.isChecked()) {
                    menuItem.setChecked(false);
                } else {
                    menuItem.setChecked(true);
                }
                menuItem.setChecked(true);

                loadHomeFragment();

                return true;
            }
        });


        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.openDrawer, R.string.closeDrawer) {

            @Override
            public void onDrawerClosed(View drawerView) {
                // Code here will be triggered once the drawer closes as we dont want anything to happen so we leave this blank
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                // Code here will be triggered once the drawer open as we dont want anything to happen so we leave this blank
                super.onDrawerOpened(drawerView);
            }
        };

        //Setting the actionbarToggle to drawer layout
        drawer.setDrawerListener(actionBarDrawerToggle);

        //calling sync state is necessary or else your hamburger icon wont show up
        actionBarDrawerToggle.syncState();
    }

    public void initSocialBouton() {
        imgFacebook = (ImageView) findViewById(R.id.img_facebook);
        imgTwitter = (ImageView) findViewById(R.id.img_twitter);
        imgYoutube = (ImageView) findViewById(R.id.img_youtube);
    }


    //Partage de l'application

    public void shareApp() {
        Intent ishare = new Intent(Intent.ACTION_SEND);
        ishare.setType("text/plain");
        ishare.putExtra(Intent.EXTRA_TEXT, getString(R.string.message) + "\nhttp://play.google.com/store/apps/details?id=" + getPackageName());
        startActivity(ishare);
    }

    public void moreApplicatiions() {
        MainActivity.this.startActivity(new Intent("android.intent.action.VIEW",
                Uri.parse("https://play.google.com/store/apps/developer?id=Madzou+Michel+Bercy")));
    }


    //Contact Us

    private String a(String paramString) {
        return PhoneNumberUtils.stripSeparators(paramString);
    }

    @RequiresApi(api = Build.VERSION_CODES.DONUT)
    private void sendWhatsapppMessage(String number) {
        try {
            String toNumber = number.replace("+", "").replace(" ", "");
            String str = a(toNumber) + "@s.whatsapp.net";
            Intent localIntent = new Intent("android.intent.action.SENDTO", Uri.parse("smsto:" + str));
            localIntent.putExtra("jid", str);
            localIntent.putExtra("sms_body", "");
            localIntent.setPackage("com.whatsapp");
            startActivity(localIntent);
            return;
        } catch (ActivityNotFoundException localActivityNotFoundException) {
            Toast.makeText(this, "Make Sure you have WhatsApp App Installed on Your Device", Toast.LENGTH_SHORT).show();
        }
    }

    //Dialogue about

    private void aboutDialog() {
        localDialog = new Dialog(this);
        localDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        localDialog.setContentView(R.layout.dialog_about);
        localDialog.setCancelable(false);
        localDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        WindowManager.LayoutParams localLayoutParams = new WindowManager.LayoutParams();
        localLayoutParams.copyFrom(localDialog.getWindow().getAttributes());
        localLayoutParams.width = -2;
        localLayoutParams.height = -2;
        Typeface localTypeface1 = Typeface.createFromAsset(this.getAssets(), "fonts/OpenSans-Bold.ttf");
        Typeface localTypeface2 = Typeface.createFromAsset(this.getAssets(), "fonts/OpenSans-Regular.ttf");
        ((TextView) localDialog.findViewById(R.id.about_text)).setTypeface(localTypeface1);
        ((TextView) localDialog.findViewById(R.id.tv_ver)).setTypeface(localTypeface2);
        ((TextView) localDialog.findViewById(R.id.da_msg)).setTypeface(localTypeface2);
        ((TextView) localDialog.findViewById(R.id.tv_version)).setTypeface(localTypeface2);

        localDialog.findViewById(R.id.bt_rateapp).setOnClickListener(new View.OnClickListener() {
            public void onClick(View paramView) {
                Intent localIntent = new Intent("android.intent.action.VIEW");
                localIntent.setData(Uri.parse("http://play.google.com/store/apps/details?id=" + getPackageName()));
                MainActivity.this.startActivity(localIntent);
            }
        });

        localDialog.findViewById(R.id.bt_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                localDialog.dismiss();
            }
        });

        localDialog.show();
        localDialog.getWindow().setAttributes(localLayoutParams);
    }

    private void initComposant() {
        imageViewProfil = findViewById(R.id.imageViewUser);
        textName = findViewById(R.id.textViewName);
        textEmail = findViewById(R.id.textViewEmail);


    }

}
