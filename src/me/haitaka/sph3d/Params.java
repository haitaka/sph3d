package me.haitaka.sph3d;

import me.haitaka.sph3d.utils.Ref;

class Params {
    private static final Params INSTANCE = new Params();
    double t;
    double c_s;
    double K;
    double kx;
    double ky;
    double kz;
    int n_gas = 100;
    int n_dust = 0;
    double h = 0.1;
    double smooth_radius = 2. * h;
    double tau = 0.1;
    double middle_gas_dens;
    double d2g;
    double delta;
    // boundary of area
    Point border1 = new Point(0, 0, 0);
    Point border2 = new Point(1, 1, 1);
    double grid_step_x = 0.01;
    double grid_step_y = 0.01;
    double grid_step_z = 0.01;

    static Ref<Params> get_instance() {
        return new Ref<>(INSTANCE);
    }
}
