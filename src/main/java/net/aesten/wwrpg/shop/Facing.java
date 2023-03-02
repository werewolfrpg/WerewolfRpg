package net.aesten.wwrpg.shop;

public enum Facing {
    S(0f),
    SW(45f),
    W(90f),
    NW(135f),
    N(180f),
    NE(225f),
    E(270f),
    SE(315f);

    private final Float yaw;
    Facing(Float yaw) {
        this.yaw = yaw;
    }

    public Float getYaw() {
        return yaw;
    }
}
