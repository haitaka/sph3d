package me.haitaka.sph3d;

import me.haitaka.sph3d.utils.Ref;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.lang.ref.Reference;
import java.util.function.Function;

import static java.lang.Math.*;
import static me.haitaka.sph3d.Common.kernel;
import static me.haitaka.sph3d.Common.random_double;
import static me.haitaka.sph3d.Particle.Kind.Dust;
import static me.haitaka.sph3d.Particle.Kind.Gas;

public class Main {

    private static Particle new_random_particle(Particle.Kind kind, Point border1, Point border2) {

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

        Particle particle = new Particle(kind, x, y, z);

        particle.vx = vx;
        particle.vy = vy;
        particle.vz = vz;

        return particle;
    }

    private static ParticlesState random_init_state() {
        ParticlesState init_state = new ParticlesState();

        Ref<Params> params = Params.get_instance();

        for (int i = 0; i < params.get().n_gas; ++i) {
            Particle particle = new_random_particle(Gas, init_state.grid.border1, init_state.grid.border2);
            init_state.with_copy_of(new Ref<>(particle));
            Reference.reachabilityFence(particle);
        }
        for (int i = 0; i < params.get().n_dust; ++i) {
            Particle particle = new_random_particle(Dust, init_state.grid.border1, init_state.grid.border2);
            init_state.with_copy_of(new Ref<> (particle));
            Reference.reachabilityFence(particle);
        }
        return init_state;
    }

    private static ParticlesState regular_init_state() {
        ParticlesState init_state = new ParticlesState();

        Ref<Params> params = Params.get_instance();

        double radius = 0.2;
        double abs_velo = radius / 2;

        double center_x = (params.get().border2.x - params.get().border1.x) / 2;
        double center_y = (params.get().border2.y - params.get().border1.y) / 2;
        double center_z = (params.get().border2.z - params.get().border1.z) / 2;

        for(int i = 0; i < params.get().n_gas; i += 1) {
            double theta = acos(1 - 2 * (i + 0.5) / params.get().n_gas);
            double phi = PI * (1 + pow(5, 0.5)) * (i + 0.5);

            double rel_x = radius * cos(phi) * sin(theta);
            double rel_y = radius * sin(phi) * sin(theta);
            double rel_z = radius * cos(theta);

            Particle p = new Particle(Gas, rel_x + center_x, rel_y + center_y, rel_z + center_z);

            p.vx = rel_x / radius * abs_velo;
            p.vy = rel_y / radius * abs_velo;
            p.vz = rel_z / radius * abs_velo;

            p.mass = 1. / params.get().n_gas;

            // System.out.println("" + p.x + " " + p.y + " " + p.z);
            init_state.with_copy_of(new Ref<>(p));
            Reference.reachabilityFence(p);
        }

        recalc_density(init_state);

        return init_state;
    }

    private static Point find_new_coordinates(Ref<Particle> particle) {
        Params params = Params.get_instance().get();
        final double x = particle.get().x + params.tau * particle.get().vx;
        final double y = particle.get().y + params.tau * particle.get().vy;
        final double z = particle.get().z + params.tau * particle.get().vz;
        return new Point(x, y, z);
    }

    private static double find_density(Ref<Particle> particle, Ref<Cell> cell) {
        double density = 0;

        for (Ref<Cell> neighbour : cell.get().get_neighbours()) {
            for (Particle p : particle.get().kind == Gas ? neighbour.get().gas_particles : neighbour.get().dust_particles) { // TODO direct access
                density += p.mass * kernel(particle.get().x, p.x);
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
        for (int i = 0; i < old_grid.x_size; ++i) { // TODO foreach
            for (int j = 0; j < old_grid.y_size; ++j) {
                for (int k = 0; k < old_grid.z_size; ++k) {
                    Cell cell = old_grid.cells.get(i).get(j).get(k);
                    for (Ref<Particle> particle : cell.get_all_particles()) {
                        Particle new_particle = new Particle(particle);
                        Point new_coords = find_new_coordinates(new Ref<>(new_particle));
                        new_particle.set_coordinates(new_coords.x, new_coords.y, new_coords.z);

                        nextState.with_copy_of(new Ref<>(new_particle));
                        Reference.reachabilityFence(new_particle);
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
                    for (Ref<Particle> particle : cell.get_all_particles()) {
                        particle.get().density = find_density(particle, new Ref<>(cell));
                    }
                }
            }
        }
    }

    public static void main(String[] args) throws FileNotFoundException {

        final long startTime = System.nanoTime();

        //ParticlesState state = initital_state();
        ParticlesState state = regular_init_state();

        final String OUT_DIR = "/tmp/out";

        try (PrintWriter writer = new PrintWriter(OUT_DIR + "/plot_" + 0 + ".dat");) {
            state.print(writer);
        }
        for (int i = 0; i < 10; ++i) {
            final long stepStartTime = System.nanoTime();

            state = do_time_step(state);

            try (PrintWriter writer = new PrintWriter(OUT_DIR + "/plot_" + (i + 1) + ".dat");) {
                state.print(writer);
            }

            final long stepFinTime = System.nanoTime();
            System.out.println(i + " " + ((stepFinTime - stepStartTime) / pow(10, 9)) + " s");
        }

        System.out.println("Done!");

        final long finishTime = System.nanoTime();

        final long executionTime = finishTime - startTime;
        System.out.println("Finished in " + (executionTime / pow(10, 9)) + " seconds.");

    }
}
