package me.haitaka.sph3d;

import java.util.ArrayList;

import static java.lang.Math.ceil;
import static java.lang.Math.floor;
import static me.haitaka.sph3d.Cell.NO_CELL;

class Grid {

    // static final Grid NO_GRID = new Grid(-1, -1, -1, null, null);

    final ArrayList<ArrayList<ArrayList<Cell>>> cells;

    final double step_x;
    final double step_y;
    final double step_z;

    final int x_size;
    final int y_size;
    final int z_size;

    Point border1;
    Point border2;

    Grid(double step_x, double step_y, double step_z, Point border1, Point border2) {
        assert (border1.x <= border2.x);
        assert (border1.y <= border2.y);
        assert (border1.z <= border2.z);

        this.step_x = step_x;
        this.step_y = step_y;
        this.step_z = step_z;

        this.border1 = border1;
        this.border2 = border2;

        x_size = (int) ceil((border2.x - border1.x) / step_x); // TODO maybe support last cell of different size?
        y_size = (int) ceil((border2.y - border1.y) / step_y);
        z_size = (int) ceil((border2.z - border1.z) / step_z);

        cells = new ArrayList<>();
        for (int i = 0; i < x_size; ++i) {
            cells.add(i, new ArrayList<>());
            for (int j = 0; j < y_size; ++j) {
                cells.get(i).add(j, new ArrayList<>());
                for (int k = 0; k < z_size; ++k) {
                    cells.get(i).get(j).add(k, new Cell(this, i, j, k));
                }
            }
        }
    }

    Cell find_cell(Particle particle) {
        if (!encloses_point(new Point(particle.x, particle.y, particle.z))) {
            return NO_CELL;
        }

        double x_relative = particle.x - border1.x;
        double y_relative = particle.y - border1.y;
        double z_relative = particle.z - border1.z;

        int i = (int) floor(x_relative / step_x);
        int j = (int) floor(y_relative / step_y);
        int k = (int) floor(z_relative / step_z);

        return cells.get(i).get(j).get(k);
    }

    private boolean encloses_point(Point point) {
        if (point.x < border1.x || border2.x <= point.x) return false;
        if (point.y < border1.y || border2.y <= point.y) return false;
        if (point.z < border1.z || border2.z <= point.z) return false;
        return true;
    }
}
