package edu.ar.ub.patternauth;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.widget.Button;
import android.widget.Toast;

import com.google.ar.core.Anchor;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.Scene;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.ux.ArFragment;

import java.util.ArrayList;
import java.util.List;
/**
* Login Activity takes PlayerName from the Intent by which it was called.(i.e. GetLoginName activity)
* It has ARfragment in it, which allows player to create pattern on the surface
* on the submission it verifies if the entered pattern is correct or not
* if it is correct it redirects user to the ARBALLMASTER game.
*
* */
public class Login extends AppCompatActivity {
    private ArFragment arFragment;
    private Scene arSceneView;
    Button submit,reset;
    private ModelRenderable ballobject;
    Float currentdistance, storeddistance;
    /**
     * currpositions list stores the AnchorNodes of objects put by the player and storedPositions has coordinates of stored position from the database*/
    private List<AnchorNode> currpositions =new ArrayList<>();
    private List<NodePosition> storedPositions =new ArrayList<>();
    private ContentResolver PatternAuthResolver = null;
    Uri muri,scoreUri;
    String PlayerName="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //Build main URI
        Uri.Builder uriBuilder = new Uri.Builder();
        uriBuilder.scheme("content");
        uriBuilder.authority("edu.ar.ub.patternauth.provider");
        uriBuilder.appendPath(DBContract.TABLE_NAME);
        muri=uriBuilder.build();

        //build score URI
        Uri.Builder uriBuilder2 = new Uri.Builder();
        uriBuilder2.scheme("content");
        uriBuilder2.authority("edu.ar.ub.patternauth.provider");
        uriBuilder2.appendPath(DBContract.SCORE_TABLE);
        scoreUri=uriBuilder2.build();

        PatternAuthResolver=getContentResolver();
        PlayerName= getIntent().getStringExtra("PLAYERNAME");
        //PatternAuthResolver.delete(muri,null,null);
        arFragment = (ArFragment) getSupportFragmentManager().findFragmentById(R.id.ux_fragment);
        arSceneView = arFragment.getArSceneView().getScene();
        submit=findViewById(R.id.submit);
        reset=findViewById(R.id.reset);

        submit.setOnClickListener(v -> {
            storedPositions.clear();
            if(verifyThePassword())
            {

                Toast toast = Toast.makeText(this, "Welcome "+ PlayerName, Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();

                Intent ToTheGame = new Intent(this, ARballmaster.class);
                ToTheGame.putExtra("PLAYERNAME",PlayerName);
                ToTheGame.putExtra("PLAYERSCORE",getPlayerScore());
                startActivity(ToTheGame);
            }
            else{
                Toast toast2 = Toast.makeText(this, "Wrong password", Toast.LENGTH_LONG);
                toast2.setGravity(Gravity.CENTER, 0, 0);
                toast2.show();
            }

        });
        reset.setOnClickListener(v -> {
            for(int i=0;i<currpositions.size();i++)
            {
                arSceneView.removeChild(currpositions.get(i));
            }
            currpositions.clear();
        });

        ModelRenderable.builder()
                .setSource(this, R.raw.ball)
                .build()
                .thenAccept(renderable -> ballobject = renderable)
                .exceptionally(
                        throwable -> {
                            Toast toast2 = Toast.makeText(this, "Unable to load ballobject renderable", Toast.LENGTH_LONG);
                            toast2.setGravity(Gravity.CENTER, 0, 0);
                            toast2.show();
                            return null;
                        });

        arFragment.setOnTapArPlaneListener((hitResult, plane, motionEvent) -> {
            if (ballobject == null) {
                Toast toast2 = Toast.makeText(this, "Ball object is not set yet", Toast.LENGTH_LONG);
                toast2.setGravity(Gravity.CENTER, 0, 0);
                toast2.show();
                return;
            }
            Anchor anchor = hitResult.createAnchor();
            AnchorNode anchorNode = new AnchorNode(anchor);
            currpositions.add(anchorNode);
            anchorNode.setParent(arFragment.getArSceneView().getScene());
            Node placeholder = new Node();
            placeholder.setRenderable(ballobject);
            anchorNode.addChild(placeholder);
        });
    }

    /**
     * verifyThePassword method uses Global variable PlayerName which gets initialized when activity is called. it gets the PlayerName from the intent.
     * verifyThePassword method takes the all the node coordinates from the database and puts it into a list and calls the checkdifference method
     * if checkdiffrence returns true it returns true and vice a versa.
     * */
    private boolean verifyThePassword()
    {
            Cursor UserPattern=PatternAuthResolver.query(muri,null,PlayerName,null,null);
             Float sx0,sy0;
            storedPositions.clear();
            Log.d("size",""+UserPattern.getCount()+" = "+storedPositions.size());
            if(UserPattern.getCount()==currpositions.size())
            {
                while (UserPattern.moveToNext())
                {
                    sx0=UserPattern.getFloat(UserPattern.getColumnIndex(DBContract.XCORD));
                    sy0=UserPattern.getFloat(UserPattern.getColumnIndex(DBContract.YCORD));
                    NodePosition np= new NodePosition(sx0,sy0);
                    storedPositions.add(np);
                }
                if(checkdifference())
                {
                    Log.d("Verifying","Success it is"+PlayerName);
                    return true;
                }
            }

        return false;
    }

    /**
    * checkdifferce method verifies the difference between x and y coordinates of the provided objects and stored objects.
    * it makes uses of two lists one is currpositions which has the current object provided by the user and storedlist which has
    * stored objects by the user
    * if the object difference between current objects and stored objects are less than 12 then the checkdifference returns true else false.
    * */
    private boolean checkdifference()
    {
        Float x0,y0,x1,y1,sx0,sy0,sx1,sy1;
        for(int i=0;i<currpositions.size()-1;i++)
        {
            //current position under check
            x0=currpositions.get(i).getWorldPosition().x;
            y0=currpositions.get(i).getWorldPosition().y;
            sx0=storedPositions.get(i).x;
            sy0=storedPositions.get(i).y;
            Log.d("Verifying","x0:"+x0+" y0:"+y0+" Sx0:"+sx0+" Sy0:"+sy0);
            for(int j=0;j<currpositions.size();j++)
            {
                x1=currpositions.get(j).getWorldPosition().x;
                y1=currpositions.get(j).getWorldPosition().y;
                sx1=storedPositions.get(j).x;
                sy1=storedPositions.get(j).y;
                Float disx=x0-x1;
                Float disy=y0-y1;
                Float sdisx=sx0-sx1;
                Float sdisy=sy0-sy1;
                Log.d("Verifying","x1:"+x1+" y1"+y1+" Sx1:"+sx1+" Sy1:"+sy1);
                currentdistance = (float) Math.sqrt(disx*disx + disy*disy);
                storeddistance = (float) Math.sqrt(sdisx*sdisx + sdisy*sdisy);
                currentdistance=currentdistance*100;
                Log.d("Verifying","curr distance "+currentdistance);
                Log.d("Verifying","stored distance "+storeddistance);
                storeddistance=storeddistance*100;
                Float diff=currentdistance-storeddistance;
                Math.abs(diff);
                Log.d("Verifying","diff "+diff);
                if(diff>15)
                {
                    return false;
                }
            }
        }
        return true;
    }

    public int getPlayerScore()
    {
        int sc=0;
        Cursor score=PatternAuthResolver.query(scoreUri,null,PlayerName,null,null);
        if(score==null)
        {
            Log.d("Score","null error");
        }
        else
        {
            while (score.moveToNext())
            {
                sc=score.getInt(score.getColumnIndex(DBContract.SCORE));
                Log.d("Score",""+sc);
            }
        }
        return sc;
    }
}
