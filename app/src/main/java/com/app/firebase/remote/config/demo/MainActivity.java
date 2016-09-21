package com.app.firebase.remote.config.demo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;

import static android.widget.Toast.LENGTH_SHORT;
import static android.widget.Toast.makeText;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private FirebaseRemoteConfig mFirebaseRemoteConfig;
    private TextView textViewWelcomeMessage;
    private static final String WELCOME_MESSAGE_STRING_KEY = "welcome_message_string";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textViewWelcomeMessage = (TextView) findViewById(R.id.textViewWelcomeMessage);

        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setDeveloperModeEnabled(true)
                .build();
        mFirebaseRemoteConfig.setConfigSettings(configSettings);
        mFirebaseRemoteConfig.setDefaults(R.xml.remote_config_defaults);
        updateMessage("Value from Firebase - "+mFirebaseRemoteConfig.getString(WELCOME_MESSAGE_STRING_KEY));

        registerEventBus();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unRegisterEventBus();
    }

    private void registerEventBus(){
        EventBus.getDefault().register(this);
    }

    private void unRegisterEventBus(){
        EventBus.getDefault().unregister(this);
    }

    @Subscribe
    public void handleEvent(Object value){

        if(value instanceof String){
            Log.e("MainActivity", "string event handled "+value);
        }
        else if(value instanceof Integer){
            Log.e("MainActivity", "integer event handled "+value);
        }

        updateMessage("Event from EventBus - "+value);
    }

    private void updateMessage(String message){
        textViewWelcomeMessage.setText(message);
    }

    public void handleClick(View view){
        switch (view.getId()){
            case R.id.button:
                startActivity(new Intent(view.getContext(), SecondActivity.class));
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        mFirebaseRemoteConfig.fetch(0)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(Task<Void> task) {
                        if (task.isSuccessful()) {
                            makeText(MainActivity.this, "Fetch Succeeded",
                                    LENGTH_SHORT).show();

                            // Once the config is successfully fetched it must be activated before newly fetched
                            // values are returned.
                            mFirebaseRemoteConfig.activateFetched();
                        } else {
                            makeText(MainActivity.this, "Fetch Failed",
                                    LENGTH_SHORT).show();
                        }

                        updateMessage("Value from Firebase - "+mFirebaseRemoteConfig.getString(WELCOME_MESSAGE_STRING_KEY));
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                Log.e(TAG, e.toString());
            }
        });
    }
}
