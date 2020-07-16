package com.nibiru.creator.data;

public class Vec3 {
    private float x;
    private float y;
    private float z;

    public Vec3(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getZ() {
        return z;
    }

    public void setZ(float z) {
        this.z = z;
    }

    @Override
    public String toString() {
        return "Vec3{" +
                "x=" + x +
                ", y=" + y +
                ", z=" + z +
                '}';
    }

    private void normalize() {
        double length = x * x + y * y + z * z;

        if (length == 0) {
            return;
        }

        length = 1.0 / Math.sqrt(length);

        x = (float) (x * length);
        y = (float) (y * length);
        z = (float) (z * length);
    }

    private double dotProduct(Vec3 other) {
        return x * other.x + y * other.y + z * other.z;
    }

    private double checkEdge(double value) {
        if (value > 1.0000000) {
            value = 1.000000;
        }

        if (value < -1.0000000) {
            value = -1.000000;
        }

        return value;
    }

    public Vec2 getPitchHeadAngleValue() {
        float pitch, head;
        Vec3 dir = new Vec3(x, y, z);

        Vec3 xzProjection = new Vec3(x, 0, z);
        Vec3 xAxis = new Vec3(1, 0, 0);

        dir.normalize();
        xzProjection.normalize();

        //俯仰角的cos数值
        double cosValue1 = dir.dotProduct(xzProjection);
        checkEdge(cosValue1);
        double angleValue1 = Math.acos(cosValue1);
        pitch = (float) (angleValue1 / Math.PI * 180.0);

        if (y < 0) {
            pitch *= -1.0;
        }

        //航向角的cos数值
        double cosValue2 = xAxis.dotProduct(xzProjection);
        checkEdge(cosValue2);
        double angleValue2 = Math.acos(cosValue2);
        head = (float) (angleValue2 / Math.PI * 180.0);

        if (z < 0) {
            head = 360 - head;
        }
        return new Vec2(head, pitch);
    }
}
