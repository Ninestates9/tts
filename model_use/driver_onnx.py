from text import text_to_sequence
import soundfile as sf
import onnxruntime

symbols = ["_", ",", ".", "!", "?", "-", "~", "…", "N", "Q", "a", "b", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "s", "t", "u", "v", "w", "x", "y", "z", "ɑ", "æ", "ʃ", "ʑ", "ç", "ɯ", "ɪ", "ɔ", "ɛ", "ɹ", "ð", "ə", "ɫ", "ɥ", "ɸ", "ʊ", "ɾ", "ʒ", "θ", "β", "ŋ", "ɦ", "⁼", "ʰ", "`", "^", "#", "*", "=", "ˈ", "ˌ", "→", "↓", "↑", " "]
text_cleaners = ["cjke_cleaners2"]
add_blank = True

onnx_model = onnxruntime.InferenceSession("./tts/model_use/pretrained_models/G_trilingual.onnx")

def intersperse(lst, item):
  result = [item] * (len(lst) * 2 + 1)
  result[1::2] = lst
  return result

def get_text(text):

    text_norm = text_to_sequence(text, symbols, text_cleaners)
    if add_blank:
        text_norm = intersperse(text_norm, 0)
    return text_norm


def vits(text, speaker_id, noise_scale, noise_scale_w, length_scale):
    if not len(text):
        return "输入文本不能为空！", None, None
    text = text.replace("\n", " ").replace("\r", "").replace(" ", "")
    if len(text) > 10000:
        return f"输入文字过长！{len(text)}>100", None, None
    text = "[ZH]" + text + "[ZH]"
    
    stn_tst = get_text(text)
    x_tst = [stn_tst]# 升维用， 理解为外边套一层， 比如一维数组[1, 2]变成[[1, 2]]
    x_tst_lengths = [len(x_tst[0])]
    inputs = {onnx_model.get_inputs()[0].name: x_tst, onnx_model.get_inputs()[1].name: x_tst_lengths, 
                onnx_model.get_inputs()[2].name: [speaker_id], "noise_scale":[noise_scale], "length_scale":[length_scale],
                "noise_scale_w":[noise_scale_w]}
    audio = onnx_model.run(None, inputs)[0][0, 0]
    print(audio)
    
    return audio


audio = vits('''你好，世界！''', 162, 0.1, 0.668, 1)
sf.write("./tts/model_use/video/onnx2.wav", audio, samplerate=22050)