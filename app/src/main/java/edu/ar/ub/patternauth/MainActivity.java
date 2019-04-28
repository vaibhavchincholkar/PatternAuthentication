package edu.ar.ub.patternauth;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    Button signup, login;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        signup =findViewById(R.id.signup);
        login=findViewById(R.id.login);
        login.setOnClickListener(v -> {
            Intent Login = new Intent(this, GetLoginName.class);
            startActivity(Login);
        });
        signup.setOnClickListener(v -> {
            Intent register = new Intent(this, GetUserName.class);
            startActivity(register);
        });

    }
}
