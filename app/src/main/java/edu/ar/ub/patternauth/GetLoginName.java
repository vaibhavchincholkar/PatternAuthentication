package edu.ar.ub.patternauth;

import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
/**
* GetLoginName activity takes PlayeName from the user and checks if that username exists in the database
* if username exists in the database it redirects user to the Login activity
* else it gives and error popup to the user.
* */
public class GetLoginName extends AppCompatActivity {
    Button confrim;
    EditText playerName;
    Uri muri;
    private ContentResolver PatternAuthResolver = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_login_name);
        Uri.Builder uriBuilder = new Uri.Builder();
        uriBuilder.scheme("content");
        uriBuilder.authority("edu.ar.ub.patternauth.provider");
        uriBuilder.appendPath(DBContract.TABLE_NAME);
        muri=uriBuilder.build();
        PatternAuthResolver=getContentResolver();
        confrim=findViewById(R.id.confrim);
        playerName=findViewById(R.id.playerName);
        confrim.setOnClickListener(v -> {
            String name= String.valueOf(playerName.getText());
            Log.d("Player name",""+name);
            if(checkIfUserNameAvailable(name))
            {
                Intent register = new Intent(this, Login.class);
                register.putExtra("PLAYERNAME",name);
                startActivity(register);
            }
            else
            {
                new AlertDialog.Builder(this)
                        .setTitle("Player Name does NOT exists")
                        .setMessage("Please enter the correct player name..")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }

        });
    }
    /**
     * checkIfUserNameAvailable function takes PlayerName as a parameter and checks if the provided name is in the DataBase or not
     * if Database contains the PlayerName it returns true
     * else it returns false.
     * */
    private boolean checkIfUserNameAvailable(String givenName)
    {
        Cursor UsernameCursor=PatternAuthResolver.query(muri,null,"username",null,null);
        while (UsernameCursor.moveToNext())
        {
            String PlayerName=UsernameCursor.getString(UsernameCursor.getColumnIndex(DBContract.USERNAME));
            if(PlayerName.equalsIgnoreCase(givenName))
            {
                return true;
            }
        }
        return false;
    }
}
