package nz.ac.massey.a3;

/*
    Deal with Ray intersection geometry for various surface types

    Work in local coordinates of the surface
 */

/*
    Use this to record all the necessary information from the ray shooting
    and ray-surface intersection calculations
 */
class HitRecord {
    public Point4 pSurface;
    public Point4 vNormal;
    public double u, v;
    public double tHit;
    public boolean isHit;
    public HitRecord() {
        pSurface = Point4.createPoint(0,0,0);
        vNormal = Point4.createVector(0,0,0);
    }
}

public abstract class SurfaceGeometry {

    protected final static double TINY = 0.01;

    /*
    Implementation details of these abstract methods will depend upon the
    type geometry of the surface
     */

    // Shoot a ray onto the surface
    public abstract boolean shoot(Ray ray, HitRecord hit);

    // Get the smallest box that completely surrounds the surface
    public abstract BoundingBox getBB();
 }

// Shooting to an infinite plane
class Plane extends SurfaceGeometry {

    private final Point4 pOrigin = Point4.createPoint(0,0,0);

    public Plane() {
    }

    @Override
    public boolean shoot(Ray ray, HitRecord hit) {
        // Local coordinates
        Point4 p0 = ray.pOrigin;
        Point4 p1 = ray.pDest;

        Point4 U = p1.minus(p0);
        Point4 V = pOrigin.minus(p0);

        double t = V.z / U.z;

        hit.vNormal = Point4.createVector(0, 0, 1);
        hit.pSurface = ray.calculate(t);
        hit.u = hit.pSurface.x + 0.5;
        hit.v = 0.5 - hit.pSurface.y;
        hit.tHit = t;
        hit.isHit = true;
        return true;
    }

    @Override
    public BoundingBox getBB() {
        return null;
    }
}

// Shooting onto a square - one type of bounded planar region
class Square extends Plane {
    private final static double h = 0.5;
    public boolean shoot(Ray ray, HitRecord hit) {
        super.shoot(ray, hit);
        hit.isHit = hit.tHit > 0 && hit.u >= 0 && hit.u <= 1 && hit.v >= 0 && hit.v <= 1;
        return hit.isHit;
    }
    public BoundingBox getBB() {
        return new BoundingBox(-h, h,-h, h,-TINY,TINY);
    }
}

/*
Provide an implementation for ray shooting onto a Spherical surface
 */

// class Sphere extends SurfaceGeometry  ...

