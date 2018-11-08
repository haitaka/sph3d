package me.haitaka.sph3d;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

class Cell {

    static final Cell NO_CELL = new Cell(null, -1, -1, -1);
    final ArrayList<GasParticle> gas_particles = new ArrayList<>();
    final ArrayList<DustParticle> dust_particles = new ArrayList<>();
    private final Grid grid;
    private final int i;
    private final int j;
    private final int k;

    Cell(Grid grid, int i, int j, int k) {
        this.grid = grid;
        this.i = i;
        this.j = j;
        this.k = k;
    }

    void add_particle(Particle particle) {
        if (particle instanceof GasParticle) {
            gas_particles.add((GasParticle) particle);
        } else {
            dust_particles.add((DustParticle) particle);
        }
    }

    void remove_particle(Particle particle) {
        if (particle instanceof GasParticle) {
            gas_particles.remove((GasParticle) particle);
        } else {
            dust_particles.remove((DustParticle) particle);
        }
    }

    Collection<Particle> get_all_particles() {
        ArrayList<Particle> all_particles = new ArrayList<>();
        all_particles.addAll(gas_particles);
        all_particles.addAll(dust_particles);
        return all_particles;
    }

    Collection<Cell> get_neighbours() {
        int offset_x = (int) Math.ceil(Params.get_instance().smooth_radius / grid.step_x);
        int offset_y = (int) Math.ceil(Params.get_instance().smooth_radius / grid.step_y);
        int offset_z = (int) Math.ceil(Params.get_instance().smooth_radius / grid.step_z);

        List<Cell> neighbours = new ArrayList<>();

        for (int id_x = i - offset_x; id_x < i + offset_x; ++id_x) {
            if (0 <= id_x || id_x < grid.x_size) {
                for (int id_y = j - offset_y; id_y < j + offset_y; ++id_y) {
                    if (0 <= id_y || id_y < grid.y_size) {
                        for (int id_z = k - offset_z; id_z < k + offset_z; ++id_z) {
                            if (0 <= id_z || id_z < grid.z_size) {
                                neighbours.add(grid.cells.get(id_x).get(id_y).get(id_z));
                            }
                        }
                    }
                }
            }
        }

        return neighbours;
    }
}
