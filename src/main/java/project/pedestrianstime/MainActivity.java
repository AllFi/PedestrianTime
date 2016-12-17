package project.pedestrianstime;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.nfc.Tag;
import android.opengl.GLSurfaceView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.Camera2Renderer;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraGLRendererBase;
import org.opencv.android.CameraGLSurfaceView;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {

    private ImageButton mButtonGo = null;
    private ImageButton mButtonSettings = null;
    private String TAG = "main";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hello_activity);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        mButtonGo = (ImageButton) findViewById(R.id.button);
        mButtonGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent settings = getIntent();
                Intent intent = new Intent(MainActivity.this, DetectionActivity.class);
                String exist = settings.getStringExtra("exist");

                if (exist==null){
                    intent.putExtra("cascade", "2");
                    intent.putExtra("scaleFactor", "1.1");
                    intent.putExtra("minNeighbors","3");
                    intent.putExtra("FSC", "0");
                    intent.putExtra("min_width", "30");
                    intent.putExtra("min_height", "80");
                    intent.putExtra("max_width", "153");
                    intent.putExtra("max_height", "480");
                } else{
                    intent.putExtra("cascade", settings.getStringExtra("cascade"));
                    intent.putExtra("scaleFactor", settings.getStringExtra("scaleFactor"));
                    intent.putExtra("minNeighbors",settings.getStringExtra("minNeighbors"));
                    intent.putExtra("FSC",settings.getStringExtra("FSC"));
                    intent.putExtra("min_width",settings.getStringExtra("min_width"));
                    intent.putExtra("min_height",settings.getStringExtra("min_height"));
                    intent.putExtra("max_width",settings.getStringExtra("max_width"));
                    intent.putExtra("max_height",settings.getStringExtra("max_height"));
                }
                startActivity(intent);

            }
        });

        mButtonSettings = (ImageButton) findViewById(R.id.settings);
        mButtonSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                Intent settings = getIntent();
                String exist = settings.getStringExtra("exist");
                if (exist==null){
                    intent.putExtra("cascade", "2");
                    intent.putExtra("scaleFactor", "1.1");
                    intent.putExtra("minNeighbors","3");
                    intent.putExtra("FSC", "0");
                    intent.putExtra("min_width", "30");
                    intent.putExtra("min_height", "80");
                    intent.putExtra("max_width", "153");
                    intent.putExtra("max_height", "480");
                } else{
                    intent.putExtra("cascade", settings.getStringExtra("cascade"));
                    intent.putExtra("scaleFactor", settings.getStringExtra("scaleFactor"));
                    intent.putExtra("minNeighbors",settings.getStringExtra("minNeighbors"));
                    intent.putExtra("FSC",settings.getStringExtra("FSC"));
                    intent.putExtra("min_width",settings.getStringExtra("min_width"));
                    intent.putExtra("min_height",settings.getStringExtra("min_height"));
                    intent.putExtra("max_width",settings.getStringExtra("max_width"));
                    intent.putExtra("max_height",settings.getStringExtra("max_height"));
                }
                startActivity(intent);
            }
        });
    }


}
