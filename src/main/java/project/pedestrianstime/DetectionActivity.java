package project.pedestrianstime;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.hardware.camera2.CameraManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.SurfaceView;
import android.view.WindowManager;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraGLRendererBase;
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

import project.pedestrianstime.R;

/**
 * Created by админ on 16.12.2016.
 */

public class DetectionActivity extends Activity implements CameraBridgeViewBase.CvCameraViewListener2  {
    private CameraGLRendererBase camera = null;
    private SurfaceView surfaceView;
    private CameraBridgeViewBase mOpenCvCameraView;
    private String TAG = "Tag";
    private CameraManager mCameraManager    = null;
    private int REQUEST_CAMERA_PERMISSION = 0;
    private boolean load = false;

    private CascadeClassifier cascade = null;
    private Rect[] bufArray = null;
    private int FSC = 0;
    private int bufFSC = 0;

    private float scaleFactor = (float) 1.1;
    private int minNeighbors = 3;
    private int min_width = 30;
    private int min_height = 80;
    private int max_width = 153;
    private int max_height = 480;


    public void openCamera(Activity thisActivity) {
        mCameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);


        if(ContextCompat.checkSelfPermission(thisActivity,
                Manifest.permission.CAMERA)== PackageManager.PERMISSION_GRANTED){
            //mCameraManager.openCamera("0",null,null);
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(thisActivity, Manifest.permission.CAMERA)) {

            } else {
                ActivityCompat.requestPermissions(thisActivity, new String[]{Manifest.permission.CAMERA},
                        REQUEST_CAMERA_PERMISSION);
            }
        }


    }


    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    Log.i(TAG, "OpenCV loaded successfully");
                    try {
                        if (load==false) {
                            int cascadeNum = Integer.parseInt(getIntent().getStringExtra("cascade"));
                            InputStream is = null;
                            File cascadeDir = null;
                            File mCascadeFile = null;
                            FileOutputStream os = null;
                            switch (cascadeNum){
                                case 0:
                                    is = getResources().openRawResource(R.raw.cascade_first);
                                    cascadeDir = getDir("cascade", Context.MODE_PRIVATE);
                                    mCascadeFile = new File(cascadeDir, "cascade_first.xml");
                                    os = new FileOutputStream(mCascadeFile);
                                    break;
                                case 1:
                                    is = getResources().openRawResource(R.raw.cascade_second);
                                    cascadeDir = getDir("cascade", Context.MODE_PRIVATE);
                                    mCascadeFile = new File(cascadeDir, "cascade_second.xml");
                                    os = new FileOutputStream(mCascadeFile);
                                    break;
                                case 2:
                                    is = getResources().openRawResource(R.raw.cascade_third);
                                    cascadeDir = getDir("cascade", Context.MODE_PRIVATE);
                                    mCascadeFile = new File(cascadeDir, "cascade_third.xml");
                                    os = new FileOutputStream(mCascadeFile);
                                    break;
                                case 3:
                                    is = getResources().openRawResource(R.raw.cascade_fourth);
                                    cascadeDir = getDir("cascade", Context.MODE_PRIVATE);
                                    mCascadeFile = new File(cascadeDir, "cascade_fourth.xml");
                                    os = new FileOutputStream(mCascadeFile);
                                    break;

                            }


                            byte[] buffer = new byte[512];
                            int bytesRead;
                            while ((bytesRead = is.read(buffer)) != -1) {
                                os.write(buffer, 0, bytesRead);
                            }
                            is.close();
                            os.close();

                            Log.e(TAG, mCascadeFile.getAbsolutePath());
                            cascade = new CascadeClassifier(mCascadeFile.getCanonicalPath());
                            cascade.load(mCascadeFile.getAbsolutePath());
                            Log.e(TAG, mCascadeFile.getAbsolutePath());
                            // cascade = new CascadeClassifier("cascade_third.xml");
                            load = true;
                            if (cascade.empty()) {
                                Log.e(TAG, "Failed to load cascade classifier");
                                cascade = null;
                                load = false;
                            } else {
                                Log.e(TAG, "=====>>>Cascade load!!!");
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    //System.loadLibrary("detection_based_tracker");  //убрать если че
                    mOpenCvCameraView.enableView();
                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };

    @Override
    public void onResume()
    {
        super.onResume();

        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_6, this, mLoaderCallback);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        scaleFactor = Float.parseFloat(getIntent().getStringExtra("scaleFactor"));
        minNeighbors = Integer.parseInt(getIntent().getStringExtra("minNeighbors"));
        min_width = Integer.parseInt(getIntent().getStringExtra("min_width"));
        min_height = Integer.parseInt(getIntent().getStringExtra("min_height"));
        max_width = Integer.parseInt(getIntent().getStringExtra("max_width"));
        max_height = Integer.parseInt(getIntent().getStringExtra("max_height"));
        openCamera(this);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.HelloOpenCvView);
        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
        mOpenCvCameraView.setCvCameraViewListener(this);

    }

    @Override
    public void onPause()
    {
        super.onPause();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    public void onDestroy() {
        super.onDestroy();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    public void onCameraViewStarted(int width, int height) {
    }

    public void onCameraViewStopped() {
    }


    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {

        //пропуск фреймов убрать если что)
        if (bufFSC < FSC){
            Mat mat = inputFrame.rgba();
            for (int i = 0; i < bufArray.length; i++)
                Imgproc.rectangle(mat, bufArray[i].tl(), bufArray[i].br(), new Scalar(0, 255, 0), 3);
            bufFSC++;
            return mat;
        }
        bufFSC =0;
        FSC = Integer.parseInt(getIntent().getStringExtra("FSC"));;

        Mat mat = inputFrame.rgba();
        Mat mat_clone = inputFrame.gray();
        mat.copyTo(mat_clone);

        if (load) {
            MatOfRect faces = new MatOfRect();
            cascade.detectMultiScale(mat_clone, faces,scaleFactor , minNeighbors, 2, // TODO: objdetect.CV_HAAR_SCALE_IMAGE
                    new Size(min_width, min_height), new Size(max_width, max_height));

            Rect[] facesArray = faces.toArray();
            for (int i = 0; i < facesArray.length; i++)
                Imgproc.rectangle(mat, facesArray[i].tl(), facesArray[i].br(), new Scalar(0, 255, 0), 3);
            //тоже относится к пропуску фреймов
            bufArray = facesArray;
        }

        return mat;
    }

    // public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
    //     return inputFrame.rgba();
    // }
}


