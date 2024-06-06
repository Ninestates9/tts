# 软件工程课设-tts模型部署  
## 项目名称  
基于VITS的TTS模型移动部署  
## 项目成员  
队长：吴\*霖  
队员：宋\*  
队员：王\*悦  
## 项目目标  
1.熟悉使用Pytorch  
2.了解模型结构和输入输出  
3.Python下使用模型进行推理或预测  
4.对模型进行量化  
5.确定部署方案  
6.转换模型  
7.移动端部署和使用  
## 项目文件夹说明  
Android：Android Studio相关新建文件，不包括工程自动生成文件  
processor：Java实现的前处理（字符串转向量）、模型推理（Onnxruntime引擎）以及后处理（向量转wav文件）  
model_use：VITS模型python使用与处理，包括pytorch模型使用、模型转onnx格式python代码、onnx格式模型动态量化代码、onnx格式模型python使用等等
## 项目技术栈  
1.训练框架：PyTorch  
2.推理引擎：Onnxruntime  
3.安卓端语言：Java  
4.量化方法：Onnx模型动态量化  
## 参数及onnx格式模型下载地址  
[点击跳转HuggingFace](https://huggingface.co/datasets/Ninestates9/vits_parameter)  

