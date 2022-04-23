package com.muhibbin.memes;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.appupdate.testing.FakeAppUpdateManager;
import com.google.android.play.core.install.InstallState;
import com.google.android.play.core.install.InstallStateUpdatedListener;
import com.google.android.play.core.tasks.OnSuccessListener;
import com.google.android.play.core.tasks.Task;
import com.muhibbin.memes.photoEditorMainClasses.PhotoEditorActivity;

import static com.google.android.play.core.install.model.AppUpdateType.IMMEDIATE;
import static com.google.android.play.core.install.model.InstallStatus.DOWNLOADED;
import static com.google.android.play.core.install.model.UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS;
import static com.google.android.play.core.install.model.UpdateAvailability.UPDATE_AVAILABLE;

public class SplashActivity extends AppCompatActivity {

    ImageView imageView;
    AppUpdateManager appUpdateManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        imageView = findViewById(R.id.iconImageView);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        checkUpdate();

        Animation animation = AnimationUtils.loadAnimation(this, R.anim.splash_transition);
        imageView.setAnimation(animation);


        final Intent intent = new Intent(this, MainActivity.class);
        Thread timer = new Thread() {
            public void run() {
                try {

                    sleep(3000);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {

                    startActivity(intent);
                    finish();
                }

            }
        };
        timer.start();
    }

    private void checkUpdate() {
        Log.d("TAG", "onActivityResult: 11");
        appUpdateManager = AppUpdateManagerFactory.create(SplashActivity.this);
        Task<AppUpdateInfo> appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();

        appUpdateInfoTask.addOnSuccessListener(new OnSuccessListener<AppUpdateInfo>() {
            @Override
            public void onSuccess(AppUpdateInfo appUpdateInfo) {
                Log.d("TAG", "onActivityResult: 1");
                if ((appUpdateInfo.updateAvailability() == UPDATE_AVAILABLE)
                        // For a flexible update, use AppUpdateType.FLEXIBLE
                        && appUpdateInfo.isUpdateTypeAllowed(IMMEDIATE)) {
                    // Request the update.
                    Log.d("TAG", "onActivityResult: 2");

                    try {
                        Log.d("TAG", "onActivityResult: 3");
                        appUpdateManager.startUpdateFlowForResult(
                                // Pass the intent that is returned by 'getAppUpdateInfo()'.
                                appUpdateInfo,
                                // Or 'AppUpdateType.FLEXIBLE' for flexible updates.
                                IMMEDIATE,
                                // The current activity making the update request.
                                SplashActivity.this,
                                // Include a request code to later monitor this update request.
                                111);
                    } catch (IntentSender.SendIntentException ignored) {
                        Log.d("TAG", "onActivityResult: 4");
                    }
                } else {
                    Log.d("TAG", "onActivityResult: 12");
                }
            }
        });
        appUpdateManager.registerListener(installStateUpdatedListener);
    }

    InstallStateUpdatedListener installStateUpdatedListener = new InstallStateUpdatedListener() {
        @Override
        public void onStateUpdate(InstallState installState) {
            if (installState.installStatus() == DOWNLOADED) {
                Log.d("TAG", "onActivityResult: 10");
                SplashActivity.this.popupSnackbarForCompleteUpdate();
            } else
                Log.e("UPDATE", "Not downloaded yet");
        }
    };


    private void popupSnackbarForCompleteUpdate() {

        Snackbar snackbar =
                Snackbar.make(
                        findViewById(android.R.id.content),
                        "Update almost finished!",
                        Snackbar.LENGTH_INDEFINITE);
        //lambda operation used for below action
        snackbar.setAction(this.getString(R.string.restart), new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                appUpdateManager.completeUpdate();
            }
        });
        snackbar.setActionTextColor(getResources().getColor(R.color.white));
        snackbar.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 111) {
            if (resultCode != RESULT_OK) {
                checkUpdate();
                Log.d("TAG", "onActivityResult: 6");
            } else {
                Log.d("TAG", "onActivityResult: 5");
            }
        }
    }

    protected void onResume() {
        super.onResume();
        if (appUpdateManager != null) {
            appUpdateManager.getAppUpdateInfo()
                    .addOnSuccessListener(
                            new OnSuccessListener<AppUpdateInfo>() {
                                @Override
                                public void onSuccess(AppUpdateInfo appUpdateInfo) {
                                    Log.d("TAG", "onActivityResult: 9");
                                    if (appUpdateInfo.updateAvailability()
                                            == DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
                                        // If an in-app update is already running, resume the update.
                                        try {
                                            appUpdateManager.startUpdateFlowForResult(
                                                    appUpdateInfo,
                                                    IMMEDIATE,
                                                    SplashActivity.this,
                                                    111);
                                        } catch (IntentSender.SendIntentException e) {
                                            Log.d("TAG", "onActivityResult: 15");
                                        }
                                        Log.d("TAG", "onActivityResult: 7");
                                    }
                                }
                            });

        }
    }
}
