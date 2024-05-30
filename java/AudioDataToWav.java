import javax.sound.sampled.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class AudioDataToWav {
    public static void main(String[] args) {
        String csvFile = "D:\\IDEA\\java code\\TTS\\src\\audio_data.csv";
        String wavFile = "D:\\IDEA\\java code\\TTS\\src\\output.wav";
        float sampleRate = 22050; // 根据实际采样率设置

        try {
            // 读取CSV文件中的音频数据
            List<float[]> audioDataList = new ArrayList<>();
            BufferedReader br = new BufferedReader(new FileReader(csvFile));
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                float[] floatValues = new float[values.length];
                for (int i = 0; i < values.length; i++) {
                    floatValues[i] = Float.parseFloat(values[i]);
                }
                audioDataList.add(floatValues);
            }
            br.close();

            // 将音频数据转换为字节数组
            int numChannels = audioDataList.get(0).length; // 判断是单声道还是立体声
            int numFrames = audioDataList.size();
            byte[] audioBytes = new byte[numFrames * numChannels * 2]; // 16-bit PCM data
            int byteIndex = 0;
            for (float[] frame : audioDataList) {
                for (float sample : frame) {
                    int intSample = (int) (sample * 32767); // 将浮点数转换为16-bit整数
                    audioBytes[byteIndex++] = (byte) (intSample & 0xFF);
                    audioBytes[byteIndex++] = (byte) ((intSample >> 8) & 0xFF);
                }
            }

            // 设置音频格式
            AudioFormat format = new AudioFormat(sampleRate, 16, numChannels, true, false);
            ByteArrayInputStream bais = new ByteArrayInputStream(audioBytes);
            AudioInputStream audioInputStream = new AudioInputStream(bais, format, numFrames);

            // 将音频数据写入WAV文件
            AudioSystem.write(audioInputStream, AudioFileFormat.Type.WAVE, new File(wavFile));

            System.out.println("WAV文件已生成: " + wavFile);
        } catch (IOException | NumberFormatException e) {
            e.printStackTrace();
        }
    }
}
