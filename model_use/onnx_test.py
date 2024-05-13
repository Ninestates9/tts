import onnx
import torch

a = torch.rand(3, 2, 4, 4)

print(a)
print(a[0, 0])

onnx_model = onnx.load("./tts/model_use/pretrained_models/G_trilingual.onnx")
onnx.checker.check_model(onnx_model)
