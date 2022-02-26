package com.dcerna.futbolito;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements SensorEventListener{
    String TAG = "MainActivity";
    private final float[] gravity = new float[4];
    private SensorManager sensorManager;
    private Sensor sensor;
    private float xPos, xAcceleration, xVelocity = 0.0f;
    private float yPos, yAcceleration, yVelocity = 0.0f;
    private float screenWidth, screenHeight;
    private ImageView ball;
    private TextView txt1, txt2;
    int score1 = 0;
    int score2 = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this, sensor, sensorManager.SENSOR_DELAY_FASTEST);

        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        ball = (ImageView) findViewById(R.id.ball);
        txt1 = findViewById(R.id.score1);
        txt2 = findViewById(R.id.score2);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        screenWidth = (float) displayMetrics.widthPixels;
        screenHeight = (float) displayMetrics.heightPixels;

        resetBall();
    }

    private void update(float xOrientation, float yOrientation) {
        xAcceleration = xOrientation;
        yAcceleration = yOrientation;
        updateX();
        updateY();

        int dist=150;

        // Chocó izquierda
        if(xPos<0){
            xPos=0;
            xVelocity = -xVelocity/4;
        }
        // Chocó derecha
        if(xPos>screenWidth-dist){
            xPos = screenWidth-dist;
            xVelocity = -xVelocity/4;
        }
        // Chocó arriba
        if(yPos<0){
            yPos=0;
            yVelocity = -yVelocity/4;
            score2++;
            updateScores();
            resetBall();
        }
        // Chocó abajo
        if(yPos>screenHeight-dist){
            yPos = screenHeight-dist;
            yVelocity = -yVelocity/4;
            score1++;
            updateScores();
            resetBall();
        }

        ball.setX(xPos);
        ball.setY(yPos);
    }

    private void resetBall() {
        xVelocity = 0;
        xAcceleration = 0;
        yVelocity = 0;
        yAcceleration = 0;
        xPos=screenWidth/2;
        yPos=screenHeight/2;
        ball.setX(xPos);
        ball.setY(yPos);
    }

    private void updateScores() {
        txt1.setText(""+score1);
        txt2.setText(""+score2);
    }

    void updateX() {
        xVelocity -= xAcceleration * 0.3f;
        xPos += xVelocity;
    }

    void updateY() {
        yVelocity -= yAcceleration * 0.3f;
        yPos += yVelocity;
    }

    @Override
    public void onSensorChanged(SensorEvent movimiento) {
        //Constante para el movimiento lento
        final float alpha = (float) 0.9;
        //Filtrar el paso de gravedad
        gravity[0] = alpha * gravity[0] + (1 - alpha) * movimiento.values[0];
        gravity[1] = alpha * gravity[1] + (1 - alpha) * (movimiento.values[1]);
        gravity[2] = alpha * gravity[2] + (1 - alpha) * movimiento.values[2];
        //Eliminar la contribucion de la gravedad
        float x = movimiento.values[0] - gravity[0];
        float y = movimiento.values[1] - gravity[1];
        float z = movimiento.values[2] - gravity[2];

        //Actualizamos, utilizamos el contrarios de y para simular la bajada y subida
        update(x,(y*-1));
        //Log.d("Cordernadas", x+" "+y);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}