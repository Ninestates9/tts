package com.example.myapplication;

import ai.onnxruntime.*;

import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.nio.FloatBuffer;
import java.util.Collections;
import java.util.Optional;

public class MainActivity extends AppCompatActivity {
    private EditText editText;
    private MediaPlayer mediaPlayer;
    private String wavFilePath;
    private OrtEnvironment environment;
    private OrtSession session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        mediaPlayer = new MediaPlayer();

        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build();
        mediaPlayer.setAudioAttributes(audioAttributes);
        Toolbar toolbar = findViewById(R.id.toolbar);
        Button button = findViewById(R.id.button);
        Button clear = findViewById(R.id.clear);
        editText = findViewById(R.id.editText);

        wavFilePath = "android.resource://" + getPackageName() + "/" + R.raw.test;

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("MainActivity", "Button clicked");
                String text = editText.getText().toString();
                mediaPlayer.reset();
                mediaPlayer = MediaPlayer.create(MainActivity.this, R.raw.test);
                mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        Log.d("MainActivity", "MediaPlayer prepared");
                        mediaPlayer.start();
                    }
                });


            }

        });

        clear.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                editText.setText("");
            }
        });

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }
    public MainActivity() {
        try {
            // 创建ONNX Runtime环境
            environment = OrtEnvironment.getEnvironment();

            // 加载ONNX模型
            OrtSession.SessionOptions sessionOptions = new OrtSession.SessionOptions();
            session = environment.createSession("file:///android_asset/G_trilingual.onnx", sessionOptions);
        } catch (OrtException e) {
            e.printStackTrace();
        }
    }

    public float[] runInference(int speakerId, double noiseScale, double noiseScaleW, int lengthScale) {
        try {
            // 创建输入张量
            long[] shape = {1, input.length}; // 假设输入是一维向量
            OnnxTensor inputTensor = OnnxTensor.createTensor(environment, FloatBuffer.wrap(input), shape);

            // 运行推理
            OrtSession.RunOptions runOptions = new OrtSession.RunOptions();
            OrtSession.Result output = session.run(Collections.singletonMap(session.getInputNames().iterator().next(), inputTensor), runOptions);

            // 获取输出数据
            Optional<OnnxValue> optionalOutputValue = output.get(session.getOutputNames().iterator().next());
            if (optionalOutputValue.isPresent()) {
                OnnxValue outputValue = optionalOutputValue.get();
                if (outputValue instanceof OnnxTensor) {
                    OnnxTensor outputTensor = (OnnxTensor) outputValue;
                    float[] outputData = outputTensor.getFloatBuffer().array();
                    return outputData;
                }
            }
        } catch (OrtException e) {
            e.printStackTrace();
        }

        return null;
    }
}