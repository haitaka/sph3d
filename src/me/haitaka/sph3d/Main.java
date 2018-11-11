package me.haitaka.sph3d;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.function.Function;

import static java.lang.Math.*;
import static me.haitaka.sph3d.Common.kernel;
import static me.haitaka.sph3d.Common.random_double;

public class Main {


    private static Particle new_random_particle(Point border1, Point border2, Class<?> type) {

        Particle particle = (type == GasParticle.class) ? new GasParticle() : new DustParticle();

        double radius = (border2.x - border1.x) / 50;

        double center_x = (border2.x - border1.x) / 2;
        double center_y = (border2.y - border1.y) / 2;
        double center_z = (border2.z - border1.z) / 2;

        double x = center_x + random_double(-radius, radius);
        double y = center_y + random_double(-radius, radius);
        double z = center_z + random_double(-radius, radius);

        double factor = random_double(0, 0.2);
        double vx = (x - center_x) * factor / 100.;
        double vy = (y - center_y) * factor / 100.;
        double vz = (z - center_z) * factor / 100.;

        particle.set_coordinates(x, y, z);

        particle.vx = vx;
        particle.vy = vy;
        particle.vz = vz;

        return particle;
    }

    private static ParticlesState initital_state() {
        ParticlesState init_state = new ParticlesState();

        Params params = Params.get_instance();

        for (int i = 0; i < params.n_gas; ++i) {
            GasParticle particle = (GasParticle) new_random_particle(init_state.grid.border1, init_state.grid.border2, GasParticle.class);
            init_state.with_copy_of(particle);
        }
        for (int i = 0; i < params.n_dust; ++i) {
            DustParticle particle = (DustParticle) new_random_particle(init_state.grid.border1, init_state.grid.border2, DustParticle.class);
            init_state.with_copy_of(particle);
        }
        return init_state;
    }

    private static ParticlesState regular_init_state() {
        ParticlesState init_state = new ParticlesState();

        Params params = Params.get_instance();

        double radius = 0.2;
        double abs_velo = radius / 2;

        double center_x = (params.border2.x - params.border1.x) / 2;
        double center_y = (params.border2.y - params.border1.y) / 2;
        double center_z = (params.border2.z - params.border1.z) / 2;

        for(int i = 0; i < params.n_gas; i += 1) {
            double theta = acos(1 - 2 * (i + 0.5) / params.n_gas);
            double phi = PI * (1 + pow(5, 0.5)) * (i + 0.5);

            double rel_x = radius * cos(phi) * sin(theta);
            double rel_y = radius * sin(phi) * sin(theta);
            double rel_z = radius * cos(theta);

            GasParticle p = new GasParticle(rel_x + center_x, rel_y + center_y, rel_z + center_z);

            p.vx = rel_x / radius * abs_velo;
            p.vy = rel_y / radius * abs_velo;
            p.vz = rel_z / radius * abs_velo;

            p.mass = 1. / params.n_gas;

            // System.out.println("" + p.x + " " + p.y + " " + p.z);
            init_state.with_copy_of(p);
        }

        recalc_density(init_state);

        return init_state;
    }

    private static Point find_new_coordinates(Particle particle) {
        Params params = Params.get_instance();
        double x = particle.x + params.tau * particle.vx;
        double y = particle.y + params.tau * particle.vy;
        double z = particle.z + params.tau * particle.vz;
        return new Point(x, y, z);
    }

    private static double find_density(Particle particle, Cell cell) {
        double density = 0;

        for (Cell neighbour : cell.get_neighbours()) {
            Function<Particle, Double> f = (Particle p) -> p.mass * kernel(particle.x, p.x);

            if (particle instanceof GasParticle) {
                for (Particle other : neighbour.gas_particles) {
                    density += f.apply(other);
                }
            } else {
                for (Particle other : neighbour.dust_particles) {
                    density += f.apply(other);
                }
            }
        }

        return density;
    }

    private static ParticlesState do_time_step(ParticlesState old) {
        //char filename[512];
        //sprintf(filename, "/home/calat/CLionProjects/SPH3d/gas/part_%0d.dat", step_num);
        //FILE * f = fopen(filename, "w");

        ParticlesState nextState = new ParticlesState();

        Grid old_grid = old.grid;
        for (int i = 0; i < old_grid.x_size; ++i) {
            for (int j = 0; j < old_grid.y_size; ++j) {
                for (int k = 0; k < old_grid.z_size; ++k) {
                    Cell cell = old_grid.cells.get(i).get(j).get(k);
                    for (Particle particle : cell.get_all_particles()) {
                        Particle new_particle;
                        if (particle instanceof GasParticle) {
                            GasParticle as_gas = (GasParticle) particle;
                            new_particle = new GasParticle(as_gas);
                        } else {
                            DustParticle as_dust = (DustParticle) particle;
                            new_particle = new DustParticle(as_dust);
                        }
                        Point new_coords = find_new_coordinates(new_particle);
                        new_particle.set_coordinates(new_coords.x, new_coords.y, new_coords.z);

                        nextState.with_copy_of(new_particle);
                    }
                }
            }
        }

        recalc_density(old);

        return nextState;
    }

    private static void recalc_density(ParticlesState state) {
        final Grid grid = state.grid;
        for (int i = 0; i < grid.x_size; ++i) {
            for (int j = 0; j < grid.y_size; ++j) {
                for (int k = 0; k < grid.z_size; ++k) {
                    Cell cell = grid.cells.get(i).get(j).get(k);
                    for (Particle particle : cell.get_all_particles()) {
                        particle.density = find_density(particle, cell);
                    }
                }
            }
        }
    }

    public static void main(String[] args) throws FileNotFoundException {

        long startTime = System.nanoTime();

        //ParticlesState state = initital_state();
        ParticlesState state = regular_init_state();

        final String OUT_DIR = "/tmp/out";

        try (PrintWriter writer = new PrintWriter(OUT_DIR + "/plot_" + 0 + ".dat");) {
            state.print(writer);
        }
        for (int i = 0; i < 10; ++i) {
            state = do_time_step(state);

            try (PrintWriter writer = new PrintWriter(OUT_DIR + "/plot_" + (i + 1) + ".dat");) {
                state.print(writer);
            }
        }

        System.out.println("Done!");

        long finishTime = System.nanoTime();

        long executionTime = finishTime - startTime;
        System.out.println("Finished in " + (+
                executionTime / pow(10, 9)) + " seconds.");

    }
}
