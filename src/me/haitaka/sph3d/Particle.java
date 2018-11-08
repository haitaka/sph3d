package me.haitaka.sph3d;

class Particle {

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

    Particle(Particle another) {
        this.vx = another.vx;
        this.vy = another.vy;
        this.vz = another.vz;

        this.mass = another.mass;

        this.pressure = another.pressure;
        this.density = another.density;
        this.energy = another.energy;

        this.x = another.x;
        this.y = another.y;
        this.z = another.z;
    }

    void set_coordinates(double new_x, double new_y, double new_z) {
        x = new_x;
        y = new_y;
        z = new_z;
    }

}


class GasParticle extends Particle {
    GasParticle() {
    }

    GasParticle(GasParticle another) {
        super(another);
    }
}

class DustParticle extends Particle {
    DustParticle() {
    }

    DustParticle(DustParticle another) {
        super(another);
    }
}
