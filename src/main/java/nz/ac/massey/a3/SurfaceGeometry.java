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
        pSurface = Point4.createPoint(0, 0, 0);
        vNormal = Point4.createVector(0, 0, 0);
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

class Sphere extends SurfaceGeometry {
    public Double radius;
    public Double radius2;

    public Sphere(double radius) {
        this.radius = radius;
        radius2 = radius * radius;
    }

    @Override
    public boolean shoot(Ray ray, HitRecord hit) {
        //equation of a ray is p(t)=Po+td, d = (Dest - orig(Po)) ray direction
        Point4 Po = ray.pOrigin;
        Point4 d = ray.pDest.minus(Po);


        //a,b,c from quadratic
        double a = (d.x * d.x + d.y * d.y + d.z * d.z);
        if (Math.abs(a) == 0.0) { hit.isHit = false; return false; }//prevent divide by zero

        double b = 2 * (Po.x * d.x + Po.y * d.y + Po.z * d.z);
        double c = (Po.x * Po.x + Po.y * Po.y + Po.z * Po.z) - radius2;

        double discr = (b * b) - 4 * (a * c);

        if (discr < 0.0) {
            hit.isHit = false;
            return false;
        }

        //find the solutions for t, the intersects
        double sq = Math.sqrt(discr);
        double t1 = (-b + sq) / (2 * a);
        double t2 = (-b - sq) / (2 * a);

        //find nearest intersection
        double tNear = Double.POSITIVE_INFINITY;//set tnear to +inf
        if (t1 > 0.0) tNear = Math.min(tNear, t1);//choose smaller of the two
        if (t2 > 0.0) tNear = Math.min(tNear, t2);
        if (!Double.isFinite(tNear)) { hit.isHit = false; return false; }//check if a solution was found, is tnear real


        Point4 p = ray.calculate(tNear);//hit point
        Point4 norm = Point4.createVector(p.x / radius, p.y / radius, p.z / radius);//surf normal
        norm.normalize();

        //get uv for texture mapping
        double thet = Math.atan2(p.z, p.x);

        // safe acos to avoid NaN if rounding pushes the value slightly out of [-1,1]
        double cosPhi = p.y / radius;
        cosPhi = Math.max(-1.0, Math.min(1.0, cosPhi));
        double phi = Math.acos(cosPhi);
        hit.u = (thet / (2 * Math.PI)) + 0.5;//recenter u
        hit.v = 1.0 - (phi / Math.PI);//invert v to match image texture

        //store hit data
        hit.pSurface = p;
        hit.vNormal = norm;
        hit.tHit = tNear;
        hit.isHit = true;

        return true;
    }

    @Override
    public BoundingBox getBB() {
        return new BoundingBox(-radius, radius, -radius, radius, -radius, radius);
    }
}

// Shooting to an infinite plane
class Plane extends SurfaceGeometry {

    private final Point4 pOrigin = Point4.createPoint(0, 0, 0);

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
        return new BoundingBox(-h, h, -h, h, -TINY, TINY);
    }
}

/*
Provide an implementation for ray shooting onto a Spherical surface
 */



