import math
from PIL import Image

BASE = r"D:\minecraft_mod_dev\Peyro-Scythes-1201\PeyroScythe1201\src\main\resources\assets\peyroscythe\textures\entity\mundus"
GUI = r"D:\minecraft_mod_dev\Peyro-Scythes-1201\PeyroScythe1201\src\main\resources\assets\peyroscythe\textures\gui\spell_icons"
BLOCK = r"D:\minecraft_mod_dev\Peyro-Scythes-1201\PeyroScythe1201\src\main\resources\assets\peyroscythe\textures\block"

W = (255, 253, 242, 255)
I = (255, 243, 196, 255)
P = (255, 224, 138, 255)
G = (255, 210, 90, 255)
A = (242, 181, 68, 255)
D = (232, 166, 42, 255)
B = (201, 138, 34, 255)
K = (143, 92, 20, 255)
DARK = (26, 20, 8, 255)
CLEAR = (0, 0, 0, 0)


def new():
    return Image.new("RGBA", (16, 16), CLEAR)


def orb_core():
    img = new()
    px = img.load()
    cx = cy = 7.5
    for y in range(16):
        for x in range(16):
            d = math.hypot(x - cx, y - cy)
            if d > 6.9:
                continue
            if d < 1.5:
                c = W
            elif d < 2.8:
                c = I
            elif d < 4.2:
                c = P
            elif d < 5.5:
                c = G
            else:
                c = A
            if x + y >= 20 and d > 4.0:
                c = D
            if d > 6.0:
                c = A
            px[x, y] = c
    # 左上高光
    px[5, 4] = W
    px[6, 4] = W
    px[4, 5] = I
    px[4, 6] = I
    img.save(BASE + r"\orb_core.png")


def orb_glow():
    img = new()
    px = img.load()
    cx = cy = 7.5
    for y in range(16):
        for x in range(16):
            d = math.hypot(x - cx, y - cy)
            if d < 2.2:
                px[x, y] = (255, 247, 214, 235)
            elif d < 3.6:
                px[x, y] = (255, 228, 154, 165)
            elif d < 5.0:
                px[x, y] = (255, 207, 102, 95)
            elif d < 6.5 and (x + y) % 2 == 0:
                px[x, y] = (255, 210, 90, 45)
    img.save(BASE + r"\orb_glow.png")


def rune_ring():
    img = new()
    px = img.load()
    cx = cy = 7.5
    for y in range(16):
        for x in range(16):
            d = math.hypot(x - cx, y - cy)
            if 5.9 <= d <= 6.7:
                ang = math.atan2(y - cy, x - cx)
                slot = int(((ang + math.pi) / (2 * math.pi)) * 24)
                if slot % 2 == 0:
                    px[x, y] = G if d < 6.4 else A
            elif 3.4 <= d <= 4.0:
                ang = math.atan2(y - cy, x - cx)
                slot = int(((ang + math.pi) / (2 * math.pi)) * 16)
                if slot % 2 == 0:
                    px[x, y] = D
    # 四个方位的白色符文菱形
    for ang_deg in (0, 90, 180, 270):
        ang = math.radians(ang_deg)
        x = int(round(cx + math.cos(ang) * 6.4))
        y = int(round(cy + math.sin(ang) * 6.4))
        if 0 <= x < 16 and 0 <= y < 16:
            px[x, y] = W
            for dx, dy in ((1, 0), (-1, 0), (0, 1), (0, -1)):
                if 0 <= x + dx < 16 and 0 <= y + dy < 16:
                    px[x + dx, y + dy] = I
    img.save(BASE + r"\rune_ring.png")


def spark():
    img = new()
    px = img.load()
    for i in range(1, 15):
        dist = abs(i - 7.5)
        if dist < 1:
            c = W
        elif dist < 2.5:
            c = I
        elif dist < 4:
            c = P
        else:
            c = G
        if 2 <= i <= 13:
            px[7, i] = c
            px[8, i] = c
            px[i, 7] = c
            px[i, 8] = c
    px[7, 7] = W
    px[8, 7] = W
    px[7, 8] = W
    px[8, 8] = W
    for x, y in ((6, 6), (9, 6), (6, 9), (9, 9)):
        px[x, y] = A
    img.save(BASE + r"\spark.png")


def flash():
    img = new()
    px = img.load()
    cx = cy = 7.5
    for y in range(16):
        for x in range(16):
            d = math.hypot(x - cx, y - cy)
            if d < 2.5:
                px[x, y] = (255, 255, 255, 255)
            elif d < 4.5:
                px[x, y] = (255, 253, 245, 220)
            elif d < 6.5:
                px[x, y] = (255, 246, 224, 140)
            elif (x + y) % 2 == 0:
                px[x, y] = (255, 240, 210, 60)
    img.save(BASE + r"\flash.png")


def spell_icon():
    img = Image.new("RGBA", (16, 16), DARK)
    px = img.load()
    cx = cy = 7.5
    # 暖色背景辉光
    for y in range(16):
        for x in range(16):
            d = math.hypot(x - cx, y - cy)
            if d < 6.5:
                px[x, y] = K if d > 4.5 else B
            if d < 4.0:
                px[x, y] = D
    # 小光球
    for y in range(16):
        for x in range(16):
            d = math.hypot(x - cx, y - cy)
            if d < 2.2:
                px[x, y] = W
            elif d < 3.2:
                px[x, y] = P
            elif d < 3.9:
                px[x, y] = G
    # 符文环
    for y in range(16):
        for x in range(16):
            d = math.hypot(x - cx, y - cy)
            if 5.9 <= d <= 6.6:
                ang = math.atan2(y - cy, x - cx)
                slot = int(((ang + math.pi) / (2 * math.pi)) * 24)
                if slot % 2 == 0:
                    px[x, y] = G
    for ang_deg in (45, 135, 225, 315):
        ang = math.radians(ang_deg)
        x = int(round(cx + math.cos(ang) * 6.2))
        y = int(round(cy + math.sin(ang) * 6.2))
        if 0 <= x < 16 and 0 <= y < 16:
            px[x, y] = W
    img.save(GUI + r"\mundus.png")


def block_texture():
    img = new()
    px = img.load()
    cx = cy = 7.5
    for y in range(16):
        for x in range(16):
            d = math.hypot(x - cx, y - cy)
            if d < 3:
                px[x, y] = G
            elif d < 5:
                px[x, y] = A
            elif d < 6.5 and (x + y) % 2 == 0:
                px[x, y] = D
    img.save(BLOCK + r"\mundus.png")


if __name__ == "__main__":
    orb_core()
    orb_glow()
    rune_ring()
    spark()
    flash()
    spell_icon()
    block_texture()
    print("done")
