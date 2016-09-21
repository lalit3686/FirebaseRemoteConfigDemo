package com.app.firebase.remote.config.demo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import de.greenrobot.event.EventBus;

/**
 * Created by Lalit.Poptani on 9/20/2016.
 */

public class SecondActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.second_activity);
    }

    public void handleClick(View view){
        switch (view.getId()){
            case R.id.button:
                EventBus.getDefault().post("Hello!");
                EventBus.getDefault().post(new Integer(100));
                finish();
                break;
        }
    }

}
