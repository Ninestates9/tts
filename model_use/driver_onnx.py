import torch
import utils
from text import text_to_sequence
import commons
from torch import no_grad, LongTensor
import soundfile as sf
import onnxruntime
import numpy 
hps_ms = utils.get_hparams_from_file("./tts/model_use/configs/uma_trilingual.json")

device = torch.device("cpu")

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
    if len(text) > 10000:
        return f"输入文字过长！{len(text)}>100", None, None
    text = "[ZH]" + text + "[ZH]"
    
    stn_tst = get_text(text, hps_ms)
    with no_grad():
        x_tst = stn_tst.unsqueeze(0).to(device)
        x_tst_lengths = torch.tensor([stn_tst.size(0)], dtype=torch.int64).to(device)
        speaker_id = torch.tensor([speaker_id], dtype=torch.int64).to(device)
        inputs = {onnx_model.get_inputs()[0].name: to_numpy(x_tst), onnx_model.get_inputs()[1].name: to_numpy(x_tst_lengths), 
                onnx_model.get_inputs()[2].name: to_numpy(speaker_id), "noise_scale":numpy.array([noise_scale], dtype=numpy.float64), "length_scale":numpy.array([length_scale], dtype=numpy.int64),
                "noise_scale_w":numpy.array([noise_scale_w], dtype=numpy.float64)}
        audio = onnx_model.run(None, inputs)[0][0, 0]
    
    return audio


audio = vits('''孟夏之日，万物并秀。国家主席习近平11日上午结束对法国、塞尔维亚和匈牙利的国事访问后乘专机回到北京。此次欧洲之行，行程紧凑，内容丰富，成果丰硕，如浩荡东风，连接合作发展的热望，萌动繁荣进步的生机，为动荡不定的世界注入了浓浓暖意。
这是一次传承友谊、增进互信、提振信心和开辟未来之旅。习近平主席所到之处受到热烈欢迎、热情接待，从图尔马莱山口响起的牧羊人之歌，到塞尔维亚大厦广场的热情欢呼，再到匈牙利少女15年后再度向习近平主席献花的用心安排，一个个触动人心的温情瞬间，无不反映出往访国人民对习近平主席的崇高敬意，对中国人民的友好情谊以及对发展双边关系的美好期待。
“这是一次里程碑式的访问”“访问将对世界产生广泛深远的影响”……此访引发国内外高度关注，在欧洲国家政要、学者和民众中激起热烈反响。他们普遍认为习近平主席此访实现了中法关系再巩固，中塞关系再强化，中匈关系再提升，中欧合作再出发，其战略意义和深远影响远超双边范畴，展现了中国特色大国外交的积极进取，彰显了习近平主席作为大国领袖的担当和魅力。''', torch.tensor([162]), 0.1, 0.668, 1)
sf.write("./tts/model_use/video/onnx1.wav", audio, samplerate=22050)