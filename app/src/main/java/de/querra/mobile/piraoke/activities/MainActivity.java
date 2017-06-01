package de.querra.mobile.piraoke.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.io.IOException;
import java.util.Locale;

import de.querra.mobile.piraoke.R;
import de.querra.mobile.piraoke.adapters.SectionsPagerAdapter;
import de.querra.mobile.piraoke.callbacks.OnErrorCallback;
import de.querra.mobile.piraoke.callbacks.OnSuccessCallback;
import de.querra.mobile.piraoke.data_adapters.YTVideo;
import de.querra.mobile.piraoke.fragments.YTVideoFragment;
import de.querra.mobile.piraoke.services.HttpServiceFactory;

public class MainActivity extends AppCompatActivity implements YTVideoFragment.OnListFragmentInteractionListener {

    private SectionsPagerAdapter mSectionsPagerAdapter;

    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onListFragmentInteraction(final YTVideo ytVideo) {
        HttpServiceFactory.getInstance().playVideo(ytVideo.getVideoId(), new OnSuccessCallback() {
                    @Override
                    public void onSuccess() {
                        new Handler(Looper.getMainLooper()).post(
                                new Runnable() {
                                    @Override
                                    public void run() {
                                        new AlertDialog.Builder(MainActivity.this)
                                                .setTitle("Video playing")
                                                .setMessage(String.format(Locale.getDefault(), "Video '%s' is playing.", ytVideo.getTitle()))
                                                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                                HttpServiceFactory.getInstance().cancelPlayback(
                                                                        ytVideo.getVideoId(),
                                                                        new OnSuccessCallback() {
                                                                            @Override
                                                                            public void onSuccess() {
                                                                                new Handler(Looper.getMainLooper()).post(
                                                                                        new Runnable() {
                                                                                            @Override
                                                                                            public void run() {
                                                                                                Toast.makeText(MainActivity.this, "Playback canceled", Toast.LENGTH_SHORT).show();
                                                                                            }
                                                                                        }
                                                                                );
                                                                            }
                                                                        },
                                                                        new OnErrorCallback() {
                                                                            @Override
                                                                            public void onError(final IOException exception) {
                                                                                new Handler(Looper.getMainLooper()).post(
                                                                                        new Runnable() {
                                                                                            @Override
                                                                                            public void run() {
                                                                                                Toast.makeText(MainActivity.this, "Error while canceling playback: "+exception.getMessage(), Toast.LENGTH_SHORT).show();
                                                                                            }
                                                                                        }
                                                                                );
                                                                            }
                                                                        }

                                                                );
                                                            }
                                                        }

                                                )
                                                .setCancelable(false)
                                                .show();
                                    }
                                }

                        );
                    }
                },
                new OnErrorCallback() {
                    @Override
                    public void onError(final IOException exception) {
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                new AlertDialog.Builder(MainActivity.this)
                                        .setTitle("Error playing video")
                                        .setMessage(String.format(Locale.getDefault(), "Could not play video '%s':\n%s", ytVideo.getTitle(), exception.getMessage()))
                                        .setPositiveButton("Ok", null)
                                        .show();
                            }
                        });
                    }
                }

        );
    }
}
