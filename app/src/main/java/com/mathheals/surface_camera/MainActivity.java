package com.mathheals.surface_camera;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.RectF;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;


public class MainActivity extends Activity implements SurfaceHolder.Callback{
    private final static String DEBUG_TAG = "MakePhotoActivity";
    private Camera camera;
    private int cameraId = 0;
    SurfaceView camview;
    SurfaceHolder surfaceholder;
    boolean camcondition=false;
    Button capture,face_detection;
    ImageView resim_show;
    Bitmap alınan_resim;
    long zaman;
    ArrayList liste=new ArrayList(10);
    private Context context;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().setFormat(PixelFormat.UNKNOWN);

        //Butonların tanımlamaları
        capture=(Button)findViewById(R.id.capture);
        face_detection=(Button)findViewById(R.id.face_detection);

        // Kameramız var mı?
        if (!getPackageManager()
                .hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            Toast.makeText(this, "No camera on this device", Toast.LENGTH_LONG)
                    .show();
        } else {
            cameraId = findFrontFacingCamera();
            if (cameraId < 0) {
                Toast.makeText(this, "No front facing camera found.",
                        Toast.LENGTH_LONG).show();
            } else {
                camera = Camera.open(cameraId);
            }

        }
        camview=(SurfaceView)findViewById(R.id.kamera);
        surfaceholder=camview.getHolder();
        surfaceholder.setType(SurfaceHolder.SURFACE_TYPE_NORMAL);

        //resim çekmek için buton kodları
        capture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               for(int i=0;i<10;i++){
                    camera.startPreview();
                    camera.takePicture(null, null,
                            null, mpicturecall);
                   try {
                       TimeUnit.SECONDS.sleep(2);
                   } catch (InterruptedException e) {
                       e.printStackTrace();
                   }
                   if(i==10)
                       camera.stopPreview();
               } }
        });



        //Yüz bulma için buton
        face_detection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
               //normalde çalışıyor farklı bir activitye atacağım

                camera.stopPreview();
                Toast.makeText(MainActivity.this, "Dosya alma denemesi =", Toast.LENGTH_SHORT).show();

                File imgFile = new  File("/storage/emulated/0/test_qr.jpg");

                if(imgFile.exists()){

                    Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());

                    resim_show.setImageBitmap(myBitmap);

                }

            }
        });


    }


    Camera.PictureCallback mpicturecall=new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {

            FileOutputStream outstream=null;
            try{
                zaman=System.currentTimeMillis();
                liste.add(zaman);
              //  Toast.makeText(MainActivity.this, "zaman"+zaman, Toast.LENGTH_SHORT).show();
                outstream=new FileOutputStream("/sdcard/AndroidFotolarım"+zaman+".jpg");
                outstream.write(data);
                outstream.close();

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally{
            }

        }
    };

    //liste içeriğini getirir
    public void liste_kontrol(){

    for(Object str : liste) {
        Toast.makeText(MainActivity.this, "zaman ="+str, Toast.LENGTH_SHORT).show();

    }



}

    private int findFrontFacingCamera() {
        int cameraId = -1;
        // Search for the front facing camera
        int numberOfCameras = Camera.getNumberOfCameras();
        for (int i = 0; i < numberOfCameras; i++) {
            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(i, info);
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                Log.d(DEBUG_TAG, "Camera found");
                cameraId = i;
                break;
            }
        }
        return cameraId;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        camera.setDisplayOrientation(360);
        camera=camera.open();

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

        if(camcondition) {

            camera.stopPreview();
            camcondition=false;
        }
        if(camera!=null)
        {
            try{
                Camera.Parameters parameters=camera.getParameters();
                parameters.setColorEffect(Camera.Parameters.EFFECT_SEPIA);
                camera.setParameters(parameters);
                camera.setPreviewDisplay(surfaceholder);
                camcondition=true;
            }

            catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

        camera.stopPreview();
        camera.release();
        camera=null;
        camcondition=false;
    }

    //yüz bulma (face detection)
    public Bitmap dosya_oku(){


        Toast.makeText(MainActivity.this, "deneme =", Toast.LENGTH_SHORT).show();
        File file= new File(android.os.Environment.getExternalStorageDirectory(),"AndroidFotolarım1520025657867.jpg");
        final Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
        //dosya okunması için

     //   resim_show.setImageBitmap(bitmap);
        Toast.makeText(MainActivity.this, "dosya okunuyor", Toast.LENGTH_SHORT).show();

        final Paint rectPaint = new Paint();
        rectPaint.setStrokeWidth(5);
        rectPaint.setColor(Color.WHITE);
        rectPaint.setStyle(Paint.Style.STROKE);

        final Bitmap tempBitmap=Bitmap.createBitmap(bitmap.getWidth(),bitmap.getHeight(),Bitmap.Config.RGB_565);
        final Canvas canvas = new Canvas(tempBitmap);

        canvas.drawBitmap(bitmap,0,0,null);
        FaceDetector faceDetector = new FaceDetector.Builder(getApplicationContext())

                .setTrackingEnabled(false)
                .setLandmarkType(FaceDetector.ALL_LANDMARKS)
                .setMode(FaceDetector.FAST_MODE)
                .build();

        if(!faceDetector.isOperational())
        {

            Toast.makeText(MainActivity.this, "FaceDedector could not be set up your device", Toast.LENGTH_SHORT).show();


        }

        Frame frame= new Frame.Builder().setBitmap(bitmap).build();

        SparseArray<Face> sparceArray=faceDetector.detect(frame);

        for(int i=0; i<sparceArray.size(); i++)
        {

            Face face = sparceArray.valueAt(i);
            float x1=face.getPosition().x;
            float y1=face.getPosition().y;
            float x2=x1+face.getWidth();
            float y2=y1+face.getHeight();

            RectF rectF= new RectF(x1,y1,x2,y2);
            canvas.drawRoundRect(rectF,2,2,rectPaint);

            /*
            Rect src = new Rect((int) left, (int) top, (int) right, (int) bottom);
            Rect dst = new Rect(0, 0, size, size);
            canvas.drawBitmap(mBitmap, src, dst, null);
             */
        }

       // resim_show.setImageDrawable(new BitmapDrawable(getResources(),tempBitmap));


        return bitmap;
}


}