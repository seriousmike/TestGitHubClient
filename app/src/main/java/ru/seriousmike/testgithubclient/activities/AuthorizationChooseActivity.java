package ru.seriousmike.testgithubclient.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import ru.seriousmike.testgithubclient.R;

public class AuthorizationChooseActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authorization_choose);

        findViewById(R.id.btnPersonal).setOnClickListener( new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent i = new Intent(AuthorizationChooseActivity.this, LoginActivity.class);
                startActivity(i);
            }
        } );


        findViewById(R.id.btnWebflow).setOnClickListener( new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent i = new Intent(AuthorizationChooseActivity.this, OauthWebFlowActivity.class);
                startActivity(i);
            }
        } );
    }
}
