import torch
import utils
from models import SynthesizerTrn
from text import text_to_sequence
import commons
from torch import no_grad, LongTensor

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
torch_model, optimizer, learning_rate, epochs = utils.load_checkpoint(file_model, net_g_ms, None)

torch_model.forward = torch_model.infer

torch_model.eval()
 
x = torch.tensor([[2, 4, 11, 1, 2, 3, 4, 4]], dtype=torch.int64)
x_lengths = torch.tensor([1], dtype=torch.int64)
sid = torch.tensor([162], dtype=torch.int64)

inputs = (x, x_lengths, sid, 0.6, 1, 0.668)


export_onnx_file = "./tts/model_use/pretrained_models/G_trilingual.onnx" 				
torch.onnx.export(torch_model,
                    inputs,
                    export_onnx_file,
                    opset_version=13,
                    do_constant_folding=True,	
                    input_names=["x", "x_lengths", "sid", "noise_scale", "length_scale", "noise_scale_w"],		
                    output_names=["audio"],	
                    dynamic_axes={"x":{1:"text_length"},	
                                    "audio":{2:"audio_info"}})
 