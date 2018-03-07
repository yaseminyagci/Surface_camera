package com.mathheals.surface_camera;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;

import java.io.File;

/**
 * Created by user on 5.03.2018.
 */

        //resimde yüzü bulma işlemini yapıyor
        public class face_detection extends Activity {

            ImageView resim, resim2;
            Button buti;

            @Override
            public void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.activity_main);

                super.onCreate(savedInstanceState);
                setContentView(R.layout.activity_main);

                resim = (ImageView) findViewById(R.id.resim_show);
                resim2 = (ImageView) findViewById(R.id.resim_show2);
                buti = (Button) findViewById(R.id.face_detection);

                //DOSYAYA KAYDETTİĞİMİZ RESMİ ÇEKİP BİTMAP DOSYASINA ATIYORUZ
                File file = new File(android.os.Environment.getExternalStorageDirectory(), "21.jpg");
                final Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());

                //final Bitmap myBitmap = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.tom);

                resim.setImageBitmap(bitmap);

                final Paint rectPaint = new Paint();
                rectPaint.setStrokeWidth(6);
                rectPaint.setColor(Color.RED);
                rectPaint.setStyle(Paint.Style.STROKE);

                final Bitmap tempBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.RGB_565);
                final Canvas canvas = new Canvas(tempBitmap);

                canvas.drawBitmap(bitmap, 0, 0, null);

                buti.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                        FaceDetector faceDetector = new FaceDetector.Builder(getApplicationContext())

                                .setTrackingEnabled(false)
                                .setLandmarkType(FaceDetector.ALL_LANDMARKS)
                                .setMode(FaceDetector.FAST_MODE)
                                .build();

                        if (!faceDetector.isOperational()) {

                            Toast.makeText(com.mathheals.surface_camera.face_detection.this, "FaceDedector could not be set up your device", Toast.LENGTH_SHORT).show();
                            return;

                        }

                        Frame frame = new Frame.Builder().setBitmap(bitmap).build();

                        SparseArray<Face> sparceArray = faceDetector.detect(frame);
                        for (int i = 0; i < sparceArray.size(); i++) {

                            Face face = sparceArray.valueAt(i);
                            float x1 = face.getPosition().x;
                            float y1 = face.getPosition().y;
                            float x2 = x1 + face.getWidth();
                            float y2 = y1 + face.getHeight();

                            RectF rectF = new RectF(x1, y1, x2, y2);
                            canvas.drawRoundRect(rectF, 2, 2, rectPaint);

                            //KIRPMA İŞLEMİNİ RESMİN ÜSTÜNE YAPIYOR

                            //  Rect src = new Rect((int) x1, (int) y1, (int) x2, (int) y2);
                            //Rect dst = new Rect(100, 0, 600,600 );
                            //canvas.drawBitmap(tempBitmap, src, dst, null);
                        }

                        resim2.setImageBitmap(tempBitmap);


                    }
                });
            }
        }






































