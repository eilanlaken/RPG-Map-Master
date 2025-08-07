package com.heavybox.jtix.physics2d;

import com.heavybox.jtix.collections.Array;
import com.heavybox.jtix.graphics.Renderer2D;
import com.heavybox.jtix.math.MathUtils;
import com.heavybox.jtix.math.Vector2;
import com.heavybox.jtix.memory.MemoryPool;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;

public class World2D {

    public static final int DEFAULT_VELOCITY_CONSTRAINT_SOLVER_ITERATIONS = 6;
    public static final int DEFAULT_POSITION_CONSTRAINT_SOLVER_ITERATIONS = 2;

    // memory pools
    final MemoryPool<Body2D>                   bodiesPool        = new MemoryPool<>(Body2D.class,10);
    final MemoryPool<Collision2DManifold>      manifoldsPool     = new MemoryPool<>(Collision2DManifold.class,10);
    final MemoryPool<Collision2D.Pair>         pairsPool         = new MemoryPool<>(Collision2D.Pair.class,5);
    final MemoryPool<Collision2D.GridCell>     cellsPool         = new MemoryPool<>(Collision2D.GridCell.class,1024);
    final MemoryPool<RayCasting2DRay>          raysPool          = new MemoryPool<>(RayCasting2DRay.class,4);
    final MemoryPool<RayCasting2DIntersection> intersectionsPool = new MemoryPool<>(RayCasting2DIntersection.class,4);

    // bodies
    private       int           bodiesCreated  = 0;
    public  final Array<Body2D> allBodies      = new Array<>(false, 500);
    private final Array<Body2D> bodiesToAdd    = new Array<>(false, 100);
    private final Array<Body2D> bodiesToRemove = new Array<>(false, 500);

    // collision detection - broad phase
    private final Array<Collision2D.GridCell> spacePartition      = new Array<>(false, 1024);
    private final Array<Collision2D.GridCell> activeCells         = new Array<>();
    private final Set<Collision2D.Pair>       collisionCandidates = new HashSet<>();
    private final Collision2DSolver           collisionSolver     = new Collision2DSolver();

    // collision detection - narrow phase
    private final Collision2D                collision = new Collision2D(this);
    private final Array<Collision2DManifold> manifolds = new Array<>(false, 20);

    // forces
    public Vector2             gravity             = new Vector2();
    public Array<ForceField2D> allForceFields      = new Array<>(false, 4);
    public Array<ForceField2D> forceFieldsToAdd    = new Array<>(false, 2);
    public Array<ForceField2D> forceFieldsToRemove = new Array<>(false, 2);

    // constraints
    public int                 velocityIterations  = DEFAULT_VELOCITY_CONSTRAINT_SOLVER_ITERATIONS;
    public int                 positionIterations  = DEFAULT_POSITION_CONSTRAINT_SOLVER_ITERATIONS;
    public Array<Constraint2D> allConstraints      = new Array<>(false, 10);
    public Array<Constraint2D> constraintsToAdd    = new Array<>(false, 5);
    public Array<Constraint2D> constraintsToRemove = new Array<>(false, 5);

    // ray casting
    final RayCasting2D                                   rayCasting    = new RayCasting2D(this);
    final HashMap<RayCasting2DRay, RayCasting2DCallback> allRays       = new HashMap<>(4);
    final HashMap<RayCasting2DRay, RayCasting2DCallback> raysToAdd     = new HashMap<>(4);
    final HashMap<RayCasting2DRay, RayCasting2DCallback> raysToRemove  = new HashMap<>(4);
    final Array<RayCasting2DIntersection>                intersections = new Array<>(false, 10);

    // debugger options
    private final World2DRenderer debugRenderer     = new World2DRenderer(this);
    public        boolean         renderBodies      = true;
    public        boolean         renderVelocities  = true;
    public        boolean         renderConstraints = true;
    public        boolean         renderRays        = true;
    public        boolean         renderContacts    = true;

    public void update(float delta) {
        /* add and remove bodies */
        {
            for (Body2D body : bodiesToRemove) {
                for (Constraint2D constraint : body.constraints) {
                    destroyConstraint(constraint);
                }
                allBodies.removeValue(body, true);
                bodiesPool.free(body);
            }
            for (Body2D body : bodiesToAdd) {
                allBodies.add(body);
                body.index = bodiesCreated;
                body.init();
                bodiesCreated++;
            }
            bodiesToRemove.clear();
            bodiesToAdd.clear();
        }

        /* add and remove constraints */
        {
            for (Constraint2D constraint : constraintsToRemove) {
                Body2D body1 = constraint.body1;
                Body2D body2 = constraint.body2;
                body1.constraints.removeValue(constraint, true);
                if (body2 != null) body2.constraints.removeValue(constraint, true);
                allConstraints.removeValue(constraint, true);
            }
            Array<Constraint2D> tmp = new Array<>(false, 5);
            for (Constraint2D constraint : constraintsToAdd) {
                Body2D body1 = constraint.body1;
                Body2D body2 = constraint.body2;
                final boolean notReady = !body1.initialized || (body2 != null && !body2.initialized);
                if (notReady) { // will be added later
                    tmp.add(constraint);
                } else {
                    body1.constraints.add(constraint);
                    if (body2 != null) body2.constraints.add(constraint);
                    allConstraints.add(constraint);
                }
            }
            constraintsToRemove.clear();
            constraintsToAdd.clear();
            constraintsToAdd.addAll(tmp);
        }

        /* preparation: add and remove force fields */
        {
            allForceFields.removeAll(forceFieldsToRemove, true);
            allForceFields.addAll(forceFieldsToAdd);
            forceFieldsToRemove.clear();
            forceFieldsToAdd.clear();
        }

        /* Euler integration: integrate velocities */
        {
            for (Body2D body : allBodies) {
                if (body.off) continue;

                if (body.motionType == Body2D.MotionType.NEWTONIAN) {
                    for (ForceField2D field : allForceFields) {
                        Vector2 force = new Vector2();
                        field.calculateForce(body, force);
                        body.netForceX += force.x;
                        body.netForceY += force.y;
                    }
                    body.netForceX += gravity.x;
                    body.netForceY += gravity.y;
                    body.vx += body.invM * delta * body.netForceX;
                    body.vy += body.invM * delta * body.netForceY;
                    body.wRad += body.netTorque * body.invI * delta;
                }
                body.netForceX = 0;
                body.netForceY = 0;
                body.netTorque = 0;
                body.touching.clear();
            }
        }

        /* collision detection: broad phase */
        {
            // to save additional iteration over the world bodies, we update the world's extent as well in this pass.
            float worldMinX = Float.POSITIVE_INFINITY;
            float worldMaxX = Float.NEGATIVE_INFINITY;
            float worldMinY = Float.POSITIVE_INFINITY;
            float worldMaxY = Float.NEGATIVE_INFINITY;
            float worldMaxR = Float.NEGATIVE_INFINITY;
            for (Body2D body : allBodies) {
                if (body.off) continue;
                for (Body2DCollider collider : body.colliders) {
                    worldMinX = Math.min(worldMinX, collider.getMinExtentX());
                    worldMaxX = Math.max(worldMaxX, collider.getMaxExtentX());
                    worldMinY = Math.min(worldMinY, collider.getMinExtentY());
                    worldMaxY = Math.max(worldMaxY, collider.getMaxExtentY());
                    worldMaxR = Math.max(worldMaxR, collider.boundingRadius());
                }
            }

            float maxDiameter = 2 * worldMaxR;
            float worldWidth = Math.abs(worldMaxX - worldMinX);
            float worldHeight = Math.abs(worldMaxY - worldMinY);
            int rows = Math.min((int) Math.ceil(worldHeight / maxDiameter), 32);
            int cols = Math.min((int) Math.ceil(worldWidth / maxDiameter), 32);
            float cellWidth = worldWidth / cols;
            float cellHeight = worldHeight / rows;

            /* collision detection - broad phase */
            cellsPool.freeAll(spacePartition);
            spacePartition.clear();
            activeCells.clear();
            for (int i = 0; i < rows * cols; i++) {
                spacePartition.add(cellsPool.allocate());
            }

            for (Body2D body : allBodies) {
                for (Body2DCollider collider : body.colliders) {
                    int startCol = Math.max(0, (int) ((collider.getMinExtentX() - worldMinX) / cellWidth));
                    int endCol = Math.min(cols - 1, (int) ((collider.getMaxExtentX() - worldMinX) / cellWidth));
                    int startRow = Math.max(0, (int) ((collider.getMinExtentY() - worldMinY) / cellHeight));
                    int endRow = Math.min(rows - 1, (int) ((collider.getMaxExtentY() - worldMinY) / cellHeight));

                    for (int row = startRow; row <= endRow; row++) {
                        for (int col = startCol; col <= endCol; col++) {
                            Collision2D.GridCell cell = spacePartition.get(row * cols + col);
                            cell.colliders.add(collider);
                            if (!cell.active) {
                                cell.active = true;
                                activeCells.add(cell);
                            }
                        }
                    }
                }
            }

            pairsPool.freeAll(collisionCandidates);
            collisionCandidates.clear();
            for (Collision2D.GridCell cell : activeCells) {
                for (int i = 0; i < cell.colliders.size - 1; i++) {
                    for (int j = i + 1; j < cell.colliders.size; j++) {
                        Body2DCollider collider_a = cell.colliders.get(i);
                        Body2DCollider collider_b = cell.colliders.get(j);
                        Body2D body_a = collider_a.body;
                        Body2D body_b = collider_b.body;
                        if (body_a.off) continue;
                        if (body_b.off) continue;
                        if (body_a.motionType == Body2D.MotionType.STATIC && body_b.motionType == Body2D.MotionType.STATIC) continue;
                        final Vector2 worldCenter_a = collider_a.worldCenter();
                        final Vector2 worldCenter_b = collider_a.worldCenter();
                        final float dx = worldCenter_b.x - worldCenter_a.x;
                        final float dy = worldCenter_b.y - worldCenter_a.y;
                        final float sum = collider_a.boundingRadius() + collider_b.boundingRadius();
                        boolean boundingCirclesCollide = dx * dx + dy * dy < sum * sum;
                        if (!boundingCirclesCollide) continue;

                        Collision2D.Pair pair = pairsPool.allocate();
                        pair.set(collider_a, collider_b);
                        collisionCandidates.add(pair);
                    }
                }
            }
        }

        /* collision detection - narrow phase */
        {
            manifoldsPool.freeAll(manifolds);
            manifolds.clear();
            for (Collision2D.Pair pair : collisionCandidates) {
                Body2DCollider collider_a = pair.getA();
                Body2DCollider collider_b = pair.getB();
                Collision2DManifold manifold = collision.detectCollision(collider_a, collider_b);
                if (manifold != null) manifolds.add(manifold);
            }
        }

        /* collision resolution */
        {
            // TODO: need to figure out how to properly set the Body's: justCollided, touching, justSeparated.
            for (Collision2DManifold manifold : manifolds) {
                Body2D body_a = manifold.collider_a.body;
                Body2D body_b = manifold.collider_b.body;

                body_a.touching.add(body_b);
                body_b.touching.add(body_a);
                collisionSolver.beginContact(manifold);
                collisionSolver.resolve(manifold);
                collisionSolver.endContact(manifold);
            }
        }

        /* solve velocity constraints */
        for (Constraint2D constraint : allConstraints) {
            constraint.prepare(delta);
        }
        for (int i = 0; i < velocityIterations; i++) {
            for (Constraint2D constraint : allConstraints) {
                constraint.solveVelocity(delta);
            }
        }

        /* integrate positions */
        for (Body2D body : allBodies) {
            if (body.off) continue;
            if (body.motionType == Body2D.MotionType.STATIC) continue;
            body.x += delta * body.vx;
            body.y += delta * body.vy;
            body.radians += delta * body.wRad;
            body.syncTransform();
        }

        /* solve position constraints */
        boolean positionSolved = false;
        for (int i = 0; i < positionIterations; ++i) {
            for (Constraint2D constraint : allConstraints) {
                constraint.solvePosition(delta);
            }
        }

        /* ray casting */
        {
            for (Map.Entry<RayCasting2DRay, RayCasting2DCallback> rayCallback : raysToRemove.entrySet()) {
                RayCasting2DRay ray = rayCallback.getKey();
                allRays.remove(ray);
                raysPool.free(ray);
            }
            allRays.putAll(raysToAdd);
            raysToRemove.clear();
            raysToAdd.clear();
            intersectionsPool.freeAll(intersections);
            intersections.clear();

            for (Map.Entry<RayCasting2DRay, RayCasting2DCallback> rayCallback : allRays.entrySet()) {
                RayCasting2DRay ray = rayCallback.getKey();
                RayCasting2DCallback callback = rayCallback.getValue();
                // set the distance for the ray based on world's extent
                if (ray.dst == Float.POSITIVE_INFINITY || Float.isNaN(ray.dst)) {

                }
                Array<RayCasting2DIntersection> results = new Array<>();
                // TODO: optimize this using the cell grid.
                rayCasting.calculateIntersections(ray, allBodies, results);
                intersections.addAll(results);
                if (callback != null) callback.intersected(results);
                results.clear();
                raysToRemove.put(ray, callback);
            }
        }

    }

    public void render(Renderer2D renderer) {
        debugRenderer.render(renderer);
    }

    /* World state setters */
    public void setGravity(float gx, float gy) {
        this.gravity.set(gx, gy);
    }

    /* Bodies and Colliders API */

    @NotNull
    @Contract(pure = true)
    public Body2D createBody(Object owner,
                             Body2D.MotionType motionType,
                             Body2DCollider...colliders) {
        Body2D body = bodiesPool.allocate();
        body.owner = owner;
        body.off = false;
        body.motionType = motionType;
        for (Body2DCollider collider : colliders) {
            collider.body = body;
            body.colliders.add(collider);
        }
        bodiesToAdd.add(body);
        return body;
    }

    @NotNull
    @Contract(pure = true)
    public Body2D createBodyCircle(Object owner,
                                   Body2D.MotionType motionType,
                                   float x, float y, float angleDeg,
                                   float vx, float vy, float velAngleDeg,
                                   float density, float staticFriction, float dynamicFriction, float restitution,
                                   boolean ghost, int bitmask,
                                   float radius) {
        Body2D body = bodiesPool.allocate();
        body.owner = owner;
        body.off = false;
        body.motionType = motionType;

        Body2DColliderCircle circleCollider = new Body2DColliderCircle(density, staticFriction,
                dynamicFriction, restitution, ghost, bitmask, radius, 0, 0, 0);
        circleCollider.body = body;
        body.colliders.add(circleCollider);

        body.x = x;
        body.y = y;
        body.radians = angleDeg * MathUtils.degreesToRadians;

        body.vx = vx;
        body.vy = vy;
        body.wRad = velAngleDeg * MathUtils.degreesToRadians;

        bodiesToAdd.add(body);
        return body;
    }

    @NotNull
    @Contract(pure = true)
    public Body2D createBodyCircle(Object owner,
                                   Body2D.MotionType motionType,
                                   float x, float y, float angleDeg,
                                   float vx, float vy, float velAngleDeg,
                                   float density, float staticFriction, float dynamicFriction, float restitution,
                                   boolean ghost, int bitmask,
                                   float radius, float offsetX, float offsetY) {
        Body2D body = bodiesPool.allocate();
        body.owner = owner;
        body.off = false;
        body.motionType = motionType;

        Body2DColliderCircle circleCollider = new Body2DColliderCircle(density, staticFriction,
                dynamicFriction, restitution, ghost, bitmask, radius, offsetX, offsetY, 0);
        circleCollider.body = body;
        body.colliders.add(circleCollider);

        body.x = x;
        body.y = y;
        body.radians = angleDeg * MathUtils.degreesToRadians;

        body.vx = vx;
        body.vy = vy;
        body.wRad = velAngleDeg * MathUtils.degreesToRadians;

        bodiesToAdd.add(body);
        return body;
    }

    // TODO
    @NotNull
    @Contract(pure = true)
    public Body2D createBodyRectangle(Object owner,
                                      Body2D.MotionType motionType,
                                      float x, float y, float angleDeg,
                                      float vx, float vy, float velAngleDeg,
                                      float density, float staticFriction, float dynamicFriction, float restitution,
                                      boolean ghost, int bitmask,
                                      float width, float height) {
        Body2D body = bodiesPool.allocate();
        body.owner = owner;
        body.off = false;
        body.motionType = motionType;

        Body2DColliderRectangle rectangleCollider = new Body2DColliderRectangle(density, staticFriction,
                dynamicFriction, restitution, ghost, bitmask, width, height, 0, 0, 0);
        rectangleCollider.body = body;
        body.colliders.add(rectangleCollider);

        body.x = x;
        body.y = y;
        body.radians = angleDeg * MathUtils.degreesToRadians;

        body.vx = vx;
        body.vy = vy;
        body.wRad = velAngleDeg * MathUtils.degreesToRadians;

        bodiesToAdd.add(body);
        return body;
    }

    // TODO
    @NotNull
    @Contract(pure = true)
    public Body2D createBodyRectangle(Object owner,
                                      Body2D.MotionType motionType,
                                      float x, float y, float angleDeg,
                                      float vx, float vy, float velAngleDeg,
                                      float density, float staticFriction, float dynamicFriction, float restitution,
                                      boolean ghost, int bitmask,
                                      float width, float height, float offsetX, float offsetY, float offsetAngleDeg) {
        Body2D body = bodiesPool.allocate();
        body.owner = owner;
        body.off = false;
        body.motionType = motionType;

        Body2DColliderRectangle rectangleCollider = new Body2DColliderRectangle(density, staticFriction,
                dynamicFriction, restitution, ghost, bitmask, width, height, offsetX, offsetY, offsetAngleDeg * MathUtils.degreesToRadians);
        rectangleCollider.body = body;
        body.colliders.add(rectangleCollider);

        body.x = x;
        body.y = y;
        body.radians = angleDeg * MathUtils.degreesToRadians;

        body.vx = vx;
        body.vy = vy;
        body.wRad = velAngleDeg * MathUtils.degreesToRadians;

        bodiesToAdd.add(body);
        return body;
    }

    // TODO
    @NotNull
    @Contract(pure = true)
    public Body2D createBodyPolygon(Object owner,
                                    Body2D.MotionType motionType,
                                    float x, float y, float angleDeg,
                                    float vx, float vy, float velAngleDeg,
                                    float density, float staticFriction, float dynamicFriction, float restitution,
                                    boolean ghost, int bitmask,
                                    float[] vertices) {
        Body2D body = bodiesPool.allocate();
        body.owner = owner;
        body.off = false;
        body.motionType = motionType;

        boolean convex = MathUtils.polygonIsConvex(vertices);

        if (!convex) throw new IllegalArgumentException("concave polygons not supported yet.");

        // TODO: see if polygon is concave or with holes. If it is, handle case properly.
        Body2DColliderPolygon polygonCollider = new Body2DColliderPolygon(density, staticFriction,
                dynamicFriction, restitution, ghost, bitmask, vertices);
        polygonCollider.body = body;
        body.colliders.add(polygonCollider);

        body.x = x;
        body.y = y;
        body.radians = angleDeg * MathUtils.degreesToRadians;

        body.vx = vx;
        body.vy = vy;
        body.wRad = velAngleDeg * MathUtils.degreesToRadians;

        bodiesToAdd.add(body);
        return body;
    }

    /* Force fields API */

    @NotNull
    @Contract(pure = true)
    public ForceField2D createForceField(BiConsumer<Body2D, Vector2> forceFunction) {
        ForceField2D forceField = new ForceField2D(this) {
            @Override
            public void calculateForce(Body2D body, Vector2 out) {
                forceFunction.accept(body, out);
            }
        };
        forceFieldsToAdd.add(forceField);
        return forceField;
    }

    /* TODO: Constraints API */

    public Constraint2DDistance createConstraintDistance(Body2D body_a, Body2D body_b, float distance) {
        Constraint2DDistance constraintDistance = new Constraint2DDistance(body_a, body_b, new Vector2(), new Vector2(), distance, 0, 0);
        constraintsToAdd.add(constraintDistance);
        return constraintDistance;
    }

    public void destroyConstraint(Constraint2D constraint) {
        constraintsToRemove.add(constraint);
    }

    /* TODO: Ray casting API */

    public void castRay(final RayCasting2DCallback callback, float originX, float originY, float dirX, float dirY) {
        RayCasting2DRay ray = raysPool.allocate();
        ray.originX = originX;
        ray.originY = originY;
        float len = Vector2.len(dirX, dirY);
        boolean zero = MathUtils.isZero(dirX) && MathUtils.isZero(dirY);
        ray.dirX = zero ? 1 : dirX / len;
        ray.dirY = zero ? 0 : dirY / len;
        ray.dst = Float.POSITIVE_INFINITY;
        raysToAdd.put(ray, callback);
    }

    public void castRay(final RayCasting2DCallback callback, float originX, float originY, float dirX, float dirY, float maxDst) {
        RayCasting2DRay ray = raysPool.allocate();
        ray.originX = originX;
        ray.originY = originY;
        float len = Vector2.len(dirX, dirY);
        boolean zero = MathUtils.isZero(dirX) && MathUtils.isZero(dirY);
        ray.dirX = zero ? 1 : dirX / len;
        ray.dirY = zero ? 0 : dirY / len;
        ray.dst = Math.abs(maxDst);
        raysToAdd.put(ray, callback);
    }


}
