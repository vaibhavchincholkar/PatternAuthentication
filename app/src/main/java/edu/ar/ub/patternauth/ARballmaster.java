package edu.ar.ub.patternauth;

import android.content.ContentResolver;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.ar.core.Anchor;
import com.google.ar.core.Config;
import com.google.ar.core.Frame;
import com.google.ar.core.Plane;
import com.google.ar.core.Pose;
import com.google.ar.core.TrackingState;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.Scene;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.Color;
import com.google.ar.sceneform.rendering.Material;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.rendering.ViewRenderable;
import com.google.ar.sceneform.ux.ArFragment;

import java.util.Random;
/*
* ARballmaster is the game Activity
*This is a AR game wherein user has to tap balls to gain score.
*If user misses a ball then score is decreased by one.
*If user score goes below -1 then game is over.
* */
public class ARballmaster extends AppCompatActivity {
    private ArFragment arFragment;
    private Anchor anchor;
    private AnchorNode anchorNode;
    private Node  replay, play;
    private Scene arSceneView;
    private ModelRenderable ball1,ball2,ball3,ball4,blackBall;
    private boolean objAdded=false;
    private Node obj1,obj2,obj3,obj4;
    private Node gameOver;
    private Vector3 orignalpose;
    private int step1=0;
    private int step2=0;
    private int step3=0;
    private int step4=0;
    private int step5=0;
    private int score=0;
    private boolean over=false;
    private boolean start=false;
    private ImageButton close;
    private MediaPlayer bubble,gmover;
    private TextView Curr_score;
    private int myHighestScore=0;
    private int allHighestScore=0;
    Uri muri,scoreUri;
    ContentResolver PatternAuthResolver=null;
    String PlayerName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_arballmaster);
        arFragment = (ArFragment) getSupportFragmentManager().findFragmentById(R.id.ux_fragment);
        arSceneView = arFragment.getArSceneView().getScene();
        PlayerName= getIntent().getStringExtra("PLAYERNAME");
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

        //myHighestScore=getMyScore();
       // allHighestScore=getAllHighScore();

        Toast toast2 =
                Toast.makeText(this, PlayerName+"'s highest score:"+myHighestScore, Toast.LENGTH_LONG);
        toast2.setGravity(Gravity.CENTER, 0, 0);
        toast2.show();

        play = new Node();
        replay = new Node();
        obj1= new Node();
        obj2= new Node();
        obj3= new Node();
        obj4= new Node();
        gameOver= new Node();
        bubble = MediaPlayer.create(this, R.raw.bubble);
        gmover = MediaPlayer.create(this, R.raw.gmover);

        Curr_score =  findViewById(R.id.score);
        Curr_score.setVisibility(View.INVISIBLE);
        close =  findViewById(R.id.close_button);
        close.setOnClickListener(v -> {
            finish();
            System.exit(0);
        });
        ViewRenderable.builder()
                .setView(this, R.layout.replay_view)
                .build()
                .thenAccept(
                        (renderable) -> {
                            replay.setRenderable(renderable);
                        })
                .exceptionally(
                        (throwable) -> {
                            throw new AssertionError("Could not load plane card view.", throwable);
                        });
        ViewRenderable.builder()
                .setView(this, R.layout.play_view)
                .build()
                .thenAccept(
                        (renderable) -> {
                            play.setRenderable(renderable);
                        })
                .exceptionally(
                        (throwable) -> {
                            throw new AssertionError("Could not load plane card view.", throwable);
                        });


        ModelRenderable.builder()
                .setSource(this, R.raw.ball2)
                .build()
                .thenAccept(renderable -> ball1 = renderable)
                .exceptionally(
                        throwable -> {
                            Toast toast =
                                    Toast.makeText(this, "Unable to load andy renderable", Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                            return null;
                        });
        ModelRenderable.builder()
                .setSource(this, R.raw.ball2)
                .build()
                .thenAccept(renderable -> ball2 = renderable)
                .exceptionally(
                        throwable -> {
                            Toast toast =
                                    Toast.makeText(this, "Unable to load andy renderable", Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                            return null;
                        });
        ModelRenderable.builder()
                .setSource(this, R.raw.ball2)
                .build()
                .thenAccept(renderable -> ball3 = renderable)
                .exceptionally(
                        throwable -> {
                            Toast toast =
                                    Toast.makeText(this, "Unable to load andy renderable", Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                            return null;
                        });
        ModelRenderable.builder()
                .setSource(this, R.raw.ball2)
                .build()
                .thenAccept(renderable -> ball4 = renderable)
                .exceptionally(
                        throwable -> {
                            Toast toast =
                                    Toast.makeText(this, "Unable to load andy renderable", Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                            return null;
                        });

        ModelRenderable.builder()
                .setSource(this, R.raw.gameover)
                .build()
                .thenAccept(renderable -> blackBall = renderable)
                .exceptionally(
                        throwable -> {
                            Toast toast =
                                    Toast.makeText(this, "Unable to load andy renderable", Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                            return null;
                        });

        arSceneView
                .addOnUpdateListener(
                        frameTime -> {

                            Frame frame = arFragment.getArSceneView().getArFrame();
                            if (frame == null) {
                                return;
                            }

                            if (frame.getCamera().getTrackingState() != TrackingState.TRACKING) {
                                return;
                            }
                            if(objAdded&&!over)//animation
                            {
                                Vector3 currPose = obj1.getWorldPosition();
                                Vector3 up = new Vector3(currPose.x,currPose.y+0.015f,currPose.z);
                                obj1.setWorldPosition(up);

                                currPose = obj2.getWorldPosition();
                                up = new Vector3(currPose.x,currPose.y+0.008f,currPose.z);
                                obj2.setWorldPosition(up);

                                currPose = obj3.getWorldPosition();
                                up = new Vector3(currPose.x,currPose.y+0.014f,currPose.z);
                                obj3.setWorldPosition(up);

                                currPose = obj4.getWorldPosition();
                                up = new Vector3(currPose.x,currPose.y+0.010f,currPose.z);
                                obj4.setWorldPosition(up);

                                currPose = gameOver.getWorldPosition();
                                up = new Vector3(currPose.x,currPose.y+0.010f,currPose.z);
                                gameOver.setWorldPosition(up);

                                if(step1>=200)
                                {
                                    decScore();
                                    setToOrignal(obj1);//set object back to orignal position
                                }

                                if(step2>=200)
                                {
                                    decScore();
                                    setToOrignal(obj2);//set object back to orignal position
                                }
                                if(step3>=200)
                                {
                                    decScore();
                                    setToOrignal(obj3);//set object back to orignal position
                                }
                                if(step4>=200)
                                {
                                    decScore();
                                    setToOrignal(obj4);//set object back to orignal position
                                }
                                if(step5>=300)
                                {
                                    // decScore();
                                    setToOrignal(gameOver);//set object back to orignal position
                                }
                                step1++;
                                step2++;
                                step3++;
                                step4++;
                                step5++;
                            }

                            for (Plane plane : frame.getUpdatedTrackables(Plane.class)) {
                                if (plane.getTrackingState() == TrackingState.TRACKING) {
                                    //as soon as planeis detected add everything
                                    Pose pose = plane.getCenterPose();
                                    // Anchor anchor = plane.createAnchor(pose);
                                    // AnchorNode anchorNode = new AnchorNode(anchor);
                                    if(start==false)
                                    {

                                        play.setLocalPosition(new Vector3(0.0f, 0.0f, -1.0f));
                                        arSceneView.getCamera().addChild(play);
                                        play.setEnabled(true);
                                        play.setOnTapListener((hitTestResult, motionEvent) -> {
                                            start=true;
                                            play.setEnabled(false); });
                                    }
                                    if(score<-1)
                                    {
                                        score=0;
                                        gmover.start();
                                        gameOver();
                                    }
                                    if(!objAdded && start==true)
                                    {
                                        objAdded=true;
                                        Config config = arFragment.getArSceneView().getSession().getConfig();
                                        config.setCloudAnchorMode(Config.CloudAnchorMode.ENABLED);
                                        arFragment.getArSceneView().getSession().configure(config);
                                        anchor = arFragment.getArSceneView().getSession().hostCloudAnchor(plane.createAnchor(pose));
                                        anchorNode = new AnchorNode(anchor);

                                        Anchor.CloudAnchorState cloudState = anchor.getCloudAnchorState();
                                        // Toast.makeText(this, "Now hosting"+cloudState, Toast.LENGTH_LONG).show();

                                        Curr_score.setVisibility(View.VISIBLE);
                                        obj1.setRenderable(ball1);
                                        obj2.setRenderable(ball2);
                                        obj2.setLocalPosition(new Vector3(0.9f, 0.0f, 0.0f));
                                        obj3.setRenderable(ball3);
                                        obj3.setLocalPosition(new Vector3(0.1f, 0.0f, 0.4f));
                                        obj4.setRenderable(ball4);
                                        obj4.setLocalPosition(new Vector3(1.1f, 0.0f, 0.6f));

                                        Material black=blackBall.getMaterial();
                                        black.setFloat3("baseColorTint",0,0,0);//setting it to black color
                                        blackBall.setMaterial(black);
                                        gameOver.setRenderable(blackBall);
                                        gameOver.setLocalPosition(new Vector3(0.3f, 0.0f, 0.8f));

                                        orignalpose= anchorNode.getWorldPosition();
                                        anchorNode.setParent(arFragment.getArSceneView().getScene());
                                        anchorNode.addChild(obj1);
                                        anchorNode.addChild(obj2);
                                        anchorNode.addChild(obj3);
                                        anchorNode.addChild(obj4);
                                        anchorNode.addChild(gameOver);
                                        gameOver.setEnabled(false);
                                        setToOrignal(gameOver);

                                        replay.setLocalPosition(new Vector3(0.0f, 0.0f, -1.0f));
                                        arSceneView.getCamera().addChild(replay);
                                        replay.setEnabled(false);

                                        obj1.setOnTapListener((hitTestResult, motionEvent) -> {
                                            setToOrignal(obj1);
                                            addScore();
                                        });
                                        obj2.setOnTapListener((hitTestResult, motionEvent) -> {
                                            setToOrignal(obj2);
                                            addScore();
                                        });
                                        obj3.setOnTapListener((hitTestResult, motionEvent) -> {
                                            setToOrignal(obj3);
                                            addScore();
                                        });
                                        obj4.setOnTapListener((hitTestResult, motionEvent) -> {
                                            setToOrignal(obj4);
                                            addScore();
                                        });
                                        gameOver.setOnTapListener((hitTestResult, motionEvent) -> {
                                            gmover.start();
                                            gameOver();
                                        });
                                        replay.setOnTapListener((hitTestResult, motionEvent) -> {
                                            gamereplay();
                                        });
                                    }
                                }
                            }
                        });
    }

    /*
    * setToOrignal methods changes the objects y positions so that they go back to surface
    * also it provides random delay for the reappearance
    * */
    void setToOrignal(Node obj)
    {
        if(obj==obj1)
        {
            step1=0;
            obj1.setEnabled(false);
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {

                    Material changedMaterial = ball1.getMaterial();
                    Random newColor = new Random();
                    Random newPose = new Random();
                    changedMaterial.setFloat3("baseColorTint", new Color(newColor.nextInt(100)+20,newColor.nextInt(150)+100,newColor.nextInt(10)+10));
                    ball1.setMaterial(changedMaterial);
                    float nwx= (float) newPose.nextInt(350)-350;
                    nwx=nwx/100;
                    obj1.setWorldPosition(new Vector3(orignalpose.x+nwx, orignalpose.y+0.0f, orignalpose.z+0.3f));
                    obj1.setEnabled(true);
                }
            }, 2000);
        }
        if(obj==obj2)
        {   step2=0;
            obj2.setEnabled(false);
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Material changedMaterial = ball2.getMaterial();
                    Random newColor = new Random();
                    changedMaterial.setFloat3("baseColorTint", new Color(newColor.nextInt(155)+100,newColor.nextInt(10),newColor.nextInt(100)));
                    ball2.setMaterial(changedMaterial);
                    Random newPose = new Random();
                    float nwx= (float) newPose.nextInt(350)-350;
                    nwx=nwx/100;
                    obj2.setWorldPosition(new Vector3(orignalpose.x+nwx, orignalpose.y+0.0f, orignalpose.z+0.3f));
                    obj2.setEnabled(true);
                }
            }, 1000);
        }
        if(obj==obj3)
        {   step3=0;
            obj3.setEnabled(false);
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Material changedMaterial = ball3.getMaterial();
                    Random newColor = new Random();
                    changedMaterial.setFloat3("baseColorTint", new Color(newColor.nextInt(10),newColor.nextInt(10),newColor.nextInt(150)+100));
                    ball3.setMaterial(changedMaterial);
                    Random newPose = new Random();
                    float nwx= (float) newPose.nextInt(350)-200;
                    nwx=nwx/100;
                    obj3.setWorldPosition(new Vector3(orignalpose.x+0.1f, orignalpose.y+0.0f, orignalpose.z+nwx));
                    obj3.setEnabled(true);
                }
            }, 1500);
        }

        if(obj==obj4)
        {   step4=0;
            obj4.setEnabled(false);
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Material changedMaterial = ball4.getMaterial();
                    Random newColor = new Random();
                    changedMaterial.setFloat3("baseColorTint", new Color(newColor.nextInt(10),newColor.nextInt(10),newColor.nextInt(150)+100));
                    ball4.setMaterial(changedMaterial);
                    Random newPose = new Random();
                    float nwx= (float) newPose.nextInt(250);
                    nwx=nwx/100;
                    obj4.setWorldPosition(new Vector3(orignalpose.x+nwx, orignalpose.y+0.0f, orignalpose.z+nwx));
                    obj4.setEnabled(true);
                }
            }, 1700);
        }
        if(obj==gameOver)
        {

            step5 = 0;
            gameOver.setEnabled(false);
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Random newPose = new Random();
                    float nwx= (float) newPose.nextInt(250);
                    nwx=nwx/100;
                    gameOver.setWorldPosition(new Vector3(orignalpose.x+nwx, orignalpose.y+0.0f, orignalpose.z+nwx));
                    gameOver.setEnabled(true);
                }
            }, 3000);
        }
    }
    /*
    * addScore method add the score the current score of the user*/
    void addScore()
    {
        bubble.start();
        score++;
        Curr_score.setText("Score : "+score);
    }
    int getMyScore()
    {
        int sc=0;
        Cursor score=PatternAuthResolver.query(scoreUri,null,PlayerName,null,null);
        while (score.moveToNext())
        {
         Log.d("Score",""+score.getString(score.getColumnIndex(DBContract.SCORE)));
        }
        return sc;
    }
    int getAllHighScore()
    {
        Cursor score=PatternAuthResolver.query(scoreUri,null,null,null,null);
        while (score.moveToNext())
        {
            return score.getInt(score.getColumnIndex(DBContract.SCORE));
        }
        return 0;
    }

    void storeHighScore()
    {

    }



    void decScore()
    {
        score=score-1;
        Curr_score.setText("Score : "+score);
    }
    void gameOver()
    {
        replay.setEnabled(true);
        over=true;
        obj1.setEnabled(false);
        obj1.setEnabled(false);
        obj1.setEnabled(false);
        obj1.setEnabled(false);
        gameOver.setEnabled(false);
    }
    /*
     * gamereokay resets the game paramter to the start
     * */
    void  gamereplay()
    {
        score=0;
        Curr_score.setText("Score : "+score);
        setToOrignal(obj1);
        setToOrignal(obj2);
        setToOrignal(obj3);
        setToOrignal(obj4);
        setToOrignal(gameOver);
        replay.setEnabled(false);
        over=false;
    }
}
