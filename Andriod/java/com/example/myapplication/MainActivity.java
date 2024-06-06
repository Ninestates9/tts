package com.example.myapplication;




import java.io.*;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.widget.AdapterView;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import ai.onnxruntime.*;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.res.AssetManager;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;

import ai.onnxruntime.OnnxTensor;
import ai.onnxruntime.OrtEnvironment;
import ai.onnxruntime.OrtSession;

import java.io.File;
import java.io.IOException;
import java.util.Map;


import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {
    private EditText editText;
    private MediaPlayer mediaPlayer;
    private String wavFilePath;


    @SuppressLint("StaticFieldLeak")
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
        Button replay = findViewById(R.id.replay);
        wavFilePath =  getFilesDir().getAbsolutePath() + "/result.wav";
        final long[] speakerId = {0};
        double noiseScale = 0.1;
        double noiseScaleW = 0.668;
        long lengthScale = 1;



        Spinner spinner = findViewById(R.id.spinner);
        String[] items = new String[]{"艾瑞莉娅", "男生", "女生", "还是女生", "薇古丝", "小孩"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String selectedItem = items[position]; // 获取用户选择的选项

                // 根据选择的选项来更改变量的值
                switch (selectedItem) {
                    case "Option 1" ->
                        // 更改变量的值为某个值
                            speakerId[0] = 129;
                    case "男生" ->
                        // 更改变量的值为另一个值
                            speakerId[0] = 96;
                    case "女生" ->
                        // 更改变量的值为另一个值
                            speakerId[0] = 124;
                    case "还是女生" ->
                        // 更改变量的值为另一个值
                            speakerId[0] = 35;
                    case "薇古丝" ->
                        // 更改变量的值为另一个值
                            speakerId[0] = 49;
                    case "小孩" ->
                        // 更改变量的值为另一个值
                            speakerId[0] = 228;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                speakerId[0] = 129;
            }
        });


        button.setOnClickListener(v -> {
            Log.d("MainActivity", "Button clicked");

            String inputtext = editText.getText().toString();
            if (inputtext.isEmpty()) {
                new AlertDialog.Builder(this)
                        .setIcon(R.drawable.baseline_announcement_24)
                        .setTitle("提示")
                        .setMessage("输入不能为空")
                        .create()
                        .show();
            } else {
                AlertDialog alarm = new AlertDialog.Builder(this)
                        .setIcon(R.drawable.baseline_announcement_24)
                        .setTitle("提示")
                        .setMessage("正在生成语音")
                        .create();
                alarm.show();

                new AsyncTask<String, Void, Void>() {
                    @Override
                    protected Void doInBackground(String... strings) {
                        try {
                            audioCreate(inputtext, speakerId[0], noiseScale, noiseScaleW, lengthScale);
                        } catch (IOException | OrtException e) {
                            throw new RuntimeException(e);
                        }
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void aVoid) {
                        alarm.dismiss();
                        exportAudioFile();

                        mediaPlayer.reset();
                        try {
                            mediaPlayer.setDataSource(getFilesDir().getAbsolutePath() + "/result.wav");
                            mediaPlayer.prepareAsync();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }

                        mediaPlayer.setOnPreparedListener(mp -> {
                            mediaPlayer.start();
                            Log.d("MainActivity", "MediaPlayer played");
                        });
                    }
                }.execute(inputtext);
            }
        });
        replay.setOnClickListener(v -> {
            mediaPlayer.reset();
            try {
                mediaPlayer.setDataSource(getFilesDir().getAbsolutePath() + "/result.wav");
                mediaPlayer.prepareAsync();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            mediaPlayer.setOnPreparedListener(mp -> {
                mediaPlayer.start();
                Log.d("MainActivity", "MediaPlayer played");
            });
                });

        clear.setOnClickListener(v -> editText.setText(""));

        toolbar.setNavigationOnClickListener(v -> {

        });

    }

    public void audioCreate(String text, long sID, double noiseScale, double noiseScaleW, long lengthScale) throws IOException, OrtException {

        text = text.replace("\n", "").replace("\r", "").replace(" ", "");
//        text = "[ZH]" + text + "[ZH]";


        List<Long> list = toSequence(text);
        long[] x = list.stream().mapToLong(Long::valueOf).toArray();

        long[] input_x = new long[x.length * 2 + 1];

        for (int i = 0, j = 0; i < x.length * 2 + 1; i++) {
            if (i % 2 == 0) {
                input_x[i] = 0;
            } else {
                input_x[i] = x[j++];
            }
        }

        vits(input_x, sID, noiseScale, noiseScaleW, lengthScale);

    }


    public void vits(long[] input_x, long sID, double noiseScale, double noiseScaleW, long lengthScale) throws OrtException, IOException {
        var env = OrtEnvironment.getEnvironment();
        AssetManager assetManager = getAssets();
        InputStream stream = assetManager.open("G_trilingual.onnx");
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();

        byte[] buffer = new byte[4096];
        int bytesRead;
        while ((bytesRead = stream.read(buffer)) != -1) {
            byteStream.write(buffer, 0, bytesRead);
        }

        byteStream.flush();
        byte[] bytes = byteStream.toByteArray();
        Log.d("MainActivity", String.valueOf(bytes.length));

        var session = env.createSession(bytes, new OrtSession.SessionOptions());

        long[][] x={input_x};
        var t_x = OnnxTensor.createTensor(env,x);
        long[] x_lengths = {x[0].length};
        var t_x_lengths = OnnxTensor.createTensor(env,x_lengths);
        long[] sid = {sID};
        var t_sid = OnnxTensor.createTensor(env,sid);
        double[] noise_scale = {noiseScale};
        var t_noise_scale = OnnxTensor.createTensor(env,noise_scale);
        double[] noise_scale_w = {noiseScaleW};
        var t_noise_scale_w = OnnxTensor.createTensor(env,noise_scale_w);
        long[] length_scale = {lengthScale};
        var t_x_length_scale = OnnxTensor.createTensor(env,length_scale);
        var inputs = Map.of("x",t_x,"x_lengths",t_x_lengths, "sid", t_sid, "noise_scale", t_noise_scale, "length_scale", t_x_length_scale, "noise_scale_w", t_noise_scale_w);
        try (var results = session.run(inputs)) {
            float[][][] result = (float[][][])results.get(0).getValue();
            float[] audio = result[0][0];
            saveArrayAsWav(audio, wavFilePath);
        }
    }





    public static void saveArrayAsWav(float[] data, String filePath) {
        try {
            // 创建WAV文件头
            byte[] header = createWavHeader(data.length);

            // 创建文件
            File file = new File(filePath);
            FileOutputStream fos = new FileOutputStream(file);

            // 写入WAV文件头
            fos.write(header);

            // 写入数据
            for (float value : data) {
                fos.write(toByteArray(value));
            }

            fos.close();
        } catch (IOException e) {
            Log.e("MainActivity", "An IOException occurred", e);
        }
    }
    private static byte[] createWavHeader(int dataLength) {
        int sampleRate = 22050;
        int channels = 1;
        int bytesPerSample = 2; // 2 bytes per sample (float)

        int totalLength = dataLength * bytesPerSample + 44;

        byte[] header = new byte[44];

        // RIFF chunk descriptor
        header[0] = 'R';
        header[1] = 'I';
        header[2] = 'F';
        header[3] = 'F';
        writeInt(header, 4, totalLength);
        header[8] = 'W';
        header[9] = 'A';
        header[10] = 'V';
        header[11] = 'E';

        // Format chunk
        header[12] = 'f';
        header[13] = 'm';
        header[14] = 't';
        header[15] = ' ';
        writeInt(header, 16, 16); // chunk size
        header[20] = 1; // audio format (1 = PCM)
        header[22] = (byte) channels;
        writeInt(header, 24, sampleRate);
        writeInt(header, 28, sampleRate * channels * bytesPerSample); // Byte rate
        header[32] = (byte) (channels * bytesPerSample); // Block align
        header[34] = (byte) (bytesPerSample * 8); // Bits per sample

        // Data chunk
        header[36] = 'd';
        header[37] = 'a';
        header[38] = 't';
        header[39] = 'a';
        writeInt(header, 40, dataLength * bytesPerSample);

        return header;
    }

    private static byte[] toByteArray(float value) {
        byte[] bytes = new byte[2];
        int intValue = (int) (value * 32767.0f); // 将float值转换为16位整数
        bytes[0] = (byte) intValue;
        bytes[1] = (byte) (intValue >> 8);
        return bytes;
    }

    private static void writeInt(byte[] buffer, int offset, int value) {
        buffer[offset] = (byte) (value & 0xFF);
        buffer[offset + 1] = (byte) ((value >> 8) & 0xFF);
        buffer[offset + 2] = (byte) ((value >> 16) & 0xFF);
        buffer[offset + 3] = (byte) ((value >> 24) & 0xFF);
    }
    private void exportAudioFile() {
        try {
            File inputFile = new File(getFilesDir(), "result.wav");
            Uri externalUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

            ContentValues contentValues = new ContentValues();
            contentValues.put(MediaStore.Audio.Media.DISPLAY_NAME, "exported_audio.wav");

            Uri exportUri = getContentResolver().insert(externalUri, contentValues);

            if (exportUri != null) {
                try (OutputStream outputStream = getContentResolver().openOutputStream(exportUri);
                     FileInputStream fileInputStream = new FileInputStream(inputFile)) {

                    byte[] buffer = new byte[1024];
                    int length;
                    while ((length = fileInputStream.read(buffer)) > 0) {
                        if (outputStream != null) {
                            outputStream.write(buffer, 0, length);
                        } else {
                            Log.e("MainActivity", "outputStream is null. Cannot write to stream.");
                        }
                    }

                    Log.d("MainActivity", "Audio file exported successfully to: " + exportUri);
                }
            }
        } catch (IOException e) {
            Log.e("MainActivity", "An IOException occurred", e);
        }
        new AlertDialog.Builder(this)
                .setIcon(R.drawable.baseline_announcement_24)
                .setTitle("提示")
                .setMessage("音频文件已保存到资源管理器根目录")
                .create()
                .show();
    }

    private static final Map<Character, Integer> symbolToId = new HashMap<>();
    static {
        symbolToId.put('_', 0);
        symbolToId.put(',', 1);
        symbolToId.put('.', 2);
        symbolToId.put('!', 3);
        symbolToId.put('?', 4);
        symbolToId.put('-', 5);
        symbolToId.put('~', 6);
        symbolToId.put('…', 7);
        symbolToId.put('N', 8);
        symbolToId.put('Q', 9);
        symbolToId.put('a', 10);
        symbolToId.put('b', 11);
        symbolToId.put('d', 12);
        symbolToId.put('e', 13);
        symbolToId.put('f', 14);
        symbolToId.put('g', 15);
        symbolToId.put('h', 16);
        symbolToId.put('i', 17);
        symbolToId.put('j', 18);
        symbolToId.put('k', 19);
        symbolToId.put('l', 20);
        symbolToId.put('m', 21);
        symbolToId.put('n', 22);
        symbolToId.put('o', 23);
        symbolToId.put('p', 24);
        symbolToId.put('s', 25);
        symbolToId.put('t', 26);
        symbolToId.put('u', 27);
        symbolToId.put('v', 28);
        symbolToId.put('w', 29);
        symbolToId.put('x', 30);
        symbolToId.put('y', 31);
        symbolToId.put('z', 32);
        symbolToId.put('ɑ', 33);
        symbolToId.put('æ', 34);
        symbolToId.put('ʃ', 35);
        symbolToId.put('ʑ', 36);
        symbolToId.put('ç', 37);
        symbolToId.put('ɯ', 38);
        symbolToId.put('ɪ', 39);
        symbolToId.put('ɔ', 40);
        symbolToId.put('ɛ', 41);
        symbolToId.put('ɹ', 42);
        symbolToId.put('ð', 43);
        symbolToId.put('ə', 44);
        symbolToId.put('ɫ', 45);
        symbolToId.put('ɥ', 46);
        symbolToId.put('ɸ', 47);
        symbolToId.put('ʊ', 48);
        symbolToId.put('ɾ', 49);
        symbolToId.put('ʒ', 50);
        symbolToId.put('θ', 51);
        symbolToId.put('β', 52);
        symbolToId.put('ŋ', 53);
        symbolToId.put('ɦ', 54);
        symbolToId.put('⁼', 55);
        symbolToId.put('ʰ', 56);
        symbolToId.put('`', 57);
        symbolToId.put('^', 58);
        symbolToId.put('#', 59);
        symbolToId.put('*', 60);
        symbolToId.put('=', 61);
        symbolToId.put('ˈ', 62);
        symbolToId.put('ˌ', 63);
        symbolToId.put('→', 64);
        symbolToId.put('↓', 65);
        symbolToId.put('↑', 66);
        symbolToId.put(' ', 67);
    }

    public static String cleantext(String text) {
        var cleantext = new CleanText();
        text = cleantext.clean(text);

        return text;

    }

    public List<Long> toSequence(String text) {

        List<Long> sequence = new ArrayList<>();

        text = cleantext(text);

        for (char symbol : text.toCharArray()) {
            if (!symbolToId.containsKey(symbol)) {
                continue;
            }
            int symbolId = symbolToId.get(symbol);
            sequence.add((long) symbolId);
        }

        return sequence;

    }


}



