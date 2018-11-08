package me.haitaka.sph3d;

import java.util.function.Function;

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


        for (int i = 0; i < old_grid.x_size; ++i) {
            for (int j = 0; j < old_grid.y_size; ++j) {
                for (int k = 0; k < old_grid.z_size; ++k) {
                    Cell cell = old_grid.cells.get(i).get(j).get(k);
                    for (Particle particle : cell.get_all_particles()) {
                        System.out.println("dens: " + find_density(particle, cell));
                    }
                }
            }
        }
        return nextState;
    }

    public static void main(String[] args) {

        long startTime = System.nanoTime();

        // TODO rename me!!
        ParticlesState state = initital_state();

        for (int i = 0; i < 10; ++i) {
            state = do_time_step(state);
        }

        System.out.println("Done!");

        long finishTime = System.nanoTime();

        double executionTime = finishTime - startTime;
        System.out.println("Finished in " + executionTime + " seconds.");

    }
}
