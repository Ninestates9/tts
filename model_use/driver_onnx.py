import onnx.checker
import torch
import utils
from models import SynthesizerTrn
from text import text_to_sequence
import commons
from torch import no_grad, LongTensor
import os
import soundfile as sf
import onnxruntime
import onnx
import numpy 
limitation = os.getenv("SYSTEM") == "spaces"

file_model = "./tts/model_use/pretrained_models/G_trilingual.pth"
hps_ms = utils.get_hparams_from_file("./tts/model_use/configs/uma_trilingual.json")

device = torch.device("cpu")

net_g_ms = SynthesizerTrn(
    len(hps_ms.symbols),
    hps_ms.data.filter_length // 2 + 1,
    hps_ms.train.segment_size // hps_ms.data.hop_length,
    n_speakers=hps_ms.data.n_speakers,
    **hps_ms.model)
_ = net_g_ms.eval().to(device)

speakers = hps_ms.speakers
model, optimizer, learning_rate, epochs = utils.load_checkpoint(file_model, net_g_ms, None)

onnx_model = onnxruntime.InferenceSession("./tts/model_use/pretrained_models/G_trilingual.onnx")

def get_text(text, hps):

    text_norm = text_to_sequence(text, hps_ms.symbols, hps.data.text_cleaners)
    if hps.data.add_blank:
        text_norm = commons.intersperse(text_norm, 0)
    text_norm = LongTensor(text_norm)
    return text_norm

def to_numpy(tensor):
    return tensor.numpy()


def vits(text, speaker_id, noise_scale, noise_scale_w, length_scale):
    if not len(text):
        return "输入文本不能为空！", None, None
    text = text.replace("\n", " ").replace("\r", "").replace(" ", "")
    if len(text) > 100 and limitation:
        return f"输入文字过长！{len(text)}>100", None, None
    text = "[ZH]" + text + "[ZH]"
    
    stn_tst = get_text(text, hps_ms)
    with no_grad():
        x_tst = stn_tst.unsqueeze(0).to(device)
        x_tst_lengths = torch.tensor([stn_tst.size(0)], dtype=torch.int64).to(device)
        speaker_id = torch.tensor([speaker_id], dtype=torch.int64).to(device)
        # audio = net_g_ms.infer(x_tst, x_tst_lengths, sid=speaker_id, noise_scale=noise_scale,
        #                        noise_scale_w=noise_scale_w, 
        #                        length_scale=length_scale)[0][0, 0].data.cpu().float().numpy()
        # audio = model.infer(x_tst, x_tst_lengths, sid=speaker_id, noise_scale=noise_scale,
        #                        noise_scale_w=noise_scale_w, 
        #                        length_scale=length_scale)[0][0, 0].data.cpu().float().numpy()
        inputs = {onnx_model.get_inputs()[0].name: to_numpy(x_tst), onnx_model.get_inputs()[1].name: to_numpy(x_tst_lengths), 
                onnx_model.get_inputs()[2].name: to_numpy(speaker_id), "noise_scale":numpy.array([noise_scale], dtype=numpy.float64), "length_scale":numpy.array([length_scale], dtype=numpy.int64),
                "noise_scale_w":numpy.array([noise_scale_w], dtype=numpy.float64)}
        onnx.checker.check_model("./tts/model_use/pretrained_models/G_trilingual.onnx")
        audio = onnx_model.run(None, inputs)[0][0, 0]
    
    # return 22050, audio
    return audio



# speed = 1
# fs, audio = vits("你好，世界！", torch.tensor([106]), 0.1, 0.668, speed)
# sf.write("./tts/model_use/video/onnx.wav", audio, samplerate=fs)
audio = vits("你好，世界！", torch.tensor([106]), 0.1, 0.668, 1)
sf.write("./tts/model_use/video/onnx.wav", audio, samplerate=22050)