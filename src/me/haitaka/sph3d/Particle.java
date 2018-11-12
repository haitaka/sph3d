package me.haitaka.sph3d;

import me.haitaka.sph3d.utils.Ref;

class Particle {
    
    enum Kind {
        Gas,
        Dust
    }
    
    Kind kind;

    double vx;
    double vy;
    double vz;

    double mass;

    double pressure;
    double density;
    double energy;

    double x;
    double y;
    double z;

    Particle() { // TODO
        x = 42;
        y = 42;
        z = 42;
        mass = 43;
        vx = 42;
        vy = 42;
        vz = 42;
        pressure = 23;
        density = 23;
        energy = 23;
    }
    
    Particle(Kind kind, double x, double y, double z) {
        this.kind = kind;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    Particle(Ref<Particle> that) {
        this(that.get().kind, that.get().x, that.get().y, that.get().z);
        mass = that.get().mass;
        pressure = that.get().pressure;
        density = that.get().density;
        energy = that.get().energy;
        vx = that.get().vx;
        vy = that.get().vy;
        vz = that.get().vz;
    }

    void set_coordinates(double new_x, double new_y, double new_z) {
        x = new_x;
        y = new_y;
        z = new_z;
    }

}
