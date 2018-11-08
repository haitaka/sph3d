package me.haitaka.sph3d;

import java.util.Random;

import static java.lang.Math.abs;
import static java.lang.Math.pow;

class Common {


    static double random_double(double from, double to) {
        return from + (to - from) * new Random().nextDouble();
    }

    static double kernel(double x_a, double x_b) {
        double h = Params.get_instance().h;
        double r = abs(x_a - x_b);
        double q = r / h;
        double result;

        if (r / h >= 0 && r / h <= 1) {
            result = 1 - 3. / 2 * pow(q, 2) + 3. / 4 * pow(q, 3);
            return 2. / 3. / h * result;
        }
        if (r / h > 1 && r / h <= 2) {
            result = 1. / 4 * pow((2. - q), 3);
            return 2. / 3. / h * result;
        }
        if (r / h > 2) {
            return 0;
        }
        throw new AssertionError();
    }

}
