package edu.ar.ub.patternauth;

import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;

public class GetUserName extends AppCompatActivity {
    Button confrim;
    EditText playerName;
    Uri muri;
    private ContentResolver PatternAuthResolver = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_user_name);
        Uri.Builder uriBuilder = new Uri.Builder();
        uriBuilder.scheme("content");
        uriBuilder.authority("edu.ar.ub.patternauth.provider");
        muri=uriBuilder.build();
        PatternAuthResolver=getContentResolver();
        confrim=findViewById(R.id.confrim);
        playerName=findViewById(R.id.playerName);
        confrim.setOnClickListener(v -> {
            String name= String.valueOf(playerName.getText());
            if(checkIfUserNameAvailable(name))
            {
                Intent register = new Intent(this, Signup.class);
                register.putExtra("PLAYER_NAME", name);
                startActivity(register);
            }
            else
            {
                new AlertDialog.Builder(this)
                        .setTitle("Player Name Already exists")
                        .setMessage("Please try a different name...")
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
    private boolean checkIfUserNameAvailable(String givenName)
    {
        Cursor UsernameCursor=PatternAuthResolver.query(muri,null,"username",null,null);
        while (UsernameCursor.moveToNext())
        {
            String PlayerName=UsernameCursor.getString(UsernameCursor.getColumnIndex(DBContract.USERNAME));
            if(PlayerName.equalsIgnoreCase(givenName))
            {
               return false;
            }
        }
        return true;
    }

}
