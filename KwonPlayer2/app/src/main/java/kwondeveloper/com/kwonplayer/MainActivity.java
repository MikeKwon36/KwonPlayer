package kwondeveloper.com.kwonplayer;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.File;
import kwondeveloper.com.kwonplayer.AsyncTasks.AsyncBuildSongLibrary;
import kwondeveloper.com.kwonplayer.Fragments.AlbumLibraryFragment;
import kwondeveloper.com.kwonplayer.Fragments.EqualizerFragment;
import kwondeveloper.com.kwonplayer.Fragments.PlaylistFragment;
import kwondeveloper.com.kwonplayer.Fragments.SongLibraryFragment;
import kwondeveloper.com.kwonplayer.Fragments.PlayerFragment;
import kwondeveloper.com.kwonplayer.Playlists.AllSongsOnDevice;
import kwondeveloper.com.kwonplayer.Playlists.CurrentPlaylist;
import kwondeveloper.com.kwonplayer.Playlists.PlaylistDBhelper;
import kwondeveloper.com.kwonplayer.SupportClasses.CustomDialogAdapter;
import kwondeveloper.com.kwonplayer.SupportClasses.WallpaperMethods;

public class MainActivity extends AppCompatActivity implements
        GoogleApiClient.OnConnectionFailedListener,
        NavigationView.OnNavigationItemSelectedListener,
        PlayerFragment.PlayerFragmentInteractionListener,
        SongLibraryFragment.LibraryFragmentInteractionListener,
        PlaylistFragment.PlaylistFragmentInteractionListener {

    private Toolbar mToolBar;
    private ActionBar mActionBar;
    private NavigationView mNavigationView;
    private DrawerLayout mDrawer;
    private ActionBarDrawerToggle mToggle;
    private FragmentManager mFragmentManager;
    private SongLibraryFragment mLibraryFragment;
    private PlayerFragment mPlayerFragment;
    private PlaylistFragment mPlaylistFragment;
    private AdView mAdView;
    private ImageView mMainActivityBackground;
    private PlaylistDBhelper mDBhelper;
    private SharedPreferences mSharedPreferences;
    private String mImageFullPathAndName = "";
    private String mLocalImagePath = "";
    private String mDefaultBackground;
    public static String mPlaylistTitleHolder;

    //Google API variables
    private GoogleApiClient mGoogleApiClient;
    private Tracker mTracker;
    private ConnectivityManager mConnectivityManager;
    private NetworkInfo mNetworkInfo;

    //keys
    private static final String SHARED_PREFS_BACKGROUND = "com.kwondeveloper.kwonplayer.background";
    private static final String CAMERA_INTENT = "android.media.action.IMAGE_CAPTURE";
    private static final String THEME_DEFAULT = "KwonPlayer Default";
    private static final String THEME_RED = "Panchee Punch";
    private static final String THEME_PURPLE = "Cureton Purple";
    private static final String THEME_GREEN = "Adi Green";
    private static final String THEME_ORANGE = "Tygret Stripes";
    private static final String THEME_GREY = "Steely Davis";
    private static final String THEME_YELLOW = "Ambient Arndt";
    private static final String THEME_SPADE = "Spades Mahoney";
    private static final int SELECT_PICTURE = 1;
    private static final int TAKE_PICTURE = 2;
    private static final int OPTIMIZED_LENGTH = 1024;
    public static final String[] dataPerms = {"android.permission.WRITE_EXTERNAL_STORAGE", "android.permission.READ_EXTERNAL_STORAGE"};
    public static final String[] cameraPerms = {"android.permission.CAMERA"};
    public static final int dataRequestCode = 111;
    public static final int cameraRequestCode = 222;
    public static final int photoGalleryRequestCode = 333;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //build internal song library
        Log.d("***MainOnCreate", "instantiates song library & playlist");
        AllSongsOnDevice.getInstance();
        CurrentPlaylist.getInstance();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M || ContextCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.READ_EXTERNAL_STORAGE)) {
                Toast.makeText(MainActivity.this, getString(R.string.MainActivity_StoragePermission), Toast.LENGTH_SHORT).show();
            } else {
                ActivityCompat.requestPermissions(this,dataPerms, dataRequestCode);
            }
        } else {
            AsyncBuildSongLibrary loadSongs = new AsyncBuildSongLibrary();
            loadSongs.execute(this);
        }

        //Instantiate Main Activity Member Variables
        Log.d("***MainOnCreate", "instantiates MainActivity member variables & fragments");
        mNavigationView = (NavigationView) findViewById(R.id.nav_view);
        mMainActivityBackground = (ImageView) findViewById(R.id.mainActivityBackground);
        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        mToolBar = (Toolbar) findViewById(R.id.toolbar);
        mAdView = (AdView)findViewById(R.id.adView);
        mFragmentManager = getSupportFragmentManager();
        mDefaultBackground = getFilesDir().getAbsolutePath() + "/kwon/";
        mLocalImagePath = WallpaperMethods.createLocalImageFolder(this, mLocalImagePath);
        mDBhelper = PlaylistDBhelper.getInstance(this);
        mLibraryFragment = SongLibraryFragment.newInstance();
        mPlayerFragment = PlayerFragment.newInstance();
        mPlaylistFragment = PlaylistFragment.newInstance();

        //Set Action Bar
        mToolBar.setTitle(R.string.blank_string);
        setSupportActionBar(mToolBar);
        mActionBar = getSupportActionBar();
        mActionBar.setDisplayHomeAsUpEnabled(true);
        mActionBar.setDisplayShowHomeEnabled(true);

        //Set default background
        mSharedPreferences = getSharedPreferences(SHARED_PREFS_BACKGROUND,MODE_PRIVATE);
        if(mSharedPreferences.getString(SHARED_PREFS_BACKGROUND, null) == null){
            Picasso.with(this).load(mDefaultBackground).fit().into(mMainActivityBackground);
        } else {
            switch (mSharedPreferences.getString(SHARED_PREFS_BACKGROUND, null)){
                case THEME_DEFAULT:
                    mToolBar.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                    Picasso.with(MainActivity.this).load(R.drawable.background_white).fit().into(mMainActivityBackground);
                    break;
                case THEME_RED:
                    mToolBar.setBackgroundColor(getResources().getColor(android.R.color.holo_red_dark));
                    Picasso.with(MainActivity.this).load(R.drawable.background_red).fit().into(mMainActivityBackground);
                    break;
                case THEME_PURPLE:
                    mToolBar.setBackgroundColor(getResources().getColor(android.R.color.holo_purple));
                    Picasso.with(MainActivity.this).load(R.drawable.background_purple).fit().into(mMainActivityBackground);
                    break;
                case THEME_GREEN:
                    mToolBar.setBackgroundColor(getResources().getColor(android.R.color.holo_green_dark));
                    Picasso.with(MainActivity.this).load(R.drawable.background_green).fit().into(mMainActivityBackground);
                    break;
                case THEME_ORANGE:
                    mToolBar.setBackgroundColor(getResources().getColor(android.R.color.holo_orange_dark));
                    Picasso.with(MainActivity.this).load(R.drawable.background_orange).fit().into(mMainActivityBackground);
                    break;
                case THEME_GREY:
                    mToolBar.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
                    Picasso.with(MainActivity.this).load(R.drawable.background_grey).fit().into(mMainActivityBackground);
                    break;
                case THEME_YELLOW:
                    mToolBar.setBackgroundColor(getResources().getColor(R.color.colorYellow));
                    Picasso.with(MainActivity.this).load(R.drawable.background_yellow).fit().into(mMainActivityBackground);
                    break;
                case THEME_SPADE:
                    mToolBar.setBackgroundColor(getResources().getColor(R.color.colorDarkBlue));
                    Picasso.with(MainActivity.this).load(R.drawable.background_darkblue).fit().into(mMainActivityBackground);
                    break;
                default:
                    Picasso.with(this).load(new File(mSharedPreferences.getString(SHARED_PREFS_BACKGROUND, null))).fit().centerCrop().into(mMainActivityBackground);
                    break;
            }
        }

        //Set Drawer Toggle functionality
        mToggle = new ActionBarDrawerToggle(this, mDrawer, mToolBar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawer.setDrawerListener(mToggle);
        mToggle.syncState();
        mNavigationView.setNavigationItemSelectedListener(this);

        //Initialize all fragments to execute their onCreate statements, preventing null pointers on the views
        Log.d("***MainOnCreate", "instantiates library&player fragments and then places player fragment on screen");
        FragmentTransaction trans1 = mFragmentManager.beginTransaction();
        trans1.replace(R.id.mainFragContainer, mLibraryFragment).commit();
        FragmentTransaction trans2 = mFragmentManager.beginTransaction();
        trans2.replace(R.id.mainFragContainer, mPlaylistFragment).commit();
        FragmentTransaction trans3 = mFragmentManager.beginTransaction();
        trans3.replace(R.id.mainFragContainer, mPlayerFragment).commit();
        mNavigationView.setCheckedItem(R.id.nav_currentlyPlaying);

        /*mGoogleApiClient = new GoogleApiClient.Builder(this).enableAutoManage(this,this).addApi(Analytics.API).addScope(Drive.SCOPE_FILE).build();*/
        mConnectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
        if(mNetworkInfo != null && mNetworkInfo.isConnected()){
            //Google Analytics
            AnalyticsApplication application = (AnalyticsApplication) getApplication();
            mTracker = application.getDefaultTracker();
            mTracker.setScreenName(getResources().getString(R.string.analytics_main_activity));
            mTracker.send(new HitBuilders.ScreenViewBuilder().build());

            //Google AdMob
            AdRequest request = new AdRequest.Builder()
                    .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                    .addTestDevice("F831379B962C9646106F0722BF511E61")
                    .build();
            mAdView.loadAd(request);
        }
    }

    @Override
    public void onBackPressed() {
        if (mDrawer.isDrawerOpen(GravityCompat.START)) {
            mDrawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onResume() {
        if (mAdView != null) {
            mAdView.resume();
        }
        super.onResume();
    }

    @Override
    protected void onPause() {
        if (mAdView != null) {
            mAdView.pause();
        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        //Save any song changes made to CurrentPlaylist in the the playlist database
        Log.d("***PlaylistFragDialog", "CurrentPlaylist saved to database");
        if(mPlaylistTitleHolder==null){
            mDBhelper.updatePlaylistInDB(CurrentPlaylist.getPlaylist(), CurrentPlaylist.getPlaylistTitle());
        } else {
            mDBhelper.updatePlaylistInDB(CurrentPlaylist.getPlaylist(), mPlaylistTitleHolder);
        }

        if (mAdView != null) {
            mAdView.destroy();
        }

        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_themes:
                //Dialog to select different themes
                launchThemeAlertDialog();

                //Google Analytics
                if(mNetworkInfo != null && mNetworkInfo.isConnected()){
                    mTracker.send(new HitBuilders.EventBuilder()
                            .setCategory(getResources().getString(R.string.analytics_main_options_menu))
                            .setAction(getResources().getString(R.string.analytics_main_options_menu_themes))
                            .build());
                }
                return true;
            case R.id.action_background:
                launchWallpaperAlertDialog();

                //Google Analytics
                if(mNetworkInfo != null && mNetworkInfo.isConnected()){
                    mTracker.send(new HitBuilders.EventBuilder()
                            .setCategory(getResources().getString(R.string.analytics_main_options_menu))
                            .setAction(getResources().getString(R.string.analytics_main_options_menu_background))
                            .build());
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        //Google Analytics
        if(mNetworkInfo != null && mNetworkInfo.isConnected()){
            mTracker.send(new HitBuilders.EventBuilder()
                    .setCategory(getResources().getString(R.string.analytics_main_navigation_menu))
                    .setAction(getResources().getString(R.string.analytics_main_navigation_menu_ad))
                    .build());
        }

        switch (item.getItemId()){
            case R.id.nav_currentlyPlaying:
                FragmentTransaction trans = mFragmentManager.beginTransaction();
                trans.replace(R.id.mainFragContainer, mPlayerFragment).commit();
                mNavigationView.setCheckedItem(R.id.nav_currentlyPlaying);
                Log.d("***MainActivity", "Player Fragment selected in Main Activity drawer");
                break;
            case R.id.nav_playlists:
                FragmentTransaction trans2 = mFragmentManager.beginTransaction();
                trans2.replace(R.id.mainFragContainer, mPlaylistFragment).commit();
                mNavigationView.setCheckedItem(R.id.nav_playlists);
                Log.d("***MainActivity", "Playlist Fragment selected in Main Activity drawer");
                break;
            case R.id.nav_browseSongsOnDevice:
                FragmentTransaction trans3 = mFragmentManager.beginTransaction();
                trans3.replace(R.id.mainFragContainer, mLibraryFragment).commit();
                mNavigationView.setCheckedItem(R.id.nav_currentlyPlaying);
                Log.d("***MainActivity", "Song Library Fragment selected in Main Activity drawer");
                break;
            case R.id.nav_browseAlbumArtistGenre:
                FragmentTransaction trans4 = mFragmentManager.beginTransaction();
                AlbumLibraryFragment albumLibraryFragment = AlbumLibraryFragment.newInstance();
                trans4.replace(R.id.mainFragContainer, albumLibraryFragment).commit();
                mNavigationView.setCheckedItem(R.id.nav_browseAlbumArtistGenre);
                Log.d("***MainActivity", "Album/Artist/Genre Library Fragment selected in Main Activity drawer");
                break;
            case R.id.nav_equalizer:
                FragmentTransaction trans5 = mFragmentManager.beginTransaction();
                EqualizerFragment equalizerFragment = EqualizerFragment.newInstance();
                trans5.replace(R.id.mainFragContainer, equalizerFragment).commit();
                mNavigationView.setCheckedItem(R.id.nav_equalizer);
                Log.d("***MainActivity", "Equalizer Fragment selected selected in Main Activity drawer");
                break;
            case R.id.nav_photos:
                //will eventually launch photoGallery fragment, but redirects to player for now
                FragmentTransaction trans6 = mFragmentManager.beginTransaction();
                trans6.replace(R.id.mainFragContainer, mPlayerFragment).commit();
                mNavigationView.setCheckedItem(R.id.nav_currentlyPlaying);
                Toast.makeText(MainActivity.this, getString(R.string.drawer_browse_photos_notice), Toast.LENGTH_SHORT).show();
                Log.d("***MainActivity", "photoGallery Fragment selected selected in Main Activity drawer - redirected to Player");
                break;
            case R.id.nav_videos:
                //will eventually launch video fragment, but redirects to player for now
                FragmentTransaction trans7 = mFragmentManager.beginTransaction();
                trans7.replace(R.id.mainFragContainer, mPlayerFragment).commit();
                mNavigationView.setCheckedItem(R.id.nav_currentlyPlaying);
                Toast.makeText(MainActivity.this, getString(R.string.drawer_browse_videos_notice), Toast.LENGTH_SHORT).show();
                Log.d("***MainActivity", "Video Fragment selected selected in Main Activity drawer - redirected to Player");
                break;
        }

        mDrawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onPlaylistFragmentInteraction() {
        FragmentTransaction trans = mFragmentManager.beginTransaction();
        trans.replace(R.id.mainFragContainer, mPlayerFragment).commit();
        mNavigationView.setCheckedItem(R.id.nav_currentlyPlaying);
    }

    @Override
    public void onPlayerFragmentInteraction() {
        //code to tie Main Activity to PlayerFragment
    }

    @Override
    public void onLibraryFragmentInteraction() {
        FragmentTransaction trans = mFragmentManager.beginTransaction();
        trans.replace(R.id.mainFragContainer, mPlayerFragment).commit();
        mNavigationView.setCheckedItem(R.id.nav_currentlyPlaying);
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    private void launchThemeAlertDialog(){
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View view = layoutInflater.inflate(R.layout.dialog_theme, null);
        final AlertDialog themeDialog = new AlertDialog.Builder(this).create();
        ListView list = (ListView) view.findViewById(R.id.themeListView);
        final String[] themes = getResources().getStringArray(R.array.Themes);
        CustomDialogAdapter adapter = new CustomDialogAdapter(this,themes);
        list.setAdapter(adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SharedPreferences.Editor editor = mSharedPreferences.edit();
                switch (themes[position]){
                    case THEME_DEFAULT:
                        mToolBar.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                        Picasso.with(MainActivity.this).load(R.drawable.background_white).fit().into(mMainActivityBackground);
                        editor.putString(SHARED_PREFS_BACKGROUND, THEME_DEFAULT);
                        editor.commit();
                        break;
                    case THEME_RED:
                        mToolBar.setBackgroundColor(getResources().getColor(android.R.color.holo_red_dark));
                        Picasso.with(MainActivity.this).load(R.drawable.background_red).fit().into(mMainActivityBackground);
                        editor.putString(SHARED_PREFS_BACKGROUND, THEME_RED);
                        editor.commit();
                        break;
                    case THEME_PURPLE:
                        mToolBar.setBackgroundColor(getResources().getColor(android.R.color.holo_purple));
                        Picasso.with(MainActivity.this).load(R.drawable.background_purple).fit().into(mMainActivityBackground);
                        editor.putString(SHARED_PREFS_BACKGROUND, THEME_PURPLE);
                        editor.commit();
                        break;
                    case THEME_GREEN:
                        mToolBar.setBackgroundColor(getResources().getColor(android.R.color.holo_green_dark));
                        Picasso.with(MainActivity.this).load(R.drawable.background_green).fit().into(mMainActivityBackground);
                        editor.putString(SHARED_PREFS_BACKGROUND, THEME_GREEN);
                        editor.commit();
                        break;
                    case THEME_ORANGE:
                        mToolBar.setBackgroundColor(getResources().getColor(android.R.color.holo_orange_dark));
                        Picasso.with(MainActivity.this).load(R.drawable.background_orange).fit().into(mMainActivityBackground);
                        editor.putString(SHARED_PREFS_BACKGROUND, THEME_ORANGE);
                        editor.commit();
                        break;
                    case THEME_GREY:
                        mToolBar.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
                        Picasso.with(MainActivity.this).load(R.drawable.background_grey).fit().into(mMainActivityBackground);
                        editor.putString(SHARED_PREFS_BACKGROUND, THEME_GREY);
                        editor.commit();
                        break;
                    case THEME_YELLOW:
                        mToolBar.setBackgroundColor(getResources().getColor(R.color.colorYellow));
                        Picasso.with(MainActivity.this).load(R.drawable.background_yellow).fit().into(mMainActivityBackground);
                        editor.putString(SHARED_PREFS_BACKGROUND, THEME_YELLOW);
                        editor.commit();
                        break;
                    case THEME_SPADE:
                        mToolBar.setBackgroundColor(getResources().getColor(R.color.colorDarkBlue));
                        Picasso.with(MainActivity.this).load(R.drawable.background_darkblue).fit().into(mMainActivityBackground);
                        editor.putString(SHARED_PREFS_BACKGROUND, THEME_SPADE);
                        editor.commit();
                        break;
                }
            }
        });
        themeDialog.setView(view);
        themeDialog.show();
    }

    public static void launchAboutAlertDialog(View view){
        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
        builder.setIcon(R.drawable.kwondeveloper)
                .setTitle(R.string.AboutTitle)
                .setMessage(R.string.AboutContent)
                .create().show();
    }

    private void launchWallpaperAlertDialog(){
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View view = layoutInflater.inflate(R.layout.dialog_wallpaper, null);
        final AlertDialog alertD = new AlertDialog.Builder(this).create();
        ImageButton wallpaperTakePhoto = (ImageButton) view.findViewById(R.id.wallpaperTakePhoto);
        ImageButton wallpaperChoosePhoto = (ImageButton) view.findViewById(R.id.wallpaperChoosePhoto);
        ImageButton wallpaperReset = (ImageButton) view.findViewById(R.id.wallpaperReset);

        wallpaperTakePhoto.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(MainActivity.this,Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,Manifest.permission.CAMERA)) {
                        Toast.makeText(MainActivity.this, getString(R.string.MainActivity_CameraPermission), Toast.LENGTH_SHORT).show();
                    } else {
                        ActivityCompat.requestPermissions(MainActivity.this,cameraPerms, cameraRequestCode);
                    }
                } else {
                    Intent intent = new Intent(CAMERA_INTENT);
                    startActivityForResult(intent, TAKE_PICTURE);
                }
                alertD.dismiss();
            }
        });

        wallpaperChoosePhoto.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(MainActivity.this,Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,Manifest.permission.READ_EXTERNAL_STORAGE)) {
                        Toast.makeText(MainActivity.this, getString(R.string.MainActivity_photoGalleryPermission), Toast.LENGTH_SHORT).show();
                    } else {
                        ActivityCompat.requestPermissions(MainActivity.this,dataPerms, photoGalleryRequestCode);
                    }
                } else {
                    Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, SELECT_PICTURE);
                }
                alertD.dismiss();
            }
        });

        wallpaperReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertD.dismiss();
                SharedPreferences.Editor editor = mSharedPreferences.edit();
                editor.putString(SHARED_PREFS_BACKGROUND,mDefaultBackground);
                editor.commit();
                Picasso.with(getApplicationContext()).load(mDefaultBackground).into(mMainActivityBackground);
            }
        });
        alertD.setView(view);
        alertD.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SELECT_PICTURE || requestCode == TAKE_PICTURE) {
            Log.d("***MainActivity", "OnResult - intent request code received = " + requestCode);
            if (resultCode == RESULT_OK && null != data) {
                Uri selectedImage = data.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};

                Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                mImageFullPathAndName = cursor.getString(columnIndex);
                cursor.close();
                File file = new File(mImageFullPathAndName);
                Bitmap mCurrentSelectedBitmap = WallpaperMethods.decodeFile(file);

                if (mCurrentSelectedBitmap != null) {
                    // display the full size image
                    // ivSelectedImg.setImageBitmap(mCurrentSelectedBitmap);
                    // scale the image, check the resolution of the image, and if it's too large, optimize it
                    int w = mCurrentSelectedBitmap.getWidth();
                    int h = mCurrentSelectedBitmap.getHeight();
                    int length = (w > h) ? w : h;
                    if (length > OPTIMIZED_LENGTH) {
                        // resize the image
                        float ratio = (float) w / h;
                        int newW, newH = 0;
                        if (ratio > 1.0) {
                            newW = OPTIMIZED_LENGTH;
                            newH = (int) (OPTIMIZED_LENGTH / ratio);
                        } else {
                            newH = OPTIMIZED_LENGTH;
                            newW = (int) (OPTIMIZED_LENGTH * ratio);
                        }
                        mCurrentSelectedBitmap = WallpaperMethods.rescaleBitmap(mCurrentSelectedBitmap, newW, newH);
                    }
                    if(mCurrentSelectedBitmap.getWidth() > mCurrentSelectedBitmap.getHeight() || requestCode == TAKE_PICTURE){
                        mCurrentSelectedBitmap = WallpaperMethods.rotateBitmap(mCurrentSelectedBitmap, 90);
                    }
                }

                // save the new image to our local folder
                mImageFullPathAndName = WallpaperMethods.SaveImage(mCurrentSelectedBitmap, mLocalImagePath);
                Log.d("***MainActivity", "OnResult - mImageFullPathAndName set to = " + mImageFullPathAndName);
                SharedPreferences.Editor editor = mSharedPreferences.edit();
                editor.putString(SHARED_PREFS_BACKGROUND, mImageFullPathAndName);
                editor.commit();

                int currentapiVersion = android.os.Build.VERSION.SDK_INT;
                if (currentapiVersion >= Build.VERSION_CODES.M){
                    // For Marshmallow and above versions
                    Picasso.with(this).load(new File(mImageFullPathAndName)).into(mMainActivityBackground, new Callback() {
                        @Override
                        public void onSuccess() {
                            mMainActivityBackground.setScaleType(ImageView.ScaleType.CENTER_CROP);//Or ScaleType.FIT_CENTER
                        }

                        @Override
                        public void onError() {}
                    });
                } else{
                    // For phones running an SDK before Marshmallow
                    mMainActivityBackground.setImageBitmap(mCurrentSelectedBitmap);
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int permsRequestCode, String[] permissions, int[] grantResults){
        switch(permsRequestCode){
            case dataRequestCode:
                boolean dataPermissionAccepted = grantResults[0]== PackageManager.PERMISSION_GRANTED;
                if(dataPermissionAccepted){
                    AsyncBuildSongLibrary loadSongs = new AsyncBuildSongLibrary();
                    loadSongs.execute(this);
                    Log.d("***MainActivityPerm", "Permission received to access data");
                }
                break;
            case cameraRequestCode:
                boolean cameraPermissionAccepted = grantResults[0]== PackageManager.PERMISSION_GRANTED;
                if(cameraPermissionAccepted){
                    Intent intent = new Intent(CAMERA_INTENT);
                    startActivityForResult(intent, TAKE_PICTURE);
                    Log.d("***MainActivityPerm", "Permission received to access camera");
                }
                break;
            case photoGalleryRequestCode:
                boolean photoGalleryPermissionAccepted = grantResults[0]== PackageManager.PERMISSION_GRANTED;
                if(photoGalleryPermissionAccepted){
                    Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, SELECT_PICTURE);
                    Log.d("***MainActivityPerm", "Permission received to access photos");
                }
                break;
        }
    }
}