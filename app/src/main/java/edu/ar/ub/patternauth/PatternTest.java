package edu.ar.ub.patternauth;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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
/*
* patternTest activity is use to make sure that user can reproduce the given pattern,
* it allows user to enter the pattern it entered in the Signup activity
* user can enter the pattern as many times as he wants
* once user gets the enough confidence with his pattern he can store it*/
public class PatternTest extends AppCompatActivity {
    private ArFragment arFragment;
    private Scene arSceneView;
    Button store,reset,test;
    private ModelRenderable ballobject;
    private List<AnchorNode> testPositions=new ArrayList<>();
    Float currentdistance, storeddistance;
    private ContentResolver PatternAuthResolver = null;
    Uri muri,scoreUri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pattern_test);
        Uri.Builder uriBuilder = new Uri.Builder();
        uriBuilder.scheme("content");
        uriBuilder.authority("edu.ar.ub.patternauth.provider");
        muri=uriBuilder.build();

        Uri.Builder uriBuilder2 = new Uri.Builder();
        uriBuilder2.scheme("content");
        uriBuilder2.authority("edu.ar.ub.patternauth.provider");
        uriBuilder2.appendEncodedPath(DBContract.SCORE_TABLE);
        scoreUri=uriBuilder2.build();
        PatternAuthResolver=getContentResolver();
        Toast toast = Toast.makeText(this, "No of objects "+Signup.storedPositions.size(), Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
        String name= getIntent().getStringExtra("PLAYER_NAME");
        arFragment = (ArFragment) getSupportFragmentManager().findFragmentById(R.id.ux_fragment);
        arSceneView = arFragment.getArSceneView().getScene();
        store=findViewById(R.id.confirm);
        reset=findViewById(R.id.reset);

        test=findViewById(R.id.test);

        store.setOnClickListener(v -> {

            for(int i=0;i<Signup.storedPositions.size();i++)
            {   ContentValues userPattern = new ContentValues();
                userPattern.put(DBContract.USERNAME,name);
                userPattern.put(DBContract.XCORD,Signup.storedPositions.get(i).getWorldPosition().x);
                userPattern.put(DBContract.YCORD,Signup.storedPositions.get(i).getWorldPosition().y);
                PatternAuthResolver.insert(muri,userPattern);
                userPattern.clear();
            }

            //now create new entry in userScore Table and set user score to zero
            ContentValues userScore = new ContentValues();
            userScore.put(DBContract.UNAME,name);
            userScore.put(DBContract.SCORE,0);
            PatternAuthResolver.insert(scoreUri,userScore);

            Intent registered = new Intent(this, MainActivity.class);
            startActivity(registered);
        });
        reset.setOnClickListener(v -> {
            for(int i=0;i<testPositions.size();i++)
            {
                arSceneView.removeChild(testPositions.get(i));
            }
            testPositions.clear();

        });
        test.setOnClickListener(v -> {
            if(verifyThePassword())
            {
                Toast toast2 = Toast.makeText(this, "Correct Password", Toast.LENGTH_LONG);
                toast2.setGravity(Gravity.CENTER, 0, 0);
                toast2.show();
            }
            else{
                Toast toast2 = Toast.makeText(this, "Incorrect Password", Toast.LENGTH_LONG);
                toast2.setGravity(Gravity.CENTER, 0, 0);
                toast2.show();
            }
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
            testPositions.add(anchorNode);
            anchorNode.setParent(arFragment.getArSceneView().getScene());
            Node placeholder = new Node();
            placeholder.setRenderable(ballobject);
            anchorNode.addChild(placeholder);
        });
    }

    /*
     * verifyThePassword method verifies the difference between x and y coordinates of the provided objects and stored objects.
     * it makes uses of two lists one is currpositions which has the current object provided by the user and storedlist which has
     * stored objects by the user
     * if the object difference between current objects and stored objects are less than 12 then the verifyThePassword returns true else false.
     * */
    private boolean verifyThePassword()
    {
        Float x0,y0,x1,y1,sx0,sy0,sx1,sy1;
        if(Signup.storedPositions.size()!=testPositions.size())
        {
            return false;
        }

        for(int i=0;i<Signup.storedPositions.size()-1;i++)
        {
            //current position under check
            x0=testPositions.get(i).getWorldPosition().x;
            y0=testPositions.get(i).getWorldPosition().y;
            sx0=Signup.storedPositions.get(i).getWorldPosition().x;
            sy0=Signup.storedPositions.get(i).getWorldPosition().y;

            for(int j=0;j<Signup.storedPositions.size();j++)
            {
                x1=testPositions.get(j).getWorldPosition().x;
                y1=testPositions.get(j).getWorldPosition().y;
                sx1=Signup.storedPositions.get(j).getWorldPosition().x;
                sy1=Signup.storedPositions.get(j).getWorldPosition().y;
                Float disx=x0-x1;
                Float disy=y0-y1;
                Float sdisx=sx0-sx1;
                Float sdisy=sy0-sy1;
                currentdistance = (float) Math.sqrt(disx*disx + disy*disy);
                storeddistance = (float) Math.sqrt(sdisx*sdisx + sdisy*sdisy);
                currentdistance=currentdistance*100;
                storeddistance=storeddistance*100;
                Float diff=currentdistance-storeddistance;
                Math.abs(diff);
                if(diff>12)
                {
                    return false;
                }
            }
        }
        return true;
    }


}
