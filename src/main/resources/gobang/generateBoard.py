# coding=UTF-8

import random
import sys

import numpy as np
from PIL import Image, ImageDraw, ImageFont

image = Image.open(sys.argv[1])
width = image.size[0]
arr = eval(sys.argv[4])
# 创建绘制对象
draw = ImageDraw.Draw(image)
offset = 6.53
for colIndex, colArr in enumerate(arr):
    font = ImageFont.truetype(sys.argv[3], 15, encoding="unic")  # 设置字体
    for rowIndex, row in enumerate(colArr):
        x1 = width / 100 * (rowIndex * offset + 1.5)
        x2 = width / 100 * (colIndex * offset + 1.5)
        if row != 0:
            draw.ellipse((x1, x2, x1 + 30, x2 + 30), 'black' if row == 1 else 'white')
        if rowIndex == 0:
            draw.text((5, width / 100 * (colIndex * offset + 3)), chr(colIndex + 65), 'black', font)  # 画纵坐标
    draw.text((width / 100 * (colIndex * offset + 3), 5), str(colIndex), 'black', font)  # 画横坐标
if len(sys.argv) > 5:
    current = eval(sys.argv[5])
    x1 = width / 100 * (current[1] * offset + 1.5)
    x2 = width / 100 * (current[0] * offset + 1.5)
    draw.polygon((x1+10, x2+15, x1+15, x2+20, x1+20, x2+15, x1+15, x2+10), 'red', 'red')
# 改变随机点颜色
img_array = np.array(image)
for i in range(80):
    img_array[random.randint(i, width-1)][random.randint(i, width-1)] = (20, 20, 20)
image = Image.fromarray(np.uint8(img_array))

image.save(sys.argv[2])
# image.show()
