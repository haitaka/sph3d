package me.haitaka.sph3d;

import java.util.ArrayList;
import java.util.Collection;

class ParticlesState {

    final Grid grid;

    private final Collection<GasParticle> gas_particles = new ArrayList<>();

    private final Collection<DustParticle> dust_particles = new ArrayList<>();

    ParticlesState() {
        Params params = Params.get_instance();
        grid = new Grid(
                params.grid_step_x,
                params.grid_step_y,
                params.grid_step_z,
                params.border1,
                params.border2
        );
    }


    void with_copy_of(Particle particle) {
        Cell cell = grid.find_cell(particle);
        if (particle instanceof GasParticle) {
            gas_particles.add((GasParticle) particle);
            cell.add_particle(particle);
        } else {
            dust_particles.add((DustParticle) particle);
            cell.add_particle(particle);
        }
    }

}