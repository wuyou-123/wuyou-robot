# coding=UTF-8

import random
import sys

import numpy as np
from PIL import Image

ims = []
for i in sys.argv[3:]:
    ims.append(Image.open(sys.argv[1] + i + '.jpg'))
width = 25

# 创建空白长图
result = Image.new("RGB", (width * len(ims) + 80, 150))

# 拼接图片
for i, im in enumerate(ims):
    result.paste(im, box=(i * width, 0))

# 改变随机点颜色
img_array = np.array(result)
for i in range(80):
    img_array[random.randint(i, 79)][random.randint(i, 104)] = (250, 250, 250)
result = Image.fromarray(np.uint8(img_array))

# 保存图片
result.save(sys.argv[2])
