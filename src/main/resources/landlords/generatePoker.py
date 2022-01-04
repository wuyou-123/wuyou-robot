# coding=UTF-8

import sys

from PIL import Image

ims = []
for i in sys.argv[3:]:
    ims.append(Image.open(sys.argv[1] + i + '.jpg'))
width = 25

# 创建空白长图
result = Image.new(ims[0].mode, (width * len(ims) + 80, 150))

# 拼接图片
for i, im in enumerate(ims):
    result.paste(im, box=(i * width, 0))

# 保存图片
result.save(sys.argv[2])
