"""把 docs/mundus_sprite_sheet_1024.png 裁成 8 张 16x16 贴图。

精灵在网格里并不严格对齐，所以按 4 列 x 2 行的实际包围盒裁切，
再用 BOX 降采样 + 调色板量化 + alpha 阈值/阶梯化，得到像素风小图。
"""
import os
from PIL import Image
import numpy as np

BASE = os.path.dirname(os.path.abspath(__file__))
SRC = os.path.join(BASE, "mundus_sprite_sheet_1024.png")
OUT = os.path.join(BASE, "converted")

COLS = [(31, 233), (279, 488), (550, 730), (797, 993)]
ROWS = [(39, 245), (284, 497)]
NAMES = [
    ["orb_core", "orb_glow", "rune_ring", "spark"],
    ["flash", "spell_icon", "light_beam", "halo_cross"],
]
SOFT = {"orb_glow", "flash", "halo_cross"}
OPAQUE = {"spell_icon"}

PALETTE = np.array([
    (255, 253, 242),  # warm white
    (255, 243, 196),  # ivory
    (255, 224, 138),  # pale gold
    (255, 210, 90),   # gold
    (242, 181, 68),   # amber
    (232, 166, 42),   # deep amber
    (201, 138, 34),   # bronze
    (143, 92, 20),    # dark bronze
    (26, 20, 8),      # near-black brown (icon only)
], dtype=np.float32)

FLASH_PALETTE = PALETTE[:2]  # 白闪只用暖白/象牙


def map_palette(rgb, palette=PALETTE):
    d = ((rgb[:, :, None, :].astype(np.float32) - palette[None, None, :, :]) ** 2).sum(axis=-1)
    idx = d.argmin(axis=-1)
    return palette.astype(np.uint8)[idx]


def fill_transparent(img):
    """用不透明像素的平均色填充透明像素，避免降采样时出现黑边。"""
    arr = np.array(img).astype(np.float32)
    alpha = arr[:, :, 3]
    mask = alpha > 16
    if not mask.any():
        return img
    mean = arr[:, :, :3][mask].mean(axis=0)
    arr[:, :, :3][~mask] = mean
    return Image.fromarray(arr.astype(np.uint8), "RGBA")


def crop_square(img, box, pad=8):
    x0, y0, x1, y1 = box
    cx, cy = (x0 + x1) // 2, (y0 + y1) // 2
    size = max(x1 - x0, y1 - y0) + pad * 2
    left, top = cx - size // 2, cy - size // 2
    canvas = Image.new("RGBA", (size, size), (0, 0, 0, 0))
    canvas.paste(img.crop((left, top, left + size, top + size)), (0, 0))
    return canvas


def process(cell, name):
    arr = np.array(cell)
    alpha = arr[:, :, 3]
    ys, xs = np.where(alpha > 16)
    box = (int(xs.min()), int(ys.min()), int(xs.max()) + 1, int(ys.max()) + 1)
    square = crop_square(cell, box)
    square = fill_transparent(square)
    small = square.resize((16, 16), Image.Resampling.BOX)
    data = np.array(small).astype(np.float32)

    rgb = data[:, :, :3].astype(np.uint8)
    a = data[:, :, 3]

    quant = Image.fromarray(rgb, "RGB").quantize(
        colors=8, method=Image.Quantize.MEDIANCUT
    ).convert("RGB")
    rgb = np.array(quant).astype(np.uint8)
    palette = FLASH_PALETTE if name == "flash" else PALETTE
    rgb = map_palette(rgb.astype(np.float32), palette)

    if name in SOFT:
        steps = np.array([0, 24, 48, 72, 104, 136, 176, 216, 255], dtype=np.uint8)
        idx = np.abs(a[:, :, None] - steps[None, None, :]).argmin(axis=2)
        a = steps[idx].copy()
        a[a < 24] = 0
    else:
        a = np.where(a > 110, 255, 0).astype(np.uint8)
        if name in OPAQUE:
            a[:] = 255
        rgb[a == 0] = 0

    out = np.dstack([rgb, a]).astype(np.uint8)
    path = os.path.join(OUT, name + ".png")
    Image.fromarray(out, "RGBA").save(path)
    return path


def montage(paths):
    scale = 8
    pad = 8
    w = 4 * (16 * scale + pad) + pad
    h = 2 * (16 * scale + pad) + pad
    canvas = Image.new("RGBA", (w, h), (40, 40, 46, 255))
    for i, p in enumerate(paths):
        img = Image.open(p).convert("RGBA").resize(
            (16 * scale, 16 * scale), Image.Resampling.NEAREST
        )
        x = pad + (i % 4) * (16 * scale + pad)
        y = pad + (i // 4) * (16 * scale + pad)
        canvas.alpha_composite(img, (x, y))
    out = os.path.join(OUT, "_montage.png")
    canvas.save(out)
    return out


if __name__ == "__main__":
    os.makedirs(OUT, exist_ok=True)
    sheet = Image.open(SRC).convert("RGBA")
    paths = []
    for r, (y0, y1) in enumerate(ROWS):
        for c, (x0, x1) in enumerate(COLS):
            cell = sheet.crop((x0, y0, x1, y1))
            paths.append(process(cell, NAMES[r][c]))
    print("written", len(paths))
    print(montage(paths))
