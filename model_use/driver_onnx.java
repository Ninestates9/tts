package tts.model_use;

import java.nio.FloatBuffer;
import java.util.Map;

import ai.onnxruntime.*

public class driver_onnx {
    public static void main(String[] args) {
        var env = OrtEnvironment.getEnvironment();
        var session = env.createSession("model.onnx",new OrtSession.SessionOptions());

        int[][] x={{0, 22, 0, 17, 0, 65, 0, 66, 0, 30, 0, 33, 0, 48, 0, 65, 0, 66, 0, 1, 0, 67, 0, 25, 0, 57, 0, 42, 0, 57, 0, 65, 0, 26, 0, 35, 0, 55, 0, 17, 0, 41, 0, 65, 0, 3, 0}};
        var t_x = OnnxTensor.createTensor(env,x);
        int[] x_lengths = {x[0].length};
        var t_x_lengths = OnnxTensor.createTensor(env,x_lengths); 
        int[] sid = {162};
        var t_sid = OnnxTensor.createTensor(env,sid);
        double[] noise_scale = {0.1};
        var t_noise_scale = OnnxTensor.createTensor(env,noise_scale);
        double[] noise_scale_w = {0.668};
        var t_noise_scale_w = OnnxTensor.createTensor(env,noise_scale_w);
        int[] length_scale = {1};
        var t_x_length_scale = OnnxTensor.createTensor(env,length_scale);
        var inputs = Map.of("x",x,"x_lengths",x_lengths, "sid", sid, "noise_scale", noise_scale, "length_scale", length_scale, "noise_scale_w", noise_scale_w);
        try (var results = session.run(inputs)) {
            float[][][] result = (float[][][])results.get(0).getValue();
            float[] audio = result[0][0];
        }
    }
}
