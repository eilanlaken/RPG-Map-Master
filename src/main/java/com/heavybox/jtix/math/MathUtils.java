package com.heavybox.jtix.math;

import com.heavybox.jtix.collections.Array;
import com.heavybox.jtix.collections.ArrayFloat;
import com.heavybox.jtix.collections.ArrayInt;
import com.heavybox.jtix.collections.Collections;
import com.heavybox.jtix.memory.MemoryPool;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

public final class MathUtils {

    public static  final float   FLOAT_ROUNDING_ERROR = 0.000001f; // 32 bits
    public static  final float   PI                   = (float) Math.PI;
    public static  final float   PI_TWO               = PI * 2;
    public static  final float   PI_HALF              = PI / 2;
    public static  final float   E                    = (float) Math.E;
    public static  final float   radiansToDegrees     = 180f / PI;
    public static  final float   degreesToRadians     = PI / 180;
    private static final int     SIN_BITS             = 14; // 16KB. Adjust for accuracy.
    private static final int     SIN_MASK             = ~(-1 << SIN_BITS);
    private static final int     SIN_COUNT            = SIN_MASK + 1;
    private static final float   RADIANS_FULL         = PI_TWO;
    private static final float   DEGREES_FULL         = 360.0f;
    private static final float   RADIANS_TO_INDEX     = SIN_COUNT / RADIANS_FULL;
    private static final float   DEGREES_TO_INDEX     = SIN_COUNT / DEGREES_FULL;
    private static final Random  random               = new Random(System.currentTimeMillis());

    /* polygon triangulation */
    private static final MemoryPool<ArrayFloat> floatArrayPool  = new MemoryPool<>(ArrayFloat.class, 5);
    private static final MemoryPool<Vector2>    vectors2Pool    = new MemoryPool<>(Vector2.class, 5);
    private static final Array<Vector2>         polygonVertices = new Array<>(false, 10);
    private static final ArrayInt               indexList       = new ArrayInt();

    private MathUtils() {}

    /**
     * Returns a random integer uniformly in [a, b).
     *
     * @param  a the left endpoint
     * @param  b the right endpoint
     * @return a random integer uniformly in [a, b)
     * @throws MathException if {@code b - a >= Integer.MAX_VALUE}
     */
    public static int randomUniformInt(int a, int b) {
        if (((long) b - a >= Integer.MAX_VALUE)) throw new MathException("invalid range: [" + a + ", " + b + ")");
        if (b < a) {
            int c = a;
            a = b;
            b = c;
        }
        return a + random.nextInt(b - a);
    }

    /**
     * Returns a random real number uniformly in [a, b).
     *
     * @param  a the left endpoint
     * @param  b the right endpoint
     * @return a random real number uniformly in [a, b)
     * @throws MathException unless {@code a < b}
     */
    public static float randomUniformFloat(float a, float b) {
        if (b < a) {
            float c = a;
            a = b;
            b = c;
        }
        return a + random.nextFloat() * (b-a);
    }

    /**
     * Returns a random boolean from a Bernoulli distribution with success
     * probability <em>p</em>.
     *
     * @param  p the probability of returning {@code true}
     * @return {@code true} with probability {@code p} and
     *         {@code false} with probability {@code 1 - p}
     * @throws IllegalArgumentException unless {@code 0} &le; {@code p} &le; {@code 1.0}
     */
    public static boolean randomBernoulli(float p) {
        if (!(p >= 0.0 && p <= 1.0))
            throw new MathException("probability p must be between 0.0 and 1.0: " + p);
        return random.nextFloat() < p;
    }

    /**
     * Returns a random real number from a standard Gaussian distribution.
     *
     * @return a random real number from a standard Gaussian distribution
     *         (mean 0 and standard deviation 1).
     */
    public static float randomGaussian() {
        // use the polar form of the Box-Muller transform
        float r, x, y;
        do {
            x = randomUniformFloat(-1.0f, 1.0f);
            y = randomUniformFloat(-1.0f, 1.0f);
            r = x * x + y * y;
        } while (r >= 1 || r == 0);
        return x * (float) Math.sqrt(-2 * Math.log(r) / r);
    }

    /**
     * Returns a random real number from a Gaussian distribution with mean &mu;
     * and standard deviation &sigma;.
     *
     * @param  mu the mean
     * @param  sigma the standard deviation
     * @return a real number distributed according to the Gaussian distribution
     *         with mean {@code mu} and standard deviation {@code sigma}
     */
    public static float randomGaussian(float mu, float sigma) {
        return mu + sigma * randomGaussian();
    }

    /**
     * Returns a random integer from a geometric distribution with success
     * probability <em>p</em>.
     * The integer represents the number of independent trials
     * before the first success.
     *
     * @param  p the parameter of the geometric distribution
     * @return a random integer from a geometric distribution with success
     *         probability {@code p}; or {@code Integer.MAX_VALUE} if
     *         {@code p} is (nearly) equal to {@code 1.0}.
     * @throws MathException unless {@code p >= 0.0} and {@code p <= 1.0}
     */
    public static int randomGeometric(double p) {
        if (!(p >= 0)) throw new MathException("probability p must be greater than 0: " + p);
        if (!(p <= 1.0)) throw new MathException("probability p must not be larger than 1: " + p);

        return (int) Math.ceil(Math.log(random.nextFloat()) / Math.log(1.0 - p));
    }

    /**
     * Returns a random integer from a Poisson distribution with mean &lambda;.
     *
     * @param  lambda the mean of the Poisson distribution
     * @return a random integer from a Poisson distribution with mean {@code lambda}
     * @throws MathException unless {@code lambda > 0.0} and not infinite
     */
    public static int randomPoisson(float lambda) {
        if (!(lambda > 0.0)) throw new MathException("lambda must be positive: " + lambda);
        if (Double.isInfinite(lambda)) throw new MathException("lambda must not be infinite: " + lambda);

        int k = 0;
        double p = 1.0;
        double expLambda = Math.exp(-lambda);
        do {
            k++;
            p *= random.nextFloat();
        } while (p >= expLambda);
        return k-1;
    }

    /**
     * Returns a random real number from a Pareto distribution with
     * shape parameter &alpha;.
     *
     * @param  alpha shape parameter
     * @return a random real number from a Pareto distribution with shape
     *         parameter {@code alpha}
     * @throws MathException unless {@code alpha > 0.0}
     */
    public static float randomPareto(float alpha) {
        if (!(alpha > 0.0)) throw new MathException("alpha must be positive: " + alpha);
        return (float) Math.pow(1 - random.nextFloat(), -1.0 / alpha) - 1.0f;
    }

    /**
     * Returns a random real number from the Cauchy distribution.
     *
     * @return a random real number from the Cauchy distribution.
     */
    public static float randomCauchy() {
        return (float) Math.tan(Math.PI * (random.nextFloat() - 0.5f));
    }

    /**
     * Returns a random integer from the specified discrete distribution.
     *
     * @param  probabilities the probability of occurrence of each integer
     * @return a random integer from a discrete distribution:
     *         {@code i} with probability {@code probabilities[i]}
     * @throws MathException if {@code probabilities} is {@code null}
     * @throws MathException if sum of array entries is not (very nearly) equal to {@code 1.0}
     * @throws MathException unless {@code probabilities[i] >= 0.0} for each index {@code i}
     */
    public static int randomDiscrete(float[] probabilities) {
        if (probabilities == null) throw new MathException("argument array must not be null");
        float sum = 0.0f;
        for (int i = 0; i < probabilities.length; i++) {
            if (!(probabilities[i] >= 0.0)) throw new MathException("array entry " + i + " must be non-negative: " + probabilities[i]);
            sum += probabilities[i];
        }
        if (sum > 1.0 + FLOAT_ROUNDING_ERROR || sum < 1.0 - FLOAT_ROUNDING_ERROR) throw new IllegalArgumentException("sum of array entries does not approximately equal 1.0: " + sum);

        // the for loop may not return a value when both r is (nearly) 1.0 and when the
        // cumulative sum is less than 1.0 (as a result of floating-point roundoff error)
        while (true) {
            double r = random.nextFloat();
            sum = 0.0f;
            for (int i = 0; i < probabilities.length; i++) {
                sum = sum + probabilities[i];
                if (sum > r) return i;
            }
        }
    }

    /**
     * Returns a random integer from the specified discrete distribution.
     *
     * @param  frequencies the frequency of occurrence of each integer
     * @return a random integer from a discrete distribution:
     *         {@code i} with probability proportional to {@code frequencies[i]}
     * @throws MathException if {@code frequencies} is {@code null}
     * @throws MathException if all array entries are {@code 0}
     * @throws MathException if {@code frequencies[i]} is negative for any index {@code i}
     * @throws MathException if sum of frequencies exceeds {@code Integer.MAX_VALUE} (2<sup>31</sup> - 1)
     */
    public static int randomDiscrete(int[] frequencies) {
        if (frequencies == null) throw new MathException("argument array must not be null");
        long sum = 0;
        for (int i = 0; i < frequencies.length; i++) {
            if (frequencies[i] < 0) throw new MathException("array entry " + i + " must be non-negative: " + frequencies[i]);
            sum += frequencies[i];
        }
        if (sum == 0) throw new MathException("at least one array entry must be positive");
        if (sum >= Integer.MAX_VALUE) throw new MathException("sum of frequencies overflows an int");

        // pick index i with probability proportional to frequency
        double r = random.nextInt((int) sum);
        sum = 0;
        for (int i = 0; i < frequencies.length; i++) {
            sum += frequencies[i];
            if (sum > r) return i;
        }

        // can't reach here
        assert false;
        return -1;
    }

    /**
     * Returns a random real number from an exponential distribution
     * with rate &lambda;.
     *
     * @param  lambda the rate of the exponential distribution
     * @return a random real number from an exponential distribution with
     *         rate {@code lambda}
     * @throws MathException unless {@code lambda > 0.0}
     */
    public static double exponential(float lambda) {
        if (!(lambda > 0.0)) throw new MathException("lambda must be positive: " + lambda);
        return -Math.log(1 - random.nextFloat()) / lambda;
    }

    public static int clampInt(int value, int min, int max) {
        if (min < max) {
            if (value < min) return min;
            return Math.min(value, max);
        }
        if (value < max) return max;
        return Math.min(value, min);
    }

    public static long clampLong(long value, long min, long max) {
        if (min < max) {
            if (value < min) return min;
            return Math.min(value, max);
        }
        if (value < max) return max;
        return Math.min(value, min);
    }

    public static float clampFloat(float value, float min, float max) {
        if (min < max) {
            if (value < min) return min;
            return Math.min(value, max);
        }
        if (value < max) return max;
        return Math.min(value, min);
    }

    public static double clampDouble(double value, double min, double max) {
        if (min < max) {
            if (value < min) return min;
            return Math.min(value, max);
        }
        if (value < max) return max;
        return Math.min(value, min);
    }

    public static int nextPowerOf2i(int i) {
        if (i <= 0) return 1;
        if ((i & (i - 1)) == 0) return i;
        i--;
        i |= i >> 1;
        i |= i >> 2;
        i |= i >> 4;
        i |= i >> 8;
        i |= i >> 16;
        i++;
        return i;
    }

    public static int nextPowerOf2f(float f) {
        int power = 1;
        while (power < f) power *= 2;
        return power;
    }

    public static float atanUnchecked(double i) {
        double n = Math.abs(i);
        double c = (n - 1.0) / (n + 1.0);
        double c2 = c * c;
        double c3 = c * c2;
        double c5 = c3 * c2;
        double c7 = c5 * c2;
        double c9 = c7 * c2;
        double c11 = c9 * c2;
        return (float) (Math.signum(i) * ((Math.PI * 0.25) + (0.99997726 * c - 0.33262347 * c3 + 0.19354346 * c5 - 0.11643287 * c7 + 0.05265332 * c9 - 0.0117212 * c11)));
    }

    public static float atan2(final float y, float x) {
        float n = y / x;
        if (n != n)
            n = (y == x ? 1f : -1f); // if both y and x are infinite, n would be NaN
        else if (n - n != n - n) x = 0f; // if n is infinite, y is infinitely larger than x.
        if (x > 0)
            return atanUnchecked(n);
        else if (x < 0) {
            if (y >= 0) return atanUnchecked(n) + PI;
            return atanUnchecked(n) - PI;
        } else if (y > 0)
            return x + PI_HALF;
        else if (y < 0) return x - PI_HALF;
        return x + y; // returns 0 for 0, 0 or NaN if either y or x is NaN
    }

    public static float areaTriangle(float x1, float y1, float x2, float y2, float x3, float y3) { return 0.5f * Math.abs(x1*(y2 - y3) + x2*(y3 - y1) + x3*(y1 - y2)); }

    public static float areaTriangleSigned(float x1, float y1, float x2, float y2, float x3, float y3) { return 0.5f * (x1*(y2 - y3) + x2*(y3 - y1) + x3*(y1 - y2)); }

    public static float areaTriangleSigned(Vector2 p0, Vector2 p1, Vector2 p2) {
        return 0.5f * ((p1.x - p0.x) * (p2.y - p0.y) - (p2.x - p0.x) * (p1.y - p0.y));
    }

    public static float max(float a, float b, float c) {
        return Math.max(a, Math.max(b, c));
    }

    public static float max(float a, float b, float c, float d) {
        return Math.max(a, MathUtils.max(b, c, d));
    }

    public static float min(float a, float b, float c) {
        return Math.min(a, Math.min(b, c));
    }

    public static float min(float a, float b, float c, float d) {
        return Math.min(a, MathUtils.min(b, c, d));
    }

    public static int floor(float x) {
        int xi = (int) x;
        return x < xi ? xi - 1 : xi;
    }

    // [x1, x2] and [x3, x4] are intervals.
    public static float intervalsOverlap(float a_min, float a_max, float b_min, float b_max) {
        if (a_min > a_max) {
            float tmp = a_min;
            a_min = a_max;
            a_max = tmp;
        }
        if (b_min > b_max) {
            float tmp = b_min;
            b_min = b_max;
            b_max = tmp;
        }
        if (a_max <= b_min) return 0;
        if (b_max <= a_min) return 0;

        if (b_min <= a_min) {
            if (b_max <= a_max) return b_max - a_min;
            else return a_max - a_min;
        } else { //  a_min < b_min < a_max
            if (b_max <= a_max) return b_max - b_min;
            else return a_max - b_min;
        }
    }

    public static float sinRad(float radians) {
        return Sin.lookup[(int)(radians * RADIANS_TO_INDEX) & SIN_MASK];
    }

    public static float cosRad(float radians) {
        return Sin.lookup[(int)((radians + PI_HALF) * RADIANS_TO_INDEX) & SIN_MASK];
    }

    public static float sinDeg(float degrees) {
        return Sin.lookup[(int)(degrees * DEGREES_TO_INDEX) & SIN_MASK];
    }

    public static float cosDeg(float degrees) { return Sin.lookup[(int)((degrees + 90) * DEGREES_TO_INDEX) & SIN_MASK]; }

    public static float tanRad(float radians) {
        radians /= PI;
        radians += 0.5f;
        radians -= (float) Math.floor(radians);
        radians -= 0.5f;
        radians *= PI;
        final float x2 = radians * radians, x4 = x2 * x2;
        return radians * ((0.0010582010582010583f) * x4 - (0.1111111111111111f) * x2 + 1f) / ((0.015873015873015872f) * x4 - (0.4444444444444444f) * x2 + 1f);
    }

    public static float tanDeg(float degrees) {
        degrees *= (1f / 180f);
        degrees += 0.5f;
        degrees -= (float) Math.floor(degrees);
        degrees -= 0.5f;
        degrees *= PI;
        final float x2 = degrees * degrees, x4 = x2 * x2;
        return degrees * ((0.0010582010582010583f) * x4 - (0.1111111111111111f) * x2 + 1f)
                / ((0.015873015873015872f) * x4 - (0.4444444444444444f) * x2 + 1f);
    }

    public static float atan(float i) {
        double n = Math.min(Math.abs(i), Double.MAX_VALUE);
        double c = (n - 1.0) / (n + 1.0);
        double c2 = c * c;
        double c3 = c * c2;
        double c5 = c3 * c2;
        double c7 = c5 * c2;
        double c9 = c7 * c2;
        double c11 = c9 * c2;
        return Math.signum(i) * (float)((Math.PI * 0.25) + (0.99997726 * c - 0.33262347 * c3 + 0.19354346 * c5 - 0.11643287 * c7 + 0.05265332 * c9 - 0.0117212 * c11));
    }

    public static float asinDeg(float a) {
        float a2 = a * a; // a squared
        float a3 = a * a2; // a cubed
        if (a >= 0f) return 90f - (float)Math.sqrt(1f - a) * (89.99613099964837f - 12.153259893949748f * a + 4.2548418824210055f * a2 - 1.0731098432343729f * a3);
        return (float) Math.sqrt(1f + a) * (89.99613099964837f + 12.153259893949748f * a + 4.2548418824210055f * a2 + 1.0731098432343729f * a3) - 90f;
    }

    public static float acosDeg(float a) {
        float a2 = a * a; // a squared
        float a3 = a * a2; // a cubed
        if (a >= 0f) return (float) Math.sqrt(1f - a) * (89.99613099964837f - 12.153259533621753f * a + 4.254842010910525f * a2 - 1.0731098035209208f * a3);
        return 180f - (float) Math.sqrt(1f + a) * (89.99613099964837f + 12.153259533621753f * a + 4.254842010910525f * a2 + 1.0731098035209208f * a3);
    }

    public static float atanDeg(float i) {
        double n = Math.min(Math.abs(i), Double.MAX_VALUE);
        double c = (n - 1.0) / (n + 1.0);
        double c2 = c * c;
        double c3 = c * c2;
        double c5 = c3 * c2;
        double c7 = c5 * c2;
        double c9 = c7 * c2;
        double c11 = c9 * c2;
        return (float)(Math.signum(i) * (45.0 + (57.2944766070562 * c - 19.05792099799635 * c3 + 11.089223410359068 * c5 - 6.6711120475953765 * c7 + 3.016813013351768 * c9 - 0.6715752908287405 * c11)));
    }

    public static float normalizeAngleDeg(float degrees) {
        degrees %= 360;
        if (degrees < 0) degrees += 360;
        return degrees;
    }

    public static float normalizeAngleRad(float rad) {
        rad %= PI_TWO;
        if (rad < 0) rad += PI_TWO;
        return rad;
    }

    public static int segmentsIntersection(float a1x, float a1y, float a2x, float a2y, float b1x, float b1y, float b2x, float b2y, @NotNull Vector2 out) {
        Vector2 a1 = vectors2Pool.allocate().set(a1x, a1y);
        Vector2 a2 = vectors2Pool.allocate().set(a2x, a2y);
        Vector2 b1 = vectors2Pool.allocate().set(b1x, b1y);
        Vector2 b2 = vectors2Pool.allocate().set(b2x, b2y);

        int result = segmentsIntersection(a1, a2, b1, b2, out);

        vectors2Pool.free(a1);
        vectors2Pool.free(a2);
        vectors2Pool.free(b1);
        vectors2Pool.free(b2);

        return result;
    }

    /**
     * Finds the intersection of two line segments: S1 & S2
     * where S1 is the line segment between (a1, a2)
     * and   S2 is the line between (b1, b2).
     * Stores the result in out.
     * @return 0 if lines intersect at a unique point
     * 1 if the intersection lies on segment 1 alone
     * 2 if the intersection lies on segment 2 alone
     * 3 if the intersection lies outside both line segments
     * -1 if the lines are parallel (no intersection). {@param out} will store (NAN, NAN).
     * -2 if the lines coincide with infinitely many points of overlap. {@param out} will store the midpoint of the line overlap.
     * @param a1 first point on S1 segment
     * @param a2 second point on S1 segment
     * @param b1 first point on S2 segment
     * @param b2 second point on S2 segment
     * @param out the intersection is stored here.
     */
    public static int segmentsIntersection(Vector2 a1, Vector2 a2, Vector2 b1, Vector2 b2, @NotNull Vector2 out) {
        float Ax = a2.x - a1.x;
        float Ay = a2.y - a1.y;
        float Bx = b2.x - b1.x;
        float By = b2.y - b1.y;
        float Cx = b1.x - a1.x;
        float Cy = b1.y - a1.y;

        float det = Ax * By - Ay * Bx;
        float t = (Cx * By - Cy * Bx) / det;
        float u = (Ay * Cx - Ax * Cy) / det;

        /* handle degenerate cases */
        if (isZero(det)) {
            /* a1 == a2 , b1 == b2 */
            if (a1.equals(a2) && b1.equals(b2)) {
                if (a1.equals(b2)) {
                    out.set(a1);
                    return 0;
                } else {
                    out.set(Float.NaN, Float.NaN);
                    return -1;
                }
            }

            /* a1 == a2 */
            if (a1.equals(a2)) {
                if (pointOnSegment(a1, b1, b2)) {
                    out.set(a1);
                    return 0;
                } else {
                    out.set(Float.NaN, Float.NaN);
                    return -1;
                }
            }

            /* b1 == b2 */
            if (b1.equals(b2)) {
                if (pointOnSegment(b1,a1,a2)) {
                    out.set(b1);
                    return 0;
                } else {
                    out.set(Float.NaN, Float.NaN);
                    return -1;
                }
            }

            /* Segments are parallel or coincide. */

            /* segment S2 "continues" segment S1: a1--------a2b1---------b2 */
            if (a2.equals(b1) && !pointOnSegment(b2, a1, a2) && !pointOnSegment(a1, b1, b2)) {
                out.set(a2);
                return 0;
            }

            /* segment S2 "continues" segment S1: a1--------a2b2---------b1 */
            if (a2.equals(b2) && !pointOnSegment(b1, a1, a2) && !pointOnSegment(a1, b1, b2)) {
                out.set(a2);
                return 0;
            }

            /* segment S2 "continues" segment S1: a2--------a1b1---------b2 */
            if (a1.equals(b1) && !pointOnSegment(b2, a1, a2) && !pointOnSegment(a2, b1, b2)) {
                out.set(a1);
                return 0;
            }

            /* segment S2 "continues" segment S1: a2--------a1b2---------b1 */
            if (a1.equals(b2) && !pointOnSegment(b1, a1, a2) && !pointOnSegment(a2, b1, b2)) {
                out.set(a1);
                return 0;
            }

            /* the lines coincide: either they are separate or they have an overlap. */

            /* coincide */
            if (areCollinear(a1, a2, b1) && areCollinear(a1, a2, b2)) {
                out.set((a1.x + a2.x + b1.x + b2.x) * 0.25f, (a1.y + a2.y + b1.y + b2.y) * 0.25f);
                return -2;
            }

            /* the lines are parallel with no intersection*/
            out.set(Float.NaN, Float.NaN);
            return -1;
        }

        out.x = a1.x + t * (a2.x - a1.x);
        out.y = a1.y + t * (a2.y - a1.y);

        boolean onSegment1 = t >= 0 && t <= 1;
        boolean onSegment2 = u >= 0 && u <= 1;
        if (onSegment1 && onSegment2) return 0;
        if (onSegment1) return 1;
        if (onSegment2) return 2;
        return 3;
    }

    /** Point p is on-line segment S: (a1, a2) if:
     * segments (a1, p) and (p, a2) are co-linear
     * and p is within the bounding box enclosing S.
     * @param p the point to check
     * @param a1 first point on the line segment
     * @param a2 second point on the line segment
    */
    public static boolean pointOnSegment(Vector2 p, Vector2 a1, Vector2 a2) {
        /* check co-linearity */
        float crs = Vector2.crs(p.x - a1.x, p.y - a1.y, a2.x - p.x, a2.y - p.y);
        /* handle degenerate cases */
        if (Float.isNaN(crs)) {
            if (floatsEqual(p.x, a1.x) && floatsEqual(p.y, a1.y)) return true;
            if (floatsEqual(p.x,a2.x) && floatsEqual(p.y, a2.y)) return true;
            if (p.x == a1.x && p.y == a1.y) return true;
            if (p.x == a2.x && p.y == a2.y) return true;
        } else if (!isZero(crs)) return false;

        /* check if point within bounding box */
        if (p.x > Math.max(a1.x, a2.x)) return false;
        if (p.x < Math.min(a1.x, a2.x)) return false;
        if (p.y > Math.max(a1.y, a2.y)) return false;
        if (p.y < Math.min(a1.y, a2.y)) return false;

        return true;
    }

    public static boolean pointOnSegment(float px, float py, float a1x, float a1y, float a2x, float a2y) {
        /* check co-linearity */
        float crs = Vector2.crs(px - a1x, py - a1y, a2x - px, a2y - py);
        /* handle degenerate cases */
        if (Float.isNaN(crs)) {
            if (floatsEqual(px, a1x) && floatsEqual(py, a1y)) return true;
            if (floatsEqual(px,a2x) && floatsEqual(py, a2y)) return true;
        } else if (!isZero(crs)) return false;

        /* check if point within bounding box */
        if (px > Math.max(a1x, a2x)) return false;
        if (px < Math.min(a1x, a2x)) return false;
        if (py > Math.max(a1y, a2y)) return false;
        if (py < Math.min(a1y, a2y)) return false;

        return true;
    }

    public static boolean areCollinear(float p1x, float p1y, float p2x, float p2y, float p3x, float p3y) {
        return floatsEqual (
                (p2x - p1x) * (p3y - p1y)
                ,
                (p3x - p1x) * (p2y - p1y))
                ;
    }

    public static boolean areCollinear(Vector2 p1, Vector2 p2, Vector2 p3) {
        return floatsEqual (
                (p2.x - p1.x) * (p3.y - p1.y)
                    ,
                (p3.x - p1.x) * (p2.y - p1.y))
                ;
    }

    public static int sign(double n) {
        if (n > 0) return 1;
        if (n < 0) return -1;
        return 0;
    }

    public static int sign(float n) {
        if (n > 0) return 1;
        if (n < 0) return -1;
        return 0;
    }

    public static int sign(int n) {
        if (n > 0) return 1;
        if (n < 0) return -1;
        return 0;
    }

    public static boolean isZero(float value) {
        return Math.abs(value) <= FLOAT_ROUNDING_ERROR;
    }

    public static boolean isZero(float value, float tolerance) {
        return Math.abs(value) <= tolerance;
    }

    public static boolean floatsEqual(float a, float b) {
        return Math.abs(a - b) <= FLOAT_ROUNDING_ERROR;
    }

    public static boolean floatsEqual(float a, float b, float tolerance) {
        return Math.abs(a - b) <= tolerance;
    }

    public static boolean isNumeric(float a) {
        if (Float.isNaN(a)) return false;
        if (!Float.isFinite(a)) return false;
        return true;
    }

    /** @return the logarithm of value with base a */
    public static float log(float a, float value) {
        return (float)(Math.log(value) / Math.log(a));
    }

    public static float lerp(float step, float a, float b) {
        return a + step * (b - a);
    }

    public static float smoothstep(float edge0, float edge1, float x) {
        x = clampFloat((x - edge0) / (edge1 - edge0), 0f, 1f);
        return x * x * (3 - 2 * x);
    }

    // Smootherstep: 6x^5 - 15x^4 + 10x^3
    public static float smootherstep(float edge0, float edge1, float x) {
        x = clampFloat((x - edge0) / (edge1 - edge0), 0f, 1f);
        return x * x * x * (x * (x * 6 - 15) + 10);
    }

    private static class Sin {

        private static final float[] lookup = new float[SIN_COUNT];

        static {
            for (int i = 0; i < SIN_COUNT; i++) lookup[i] = (float)Math.sin((i + 0.5f) / SIN_COUNT * RADIANS_FULL);
            lookup[0] = 0f;
            lookup[(int)(90 * DEGREES_TO_INDEX) & SIN_MASK] = 1f;
            lookup[(int)(180 * DEGREES_TO_INDEX) & SIN_MASK] = 0f;
            lookup[(int)(270 * DEGREES_TO_INDEX) & SIN_MASK] = -1f;
        }

    }

    // TODO: test
    public static float getAreaTriangle(float ax, float ay, float bx, float by, float cx, float cy) {
        return 0.5f * Math.abs((ax * (by - cy) + bx * (cy - ay) + cx * (ay - by)));
    }

    // TODO: test
    public static float getAreaTriangle(Vector2 A, Vector2 B, Vector2 C) {
        return 0.5f * Math.abs((A.x * (B.y - C.y) + B.x * (C.y - A.y) + C.x * (A.y - B.y)));
    }

    public static boolean pointInTriangle(Vector2 P, Vector2 A, Vector2 B, Vector2 C) {
        float areaABC = getAreaTriangle(A, B, C);
        float areaPAB = getAreaTriangle(P, A, B);
        float areaPBC = getAreaTriangle(P, B, C);
        float areaPCA = getAreaTriangle(P, C, A);

        // Check if the sum of the areas of PAB, PBC, and PCA is the same as the area of ABC
        return floatsEqual(areaPAB + areaPBC + areaPCA, areaABC);
    }

    public static boolean pointInTriangle(float px, float py, float ax, float ay, float bx, float by, float cx, float cy) {
        float areaABC = getAreaTriangle(ax, ay, bx, by, cx, cy);
        float areaPAB = getAreaTriangle(px, py, ax, ay, bx, by);
        float areaPBC = getAreaTriangle(px, py, bx, by, cx, cy);
        float areaPCA = getAreaTriangle(px, py, cx, cy, ax, ay);

        // Check if the sum of the areas of PAB, PBC, and PCA is the same as the area of ABC
        return floatsEqual(areaPAB + areaPBC + areaPCA, areaABC);
    }

    /**
     * Returns the winding order of a polygon.
     * @return int 1 if clockwise, -1 if counter-clockwise
     */
    public static int polygonWindingOrder(final float[] polygon) {
        float sum = 0;
        for (int i = 0; i < polygon.length - 1; i += 2) {
            float x1 = Collections.getCyclic(polygon, i);
            float y1 = Collections.getCyclic(polygon, i+1);

            float x2 = Collections.getCyclic(polygon, i+2);
            float y2 = Collections.getCyclic(polygon, i+3);

            sum += (x2 - x1) * (y2 + y1);
        }
        return sum > 0.0f ? 1 : -1; // Clockwise: 1, Counter-Clockwise: -1
    }

    /**
     * Returns the winding order of a polygon.
     * @return int 1 if clockwise, -1 if counter-clockwise
     */
    public static int polygonWindingOrder(final ArrayFloat vertices) {
        float sum = 0;
        for (int i = 0; i < vertices.size - 1; i += 2) {
            float x1 = vertices.getCyclic(i);
            float y1 = vertices.getCyclic(i+1);

            float x2 = vertices.getCyclic(i+2);
            float y2 = vertices.getCyclic(i+3);

            sum += (x2 - x1) * (y2 + y1);
        }
        return sum > 0.0f ? 1 : -1; // Clockwise: 1, Counter-Clockwise: -1
    }

    /**
     * Returns the winding order of a polygon.
     * @return int 1 if clockwise, -1 if counter-clockwise
     */
    public static int polygonWindingOrder(Array<Vector2> vertices) {
        float sum = 0.0f;
        for (int i = 0; i < vertices.size; i++) {
            Vector2 v1 = vertices.get(i);
            Vector2 v2 = vertices.get((i + 1) % vertices.size);
            sum += (v2.x - v1.x) * (v2.y + v1.y);
        }
        return sum > 0.0f ? 1 : -1; // Clockwise: 1, Counter-Clockwise: -1
    }

    /**
     * Returns true if the polygon in convex.
     * @param vertices is a flat array of vertex coordinates: [x0,y0, x1,y1, x2,y2, ...].
     * @return boolean
     */
    public static boolean polygonIsConvex(final float[] vertices) {
        if (vertices.length == 6) return true;
        Vector2 tmp1 = vectors2Pool.allocate();
        Vector2 tmp2 = vectors2Pool.allocate();

        tmp1.x = vertices[0] - vertices[vertices.length - 2];
        tmp1.y = vertices[1] - vertices[vertices.length - 1];
        tmp2.x = vertices[2] - vertices[0];
        tmp2.y = vertices[3] - vertices[1];

        float crossSign = Math.signum(tmp1.crs(tmp2));

        for (int i = 2; i < vertices.length; i += 2) {
            tmp1.x = Collections.getCyclic(vertices, i) - Collections.getCyclic(vertices, i - 2);
            tmp1.y = Collections.getCyclic(vertices,i + 1) - Collections.getCyclic(vertices, i - 1);

            tmp2.x = Collections.getCyclic(vertices, i + 2) - Collections.getCyclic(vertices, i);
            tmp2.y = Collections.getCyclic(vertices,i + 3) - Collections.getCyclic(vertices, i + 1);

            float crossSignCurrent = Math.signum(tmp1.crs(tmp2));
            if (crossSignCurrent != crossSign) return false;
        }

        vectors2Pool.free(tmp1);
        vectors2Pool.free(tmp2);

        return true;
    }

    public static void polygonRemoveDegenerateVertices(@NotNull Array<Vector2> polygon, @NotNull Array<Vector2> outPolygon) {
        if (polygon.size < 3) throw new MathException("A polygon requires a minimum of 3 vertices. Got: " + polygon.size);
        if (polygon == outPolygon) throw new IllegalArgumentException("Argument outPolygon cannot be == polygon.");

        /* remove sequential duplicates: [A, B, B, B, C, D, D] -> [A, B, C, D] */
        polygonVertices.clear();
        Vector2 previous = polygon.get(0);
        polygonVertices.add(previous);
        for (int i = 1; i < polygon.size; i++) {
            Vector2 curr = polygon.get(i);
            if (!curr.equals(previous)) {
                polygonVertices.add(curr);
                previous = curr;
            }
        }
        Vector2 first = polygonVertices.first();
        Vector2 last = polygonVertices.last();
        if (last.equals(first) && last != first) {
            polygonVertices.pop();
        }

        /* remove collinear vertices */
        outPolygon.clear();
        for (int i = 0; i < polygonVertices.size; i++) {

            Vector2 prev = polygonVertices.getCyclic(i - 1);
            Vector2 curr = polygonVertices.get(i);
            Vector2 next = polygonVertices.getCyclic(i + 1);

            if (!Vector2.areCollinear(prev, curr, next)) {
                outPolygon.add(curr);
            }
        }
    }

    public static void polygonRemoveDegenerateVertices(float[] polygon, @NotNull ArrayFloat outPolygon) {
        if (polygon.length < 6) throw new MathException("A polygon requires a minimum of 3 vertices. Got: " + polygon.length);
        if (polygon.length % 2 != 0) throw new MathException("Polygon must be represented as a flat array of vertices, each vertex must have x and y coordinates: [x0,y0,  x1,y1, ...]. Therefore, polygon array length must be even. Got: " + polygon.length);
        outPolygon.clear();

        /* remove sequential duplicates: [A, B, B, B, C, D, D] -> [A, B, C, D]. Stores the result in a "compact" polygon (ArrayFloat). */
        ArrayFloat compact = floatArrayPool.allocate();
        float v_x = polygon[0];
        float v_y = polygon[1];
        compact.add(v_x);
        compact.add(v_y);
        for (int i = 2; i < polygon.length; i += 2) {
            float curr_x = polygon[i];
            float curr_y = polygon[i + 1];
            if (!floatsEqual(curr_x, v_x) || !floatsEqual(curr_y, v_y)) {
                compact.add(curr_x);
                compact.add(curr_y);
                v_x = curr_x;
                v_y = curr_y;
            }
        }
        float first_x = compact.get(0);
        float first_y = compact.get(1);
        float last_x  = compact.get(compact.size - 2);
        float last_y  = compact.get(compact.size - 1);
        if (floatsEqual(first_x, last_x) && floatsEqual(first_y, last_y) && compact.size != 2) {
            compact.pop();
            compact.pop();
        }

        /* remove collinear vertices */
        for (int i = 0; i < compact.size - 1; i += 2) {

            float prev_x = compact.getCyclic(i - 2);
            float prev_y = compact.getCyclic(i - 1);

            float curr_x = compact.get(i);
            float curr_y = compact.get(i + 1);

            float next_x = compact.getCyclic(i + 2);
            float next_y = compact.getCyclic(i + 3);

            if (!Vector2.areCollinear(prev_x, prev_y, curr_x, curr_y, next_x, next_y)) {
                outPolygon.add(curr_x);
                outPolygon.add(curr_y);
            }
        }

        floatArrayPool.free(compact);
    }

    public static void polygonRemoveDegenerateVertices(@NotNull Array<Vector2> polygon) {
        if (polygon.size < 3) throw new MathException("A polygon requires a minimum of 3 vertices. Got: " + polygon.size);

        /* remove sequential duplicates: [A, B, B, B, C, D, D] -> [A, B, C, D] */
        ArrayFloat compact = floatArrayPool.allocate();
        Vector2 previous = polygon.get(0);
        compact.add(previous.x);
        compact.add(previous.y);
        for (int i = 1; i < polygon.size; i++) {
            Vector2 curr = polygon.get(i);
            if (!curr.equals(previous)) {
                compact.add(curr.x);
                compact.add(curr.y);
                previous = curr;
            }
        }
        float first_x = compact.get(0);
        float first_y = compact.get(1);
        float last_x  = compact.get(compact.size - 2);
        float last_y  = compact.get(compact.size - 1);
        if (floatsEqual(first_x, last_x) && floatsEqual(first_y, last_y) && compact.size != 1) {
            compact.pop();
            compact.pop();
        }

        /* remove collinear vertices */
        ArrayFloat cleanPolygon = floatArrayPool.allocate();
        for (int i = 0; i < compact.size - 1; i += 2) {

            float prev_x = compact.getCyclic(i - 2);
            float prev_y = compact.getCyclic(i - 1);

            float curr_x = compact.get(i);
            float curr_y = compact.get(i + 1);

            float next_x = compact.getCyclic(i + 2);
            float next_y = compact.getCyclic(i + 3);

            if (!Vector2.areCollinear(prev_x, prev_y, curr_x, curr_y, next_x, next_y)) {
                cleanPolygon.add(curr_x);
                cleanPolygon.add(curr_y);
            }
        }

        /* copy back everything to the original polygon */
        for (int i = 0; i < cleanPolygon.size / 2; i++) {
            Vector2 vertex = polygon.get(i);
            float vx = cleanPolygon.get(2 * i);
            float vy = cleanPolygon.get(2 * i + 1);
            vertex.set(vx, vy);
        }
        polygon.setSize(cleanPolygon.size / 2);

        floatArrayPool.free(compact);
        floatArrayPool.free(cleanPolygon);
    }

    public static void polygonTriangulate(@NotNull Array<Vector2> polygon, @NotNull Array<Vector2> outVertices, @NotNull ArrayInt outIndices) {
        if (polygon.size < 3) throw new MathException("A polygon requires a minimum of 3 vertices, so the polygon array must be of length > 6. Got: " + polygon.size);
        polygonRemoveDegenerateVertices(polygon, outVertices);
        if (outVertices.size < 3) throw new MathException("Polygon contains " + (polygon.size - outVertices.size) + " collinear vertices; When removed, that total vertex count is: " + outVertices.size + "< 3.");

        int windingOrder = MathUtils.polygonWindingOrder(outVertices);
        if (windingOrder > 0) outVertices.reverse();

        indexList.clear();
        for (int i = 0; i < outVertices.size; i++) {
            indexList.add(i);
        }

        int totalTriangleCount = outVertices.size - 2;
        int totalTriangleIndexCount = totalTriangleCount * 3;

        outIndices.clear();
        outIndices.ensureCapacity(totalTriangleIndexCount);

        Vector2 va_to_vb = vectors2Pool.allocate();
        Vector2 va_to_vc = vectors2Pool.allocate();

        while (indexList.size > 3) {
            for (int i = 0; i < indexList.size; i++) {
                int a = indexList.get(i);
                int b = indexList.getCyclic(i - 1);
                int c = indexList.getCyclic(i + 1);

                Vector2 va = outVertices.get(a);
                Vector2 vb = outVertices.get(b);
                Vector2 vc = outVertices.get(c);

                va_to_vb.x = vb.x - va.x;
                va_to_vb.y = vb.y - va.y;

                va_to_vc.x = vc.x - va.x;
                va_to_vc.y = vc.y - va.y;

                // Is ear test vertex convex?
                if (Vector2.crs(va_to_vb, va_to_vc) > 0f) {
                    continue;
                }

                boolean isEar = true;

                // Test: does ear contain any polygon vertices?
                for (int j = 0; j < outVertices.size; j++) {
                    if (j == a || j == b || j == c) continue;
                    Vector2 p = outVertices.get(j);
                    if (pointInTriangle(p.x, p.y, vb.x, vb.y, va.x, va.y, vc.x, vc.y)) {
                        isEar = false;
                        break;
                    }
                }

                if (isEar) {
                    outIndices.add(b);
                    outIndices.add(a);
                    outIndices.add(c);

                    indexList.removeIndex(i);
                    break;
                }
            }
        }

        outIndices.add(indexList.get(0));
        outIndices.add(indexList.get(1));
        outIndices.add(indexList.get(2));

        /* free resources */
        vectors2Pool.free(va_to_vb);
        vectors2Pool.free(va_to_vc);
    }

    public static int[] polygonTriangulate(float[] polygon) {
        if (polygon.length < 6) throw new MathException("A polygon requires a minimum of 3 vertices, so the polygon array must be of length > 6. Got: " + polygon.length);
        if (polygon.length % 2 != 0) throw new MathException("Polygon must be represented as a flat array of vertices, each vertex must have x and y coordinates: [x0,y0,  x1,y1, ...]. Therefore, polygon array length must be even. Got: " + polygon.length);
        ArrayFloat outVertices = new ArrayFloat(true, polygon.length);

        polygonRemoveDegenerateVertices(polygon, outVertices);
        if (outVertices.size < 6) throw new MathException("Polygon contains " + (polygon.length - outVertices.size) / 2 + " collinear vertices; When removed, that total vertex count is: " + outVertices.size / 2 + ". Must have at least 3 non-collinear vertices.");

        int windingOrder = MathUtils.polygonWindingOrder(outVertices);
        if (windingOrder > 0) {
            int n = outVertices.size;
            for (int i = 0; i < n / 2; i += 2) {
                int j = n - i - 2;
                float temp1 = outVertices.get(i);
                float temp2 = outVertices.get(i + 1);
                outVertices.set(i, outVertices.get(j));
                outVertices.set(i + 1, outVertices.get(j + 1));
                outVertices.set(j, temp1);
                outVertices.set(j + 1, temp2);
            }
        }

        indexList.clear();
        for (int i = 0; i < outVertices.size / 2; i++) {
            indexList.add(i);
        }

        int totalTriangleCount = outVertices.size / 2 - 2;
        int totalTriangleIndexCount = totalTriangleCount * 3;

        ArrayInt triangles = new ArrayInt(true, totalTriangleIndexCount);

        Vector2 va_to_vb = vectors2Pool.allocate();
        Vector2 va_to_vc = vectors2Pool.allocate();

        while (indexList.size > 3) {
            for (int i = 0; i < indexList.size; i++) {
                int a = indexList.get(i);
                int b = indexList.getCyclic(i - 1);
                int c = indexList.getCyclic(i + 1);

                float vax = outVertices.get(a*2);
                float vay = outVertices.get(a*2+1);
                float vbx = outVertices.get(b*2);
                float vby = outVertices.get(b*2+1);
                float vcx = outVertices.get(c*2);
                float vcy = outVertices.get(c*2+1);

                va_to_vb.x = vbx - vax;
                va_to_vb.y = vby - vay;
                va_to_vc.x = vcx - vax;
                va_to_vc.y = vcy - vay;

                // Is ear test vertex convex?
                if (Vector2.crs(va_to_vb, va_to_vc) > 0f) continue;

                // Does test ear contain any polygon vertices?
                boolean isEar = true;
                for (int j = 0; j < outVertices.size - 1; j+=2) {
                    int index = j / 2;
                    if (index == a || index == b || index == c) continue;
                    float px = outVertices.get(j);
                    float py = outVertices.get(j+1);
                    if (pointInTriangle(px, py, vbx, vby, vax, vay, vcx, vcy)) {
                        isEar = false;
                        break;
                    }
                }

                if (isEar) {
                    triangles.add(b);
                    triangles.add(a);
                    triangles.add(c);
                    indexList.removeIndex(i);
                    break;
                }
            }
        }

        triangles.add(indexList.get(0));
        triangles.add(indexList.get(1));
        triangles.add(indexList.get(2));

        /* free resources */
        vectors2Pool.free(va_to_vb);
        vectors2Pool.free(va_to_vc);

        return triangles.pack();
    }

    public static void polygonTriangulate(float[] polygon, @NotNull ArrayFloat outVertices, @NotNull ArrayInt outIndices) {
        if (polygon.length < 6) throw new MathException("A polygon requires a minimum of 3 vertices, so the polygon array must be of length > 6. Got: " + polygon.length);
        if (polygon.length % 2 != 0) throw new MathException("Polygon must be represented as a flat array of vertices, each vertex must have x and y coordinates: [x0,y0,  x1,y1, ...]. Therefore, polygon array length must be even. Got: " + polygon.length);

        outVertices.clear();
        outIndices.clear();
        polygonRemoveDegenerateVertices(polygon, outVertices);
        if (outVertices.size < 6) throw new MathException("Polygon contains " + (polygon.length - outVertices.size) / 2 + " collinear vertices; When removed, that total vertex count is: " + outVertices.size / 2 + ". Must have at least 3 non-collinear vertices.");

        int windingOrder = MathUtils.polygonWindingOrder(outVertices);
        if (windingOrder > 0) {
            //outVertices.reverseInPairs();
            // reverse in pairs the [x,y] of the vertices array.
            int n = outVertices.size;
            for (int i = 0; i < n / 2; i += 2) {
                int j = n - i - 2;
                float temp1 = outVertices.get(i);
                float temp2 = outVertices.get(i + 1);
                outVertices.set(i, outVertices.get(j));
                outVertices.set(i + 1, outVertices.get(j + 1));
                outVertices.set(j, temp1);
                outVertices.set(j + 1, temp2);
            }
        }

        indexList.clear();
        for (int i = 0; i < outVertices.size / 2; i++) {
            indexList.add(i);
        }

        int totalTriangleCount = outVertices.size / 2 - 2;
        int totalTriangleIndexCount = totalTriangleCount * 3;

        outIndices.ensureCapacity(totalTriangleIndexCount);

        Vector2 va_to_vb = vectors2Pool.allocate();
        Vector2 va_to_vc = vectors2Pool.allocate();

        while (indexList.size > 3) {
            for (int i = 0; i < indexList.size; i++) {
                int a = indexList.get(i);
                int b = indexList.getCyclic(i - 1);
                int c = indexList.getCyclic(i + 1);

                float vax = outVertices.get(a*2);
                float vay = outVertices.get(a*2+1);
                float vbx = outVertices.get(b*2);
                float vby = outVertices.get(b*2+1);
                float vcx = outVertices.get(c*2);
                float vcy = outVertices.get(c*2+1);

                va_to_vb.x = vbx - vax;
                va_to_vb.y = vby - vay;
                va_to_vc.x = vcx - vax;
                va_to_vc.y = vcy - vay;

                // Is ear test vertex convex?
                if (Vector2.crs(va_to_vb, va_to_vc) > 0f) continue;

                // Does test ear contain any polygon vertices?
                boolean isEar = true;
                for (int j = 0; j < outVertices.size - 1; j+=2) {
                    int index = j / 2;
                    if (index == a || index == b || index == c) continue;
                    float px = outVertices.get(j);
                    float py = outVertices.get(j+1);
                    if (pointInTriangle(px, py, vbx, vby, vax, vay, vcx, vcy)) {
                        isEar = false;
                        break;
                    }
                }

                if (isEar) {
                    outIndices.add(b);
                    outIndices.add(a);
                    outIndices.add(c);
                    indexList.removeIndex(i);
                    break;
                }
            }
        }

        outIndices.add(indexList.get(0));
        outIndices.add(indexList.get(1));
        outIndices.add(indexList.get(2));

        /* free resources */
        vectors2Pool.free(va_to_vb);
        vectors2Pool.free(va_to_vc);
    }

    public static void polygonTriangulate(@NotNull ArrayFloat polygon, @NotNull ArrayFloat outVertices, @NotNull ArrayInt outIndices) {
        polygonTriangulate(polygon.items, outVertices, outIndices);
    }


    // NOTE: the winding order of the polygon does not matter here.
    public static boolean polygonContainsPoint(float[] polygon, float px, float py) {
        if (polygon.length < 6) throw new MathException("A polygon requires a minimum of 3 vertices, so the polygon array must be of length > 6. Got: " + polygon.length);
        if (polygon.length % 2 != 0) throw new MathException("Polygon must be represented as a flat array of vertices, each vertex must have x and y coordinates: [x0,y0,  x1,y1, ...]. Therefore, polygon array length must be even. Got: " + polygon.length);

        int numVertices = polygon.length / 2;
        boolean inside = false;

        for (int i = 0, j = numVertices - 1; i < numVertices; j = i++) {
            float xi = polygon[2 * i], yi = polygon[2 * i + 1];
            float xj = polygon[2 * j], yj = polygon[2 * j + 1];
            boolean intersect = ((yi > py) != (yj > py)) && (px < (xj - xi) * (py - yi) / (yj - yi) + xi);
            if (intersect) inside = !inside;
        }

        return inside;
    }

    // TODO: write tests.
    // NOTE: the winding order of the polygon does not matter here.
    public static boolean polygonContainsPoint(ArrayFloat polygon, float px, float py) {
        if (polygon.size < 6) throw new MathException("A polygon requires a minimum of 3 vertices, so the polygon array must be of length > 6. Got: " + polygon.size);
        if (polygon.size % 2 != 0) throw new MathException("Polygon must be represented as a flat array of vertices, each vertex must have x and y coordinates: [x0,y0,  x1,y1, ...]. Therefore, polygon array length must be even. Got: " + polygon.size);

        int numVertices = polygon.size / 2;
        boolean inside = false;

        for (int i = 0, j = numVertices - 1; i < numVertices; j = i++) {
            float xi = polygon.get(2 * i), yi = polygon.get(2 * i + 1);
            float xj = polygon.get(2 * j), yj = polygon.get(2 * j + 1);
            boolean intersect = ((yi > py) != (yj > py)) && (px < (xj - xi) * (py - yi) / (yj - yi) + xi);
            if (intersect) inside = !inside;
        }

        return inside;
    }

}
