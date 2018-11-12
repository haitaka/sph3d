package me.haitaka.sph3d;

import me.haitaka.sph3d.utils.Ref;

import java.io.PrintWriter;

class ParticlesState {

    final Grid grid;

    ParticlesState() {
        Ref<Params> params = Params.get_instance();
        grid = new Grid(
                params.get().grid_step_x,
                params.get().grid_step_y,
                params.get().grid_step_z,
                new Ref<>(params.get().border1),
                new Ref<>(params.get().border2)
        );
    }


    void with_copy_of(Ref<Particle> particle) {
        Ref<Cell> cell = grid.find_cell(particle);
        cell.get().add_copy_of_particle(particle);
    }

    void print(PrintWriter writer) {
        grid.for_each_cell(
                (c) -> c.get().for_each_particle(
                        (p) -> writer.println(p.get().x + " " + p.get().y + " " + p.get().z)
                )
        );
    }

}