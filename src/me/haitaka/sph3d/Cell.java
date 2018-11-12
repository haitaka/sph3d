package me.haitaka.sph3d;

import me.haitaka.sph3d.utils.Ref;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static me.haitaka.sph3d.utils.Utils.max;
import static me.haitaka.sph3d.utils.Utils.min;

class Cell {

    static final Cell NO_CELL = new Cell(null, -1, -1, -1);
    final ArrayList<Particle> gas_particles = new ArrayList<>();
    final ArrayList<Particle> dust_particles = new ArrayList<>();
    private final Ref<Grid> grid;
    private final int i;
    private final int j;
    private final int k;

    Cell(Ref<Grid> grid, int i, int j, int k) {
        this.grid = grid;
        this.i = i;
        this.j = j;
        this.k = k;
    }

    void add_copy_of_particle(Ref<Particle> particle) {
        particlesOfKind(particle.get().kind).get().add(new Particle(particle));
    }

    private Ref<Collection<Particle>> particlesOfKind(Particle.Kind kind) {
        switch(kind) {
            case Gas: return new Ref<>(gas_particles);
            case Dust: return new Ref<>(dust_particles);
        }
        throw new AssertionError();
    }

    Iterable<Ref<Particle>> get_all_particles() {
        return Stream.concat(gas_particles.stream(), dust_particles.stream()).map(Ref::new).collect(Collectors.toList());
    }

    Collection<Ref<Cell>> get_neighbours() {
        int offset_x = (int) Math.ceil(Params.get_instance().get().smooth_radius / grid.get().step_x);
        int offset_y = (int) Math.ceil(Params.get_instance().get().smooth_radius / grid.get().step_y);
        int offset_z = (int) Math.ceil(Params.get_instance().get().smooth_radius / grid.get().step_z);

        List<Ref<Cell>> neighbours = new ArrayList<>();

        int form_x = max(0, i - offset_x);
        int to_x = min(grid.get().x_size, i + offset_x);

        int form_y = max(0, j - offset_x);
        int to_y = min(grid.get().y_size, j + offset_y);

        int form_z = max(0, k - offset_x);
        int to_z = min(grid.get().z_size, k + offset_z);

        for (int id_x = form_x; id_x < to_x; ++id_x) {
            for (int id_y = form_y; id_y < to_y; ++id_y) {
                for (int id_z = form_z; id_z < to_z; ++id_z) {
                    neighbours.add(new Ref<>(grid.get().cells.get(id_x).get(id_y).get(id_z)));
                }
            }
        }

        return neighbours;
    }

    public void for_each_particle(Consumer<Ref<Particle>> f) {
        for (Ref<Particle> particle : get_all_particles()) {
            f.accept(particle);
        }
    }
}
