package edu.ar.ub.patternauth;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
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
/**
 * Signup activity allows user to create pattern on the surface for authentication*/
public class Signup extends AppCompatActivity {
    private ArFragment arFragment;
    private Scene arSceneView;
    Button store,reset;
    private ModelRenderable ballobject;
    /**
     * positions list stores the anchor nodes entered by the Player, once the user confirms the pattern we copy the position list into storedPositions list*/
    public static List<AnchorNode> positions =new ArrayList<>();
    public static List<AnchorNode> storedPositions=new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        String name= getIntent().getStringExtra("PLAYER_NAME");
        arFragment = (ArFragment) getSupportFragmentManager().findFragmentById(R.id.ux_fragment);
        arSceneView = arFragment.getArSceneView().getScene();
        storedPositions.clear();
        store=findViewById(R.id.store);
        reset=findViewById(R.id.reset);
        store.setOnClickListener(v -> {
            storedPositions.addAll(positions);
            Intent test = new Intent(this, PatternTest.class);
            test.putExtra("PLAYER_NAME", name);
            startActivity(test);
        });
        reset.setOnClickListener(v -> {
           for(int i=0;i<positions.size();i++)
            {
                arSceneView.removeChild(positions.get(i));
            }
            positions.clear();

        });
        new AlertDialog.Builder(this)
                .setTitle("Create pattern on the surface")
                .setMessage("1. Move the device around to detect the surface \n\n 2. Tap on surface to put balls on the surface \n\n 3. Once confirm click on proceed \n\n **Click on RESET to clear the surface**")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .setIcon(android.R.drawable.star_big_on)
                .show();
        ModelRenderable.builder()
                .setSource(this, R.raw.ball)
                .build()
                .thenAccept(renderable -> ballobject = renderable)
                .exceptionally(
                        throwable -> {
                            Toast toast = Toast.makeText(this, "Unable to load ballobject renderable", Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                            return null;
                        });

        arFragment.setOnTapArPlaneListener((hitResult, plane, motionEvent) -> {
            if (ballobject == null) {
                Toast toast = Toast.makeText(this, "Ball object is not set yet", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                return;
            }
            Anchor anchor = hitResult.createAnchor();
            AnchorNode anchorNode = new AnchorNode(anchor);
            positions.add(anchorNode);
            anchorNode.setParent(arFragment.getArSceneView().getScene());
            Node placeholder = new Node();
            placeholder.setRenderable(ballobject);
            anchorNode.addChild(placeholder);
        });

    }
    @Override
    protected void onResume() {
        super.onResume();
        storedPositions.clear();
    }
}
