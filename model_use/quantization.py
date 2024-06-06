from onnxruntime.quantization import QuantType, quantize_dynamic
from pathlib import Path

# 模型路径
onnx_fp64_dir = Path('./pretrained_models/G_trilingual_11.onnx')
onnx_quant_dynamic_dir = Path('./pretrained_models/G_trilingual_11_quant_dynamic.onnx')

# 动态量化
quantize_dynamic(
    model_input=onnx_fp64_dir,  # 输入模型
    model_output=onnx_quant_dynamic_dir,  # 输出模型
    weight_type=QuantType.QUInt8,  # 参数类型
)