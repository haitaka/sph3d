package me.haitaka.sph3d;

import me.haitaka.sph3d.utils.Ref;

public class Point {

    final double x;
    final double y;
    final double z;

    Point(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    Point(Ref<Point> that) {
        this(that.get().x, that.get().y, that.get().z);
    }

}
