package de.querra.mobile.piraoke.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import java.io.IOException;
import java.util.Locale;

import de.querra.mobile.piraoke.R;
import de.querra.mobile.piraoke.callbacks.OnErrorCallback;
import de.querra.mobile.piraoke.callbacks.OnSuccessCallback;
import de.querra.mobile.piraoke.services.HttpServiceFactory;
import de.querra.mobile.piraoke.services.PreferencesServiceFactory;

/**
 * A login screen that offers login via email/password.
 */
public class PiraokeRegisterActivity extends AppCompatActivity {

    // UI references.
    private EditText piraokeIp;
    private View progressView;
    private View loginFormView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_piraoke_register);

        this.piraokeIp = (EditText) findViewById(R.id.piraoke_ip);

        Button registerButton = (Button) findViewById(R.id.register_button);
        registerButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        this.loginFormView = findViewById(R.id.login_form);
        this.progressView = findViewById(R.id.login_progress);
    }

    private void attemptLogin() {

        this.piraokeIp.setError(null);

        final String ip = this.piraokeIp.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(ip) && !isIpValid(ip)) {
            this.piraokeIp.setError("This is not an IP format. IP should for example look like this:\n 192.168.0.23");
            focusView = this.piraokeIp;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            showProgress(true);
            HttpServiceFactory.getInstance().connect(ip, new OnSuccessCallback() {
                        @Override
                        public void onSuccess() {
                            new Handler(Looper.getMainLooper()).post(
                                    new Runnable() {
                                        @Override
                                        public void run() {
                                            showProgress(false);
                                            PreferencesServiceFactory.getInstance().storePiraokeIp(ip);
                                            startActivity(new Intent(PiraokeRegisterActivity.this, MainActivity.class));
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
                                            showProgress(false);
                                            new AlertDialog.Builder(PiraokeRegisterActivity.this)
                                                    .setTitle("An error occurred") // TODO: show user only error that pi is not reachable
                                                    .setMessage(String.format(Locale.getDefault(), "Error: '%s'", exception.getMessage()))
                                                    .show();
                                        }
                                    }
                            );
                        }
                    });
        }
    }

    private boolean isIpValid(String ip) {
        return ip != null && ip.matches("([0-9]{1,3}\\.){3}[0-9]{1,3}");
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            this.loginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            this.loginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    loginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            this.progressView.setVisibility(show ? View.VISIBLE : View.GONE);
            this.progressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    progressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            this.progressView.setVisibility(show ? View.VISIBLE : View.GONE);
            this.loginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }
}

