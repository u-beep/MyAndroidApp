"""
一键生成 APP 图标到所有 mipmap 文件夹
使用方法：把你的图片路径填到下面的 SOURCE_IMAGE 变量中，然后运行此脚本
"""
from PIL import Image
import os

# ====== 在这里填写你的图片路径 ======
SOURCE_IMAGE = r"C:\你的图片路径.png"
# ====================================

RES_DIR = r"d:\Users\zxy\AndroidStudioProjects\MyApplication\app\src\main\res"

# Android 图标尺寸对照表
SIZES = {
    "mipmap-mdpi": 48,
    "mipmap-hdpi": 72,
    "mipmap-xhdpi": 96,
    "mipmap-xxhdpi": 144,
    "mipmap-xxxhdpi": 192,
}

def generate_icons(source_path):
    if not os.path.exists(source_path):
        print(f"❌ 找不到图片：{source_path}")
        print("请把 SOURCE_IMAGE 改成你图片的真实路径！")
        return

    img = Image.open(source_path)
    print(f"✅ 已打开图片，原始尺寸：{img.size}")

    for folder, size in SIZES.items():
        target_dir = os.path.join(RES_DIR, folder)
        os.makedirs(target_dir, exist_ok=True)

        resized = img.resize((size, size), Image.LANCZOS)
        target_path = os.path.join(target_dir, "ic_launcher.webp")
        resized.save(target_path, "WEBP")
        print(f"✅ 已生成：{folder}/ic_launcher.webp ({size}x{size})")

    print("\n🎉 所有图标生成完毕！卸载旧APP后重新运行即可生效。")

if __name__ == "__main__":
    generate_icons(SOURCE_IMAGE)
