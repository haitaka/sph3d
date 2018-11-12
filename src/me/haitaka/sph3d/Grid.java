package me.haitaka.sph3d;

import me.haitaka.sph3d.utils.Ref;

import java.util.ArrayList;
import java.util.function.Consumer;
import java.util.function.Function;

import static java.lang.Math.ceil;
import static java.lang.Math.floor;
import static me.haitaka.sph3d.Cell.NO_CELL;

class Grid {

    final ArrayList<ArrayList<ArrayList<Cell>>> cells;

    final double step_x;
    final double step_y;
    final double step_z;

    final int x_size;
    final int y_size;
    final int z_size;

    Point border1;
    Point border2;

    Grid(double step_x, double step_y, double step_z, Ref<Point> border1, Ref<Point> border2) {
        this.border1 = new Point(border1);
        this.border2 = new Point(border2);

        assert (this.border1.x <= this.border2.x);
        assert (this.border1.y <= this.border2.y);
        assert (this.border1.z <= this.border2.z);

        this.step_x = step_x;
        this.step_y = step_y;
        this.step_z = step_z;

        x_size = (int) ceil((this.border2.x - this.border1.x) / step_x); // TODO maybe support last cell of different size?
        y_size = (int) ceil((this.border2.y - this.border1.y) / step_y);
        z_size = (int) ceil((this.border2.z - this.border1.z) / step_z);

        cells = new ArrayList<>();
        for (int i = 0; i < x_size; ++i) {
            cells.add(i, new ArrayList<>());
            for (int j = 0; j < y_size; ++j) {
                cells.get(i).add(j, new ArrayList<>());
                for (int k = 0; k < z_size; ++k) {
                    cells.get(i).get(j).add(k, new Cell(new Ref<>(this), i, j, k));
                }
            }
        }
    }

    Ref<Cell> find_cell(Ref<Particle> particle) {
        if (!encloses_point(new Point(particle.get().x, particle.get().y, particle.get().z))) {
            return new Ref<>(NO_CELL);
        }

        double x_relative = particle.get().x - border1.x;
        double y_relative = particle.get().y - border1.y;
        double z_relative = particle.get().z - border1.z;

        int i = (int) floor(x_relative / step_x);
        int j = (int) floor(y_relative / step_y);
        int k = (int) floor(z_relative / step_z);

        return new Ref<> (cells.get(i).get(j).get(k));
    }

    private boolean encloses_point(Point point) {
        if (point.x < border1.x || border2.x <= point.x) return false;
        if (point.y < border1.y || border2.y <= point.y) return false;
        if (point.z < border1.z || border2.z <= point.z) return false;
        return true;
    }

    public void for_each_cell(Consumer<Ref<Cell>> f) {
        for (int i = 0; i < x_size; ++i) {
            for (int j = 0; j < y_size; ++j) {
                for (int k = 0; k < z_size; ++k) {
                    Cell cell = cells.get(i).get(j).get(k);
                    f.accept(new Ref<>(cell));
                }
            }
        }
    }

}
