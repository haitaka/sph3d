package me.haitaka.sph3d;

public class Point {

    final double x;
    final double y;
    final double z;

    Point(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public boolean equals(Object that) {
        if (that instanceof Point) {
            Point thatPoint = (Point) that;
            return x == thatPoint.x && y == thatPoint.y && z == thatPoint.z;
        } else {
            return false;
        }
    }
}
