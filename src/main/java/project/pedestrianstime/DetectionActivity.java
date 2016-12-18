package project.pedestrianstime;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.camera2.CameraManager;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.format.Time;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraGLRendererBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvException;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Random;

import project.pedestrianstime.R;

import static org.opencv.imgcodecs.Imgcodecs.imwrite;

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
    //обратотка касаний
    //0 - ничего
    //1 - сохранение резултьтата
    //2 - показ
    private int click = 0;
    private Mat previous;


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
    private ImageButton save = null;
    private Activity thisActivity = this;


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
        //обратотка касаний
        mOpenCvCameraView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch(click){
                    case 0:
                        save.setVisibility(ImageButton.VISIBLE);

                        click = 1;
                        break;
                    case 1:
                        //save.setVisibility(ImageButton.INVISIBLE);
                        click = 0;
                        break;
                    case 2:
                        //save.setVisibility(ImageButton.INVISIBLE);
                        click = 0;
                        break;
                    default:
                        //save.setVisibility(ImageButton.INVISIBLE);
                        click = 0;
                        break;
                }
            }
        });
        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
        mOpenCvCameraView.setCvCameraViewListener(this);

        save = (ImageButton) findViewById(R.id.save);
        save.setVisibility(ImageButton.VISIBLE);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (click==2){
                    Bitmap bmp = null;

                    try {
                        bmp = Bitmap.createBitmap(previous.cols(), previous.rows(), Bitmap.Config.ARGB_8888);
                        Utils.matToBitmap(previous, bmp);
                        SavePicture(bmp);
                        click=0;
                    }
                    catch (CvException e){Log.d("Exception",e.getMessage());}
                }

            }
        });
    }

    private void SavePicture(Bitmap bmp)
    {
        //ибаный пермишен
        String[] perms = {"android.permission.WRITE_EXTERNAL_STORAGE", "android.permission.READ_EXTERNAL_STORAGE"};
        int permsRequestCode = 200;
        requestPermissions(perms, permsRequestCode);

        //дата
        SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss.S");
        GregorianCalendar calendar = new GregorianCalendar();
        Date dateTime = calendar.getTime();
        String curDate = dateFormat.format(dateTime);

        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + "/Pictures/PedestrianTime");
        myDir.mkdirs();
        String fname = "Image-" + curDate + ".jpg";
        File file = new File(myDir, fname);
        Log.i(TAG, "" + file);
        if (file.exists())
            file.delete();

        try {

            FileOutputStream out = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, out);

            out.flush();
            out.close();

            MediaStore.Images.Media.insertImage(this.getContentResolver(), file.getAbsolutePath(), file.getName(), file.getName()); // регистрация в фотоальбоме
            MediaScannerConnection.scanFile(this,
                    new String[]{file.toString()}, null,
                    new MediaScannerConnection.OnScanCompletedListener() {
                        public void onScanCompleted(String path, Uri uri) {
                            Log.i("ExternalStorage", "Scanned " + path + ":");
                            Log.i("ExternalStorage", "-> uri=" + uri);
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
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

        if (click==2){
            return previous;
        }

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
        if (click==1) {
            previous = mat;
            click++;
        }

        return mat;
    }

    // public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
    //     return inputFrame.rgba();
    // }
}


