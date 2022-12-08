package com.google.mlkit.vision.demo.java.posedetector;


//import static com.google.mlkit.vision.demo.java.musicplayer.song.audioPlayer;

import android.annotation.SuppressLint;
import android.content.Context;
        import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;


import com.google.mlkit.vision.demo.GraphicOverlay;

        import com.google.mlkit.vision.pose.Pose;
import com.google.mlkit.vision.pose.PoseLandmark;


import java.io.IOException;
        import java.util.List;


public class PoseGraphic extends GraphicOverlay.Graphic {

    private static final float DOT_RADIUS = 15;
    private static final float IN_FRAME_LIKELIHOOD_TEXT_SIZE = 40.0f;
    private static float threshold = 0f;

    private final Pose pose;
    private boolean showInFrameLikelihood;
    private float dynamicPaintThreshold = 6.5f;
    private float angleError;
    private final Paint linePaint;
    private final Paint linePaintGreen;
    private final Paint dynamicLinePaint;
    private final Paint dotPaint;
    private final Paint textPaint;
    private final Paint clinePaint;
    private final Paint rclinePaint;
    private Context context;


    PoseGraphic(GraphicOverlay overlay, Pose pose, boolean showInFrameLikelihood) {
        super(overlay);
        MediaPlayer mediaPlayer;

        final float DOT_RADIUS = 60.0f;
        this.pose = pose;
        this.showInFrameLikelihood = true;
        this.angleError = 0;

        dotPaint = new Paint();
        dotPaint.setColor(Color.WHITE);
        dotPaint.setTextSize(IN_FRAME_LIKELIHOOD_TEXT_SIZE);

        linePaint = new Paint();
        linePaint.setStrokeWidth(5);
        linePaint.setColor(Color.CYAN);

        clinePaint = new Paint();
        clinePaint.setStrokeWidth(10);
        clinePaint.setColor(Color.GREEN);

        rclinePaint = new Paint();
        rclinePaint.setStrokeWidth(10);
        rclinePaint.setColor(Color.RED);


        linePaintGreen = new Paint();
        linePaintGreen.setStrokeWidth(17);
        linePaintGreen.setColor(Color.YELLOW);

        dynamicLinePaint = new Paint();
        dynamicLinePaint.setStrokeWidth(17.5f);

        textPaint = new Paint();
        textPaint.setColor(Color.BLUE);
        textPaint.setTextSize(50);

    }

    public static int getScreenWidth() {
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }

    public static int getScreenHeight() {
        return Resources.getSystem().getDisplayMetrics().heightPixels;
    }

    private int getRed(Float value) {
        return (int) ((1 - value) * 255);


    }

    private int getGreen(Float value) {
        return (int) ((value) * 255);

    }

//


    @Override
    public void draw(Canvas canvas) {
        /* super.getClass(canvas); */
        List<PoseLandmark> landmarks = pose.getAllPoseLandmarks();
        if (landmarks.isEmpty()) {
            return;
        }


        // get cordinate of pose
        PoseLandmark leftShoulder = pose.getPoseLandmark(PoseLandmark.LEFT_SHOULDER);
        PoseLandmark rightShoulder = pose.getPoseLandmark(PoseLandmark.RIGHT_SHOULDER);
        PoseLandmark leftElbow = pose.getPoseLandmark(PoseLandmark.LEFT_ELBOW);
        PoseLandmark rightElbow = pose.getPoseLandmark(PoseLandmark.RIGHT_ELBOW);
        PoseLandmark leftWrist = pose.getPoseLandmark(PoseLandmark.LEFT_WRIST);
        PoseLandmark rightWrist = pose.getPoseLandmark(PoseLandmark.RIGHT_WRIST);
        PoseLandmark leftHip = pose.getPoseLandmark(PoseLandmark.LEFT_HIP);
        PoseLandmark rightHip = pose.getPoseLandmark(PoseLandmark.RIGHT_HIP);


        // Frame Visibility Check

        showInFrameLikelihood = true;


        if (leftShoulder.getInFrameLikelihood() < threshold) {

            showInFrameLikelihood = false;

        }

        if (rightShoulder.getInFrameLikelihood() < threshold) {

            showInFrameLikelihood = false;

        }

        if (leftElbow.getInFrameLikelihood() < threshold) {

            showInFrameLikelihood = false;

        }

        if (rightElbow.getInFrameLikelihood() < threshold) {

            showInFrameLikelihood = false;

        }

        if (leftWrist.getInFrameLikelihood() < threshold) {

            showInFrameLikelihood = false;

        }

        if (rightWrist.getInFrameLikelihood() < threshold) {

            showInFrameLikelihood = false;

        }


        if (showInFrameLikelihood == true) {


            //get Angle
            SmoothAngles smoothAngles = new SmoothAngles();
            int leftShoulder_angle = (int) smoothAngles.getAngle(leftElbow, leftShoulder, leftHip);
            int rightShoulder_angle = (int) smoothAngles.getAngle(rightElbow, rightShoulder, rightHip);
            int leftElbow_angle = (int) smoothAngles.getAngle(leftShoulder, leftElbow, leftWrist);
            int rightElbow_angle = (int) smoothAngles.getAngle(rightShoulder, rightElbow, rightWrist);


            drawPoint(canvas, leftShoulder, dotPaint);
            drawPoint(canvas, rightShoulder, dotPaint);
            drawPoint(canvas, leftElbow, dotPaint);
            drawPoint(canvas, rightElbow, dotPaint);
            drawPoint(canvas, leftWrist, dotPaint);
            drawPoint(canvas, rightWrist, dotPaint);


            int refrence = 175;
            if ((refrence - 5 <= leftElbow_angle & leftElbow_angle <= refrence + 5) & (refrence - 5 <= rightElbow_angle & rightElbow_angle <= refrence + 5)) {
                // line
                Line(canvas, leftShoulder, rightShoulder, clinePaint);
                Line(canvas, leftShoulder, leftElbow, clinePaint);
                Line(canvas, leftElbow, leftWrist, clinePaint);
                Line(canvas, rightShoulder, rightElbow, clinePaint);
                Line(canvas, rightElbow, rightWrist, clinePaint);
                Line(canvas, leftElbow, leftWrist, clinePaint);
                // for angle
                drawAngle(canvas, leftElbow_angle, rightElbow, textPaint);

                drawAngle(canvas, rightElbow_angle, leftElbow, textPaint);


            } else {

                // audioPlayer(String.valueOf(R.raw.song1));
                Log.e("XAPP", "onPrepared " + Long.toString(Thread.currentThread().getId()));
                audioPlayer("https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3");

                //playMp3("https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3");
                // no need to call prepare(); create() does that for you
                //  Thread t1 = new Thread("https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3");
//                  MyThread1 obj1 = new MyThread1();
//                  obj1.start();


                // audioPlayer("R.raw.song");
                cLine(canvas, leftShoulder, rightShoulder, rclinePaint);
                cLine(canvas, leftShoulder, leftElbow, rclinePaint);
                cLine(canvas, leftElbow, leftWrist, rclinePaint);
                cLine(canvas, rightShoulder, rightElbow, rclinePaint);
                cLine(canvas, rightElbow, rightWrist, rclinePaint);
                cLine(canvas, leftElbow, leftWrist, rclinePaint);
                // for angle
                drawAngle(canvas, leftElbow_angle, rightElbow, textPaint);

                drawAngle(canvas, rightElbow_angle, leftElbow, textPaint);

            }
        }

    }


//    public void audioPlayer(String path) {
//        // set up MediaPlayer
//        MediaPlayer mp = new MediaPlayer();
//        mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
//        try {
//             mp.setDataSource(path);
//             mp.prepare();
//
//           // mp.prepareAsync();
//            mp.start();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }




    public void audioPlayer(String path) {
        Thread thread = new Thread() {

            @Override
            public void run() {
                MediaPlayer mp = new MediaPlayer();
                mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
                try {
                    mp.setDataSource(String.valueOf(path));
                    mp.prepare();

                    // mp.prepareAsync();
                    mp.start();

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        };


        thread.start();



    }











//    private void playAudio() {
//
//
//        String audioUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3";
//        MediaPlayer mediaPlayer = new MediaPlayer();
//        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
//        try {
//            mediaPlayer.setDataSource(audioUrl);
//            mediaPlayer.prepare();
//            mediaPlayer.start();
//        } catch (IOException e) {
//            e.printStackTrace();
//
//        }
//    }



        private void cLine (Canvas canvas, @NonNull PoseLandmark rstart, PoseLandmark rend, Paint rclinePaint){
                canvas.drawLine(
                        translateX(rstart.getPosition().x), translateY(rstart.getPosition().y),
                        translateX(rend.getPosition().x), translateY(rend.getPosition().y), rclinePaint);
            }


        void drawAngle (Canvas canvas,int angle, PoseLandmark point, Paint textPaint){

//          DecimalFormat df = new DecimalFormat("#0");
//                 angle = float.valueOf(df.format(angle));

            canvas.drawText(String.valueOf(angle), translateX(point.getPosition().x), translateY(point.getPosition().y), textPaint);

        }

        void drawPoint (Canvas canvas, PoseLandmark landmark, Paint paint){
            canvas.drawCircle(translateX(landmark.getPosition().x), translateY(landmark.getPosition().y), DOT_RADIUS, paint);
        }

        void drawLine (Canvas canvas, PoseLandmark startLandmark, PoseLandmark endLandmark, Paint
        paint){
            canvas.drawLine(
                    translateX(startLandmark.getPosition().x), translateY(startLandmark.getPosition().y),
                    translateX(endLandmark.getPosition().x), translateY(endLandmark.getPosition().y), paint);
        }
        void Line (Canvas canvas, PoseLandmark start, PoseLandmark end, Paint clinePaint ){
            canvas.drawLine(
                    translateX(start.getPosition().x), translateY(start.getPosition().y),
                    translateX(end.getPosition().x), translateY(end.getPosition().y), clinePaint);
        }




    }



















