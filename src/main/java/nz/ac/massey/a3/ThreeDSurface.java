package nz.ac.massey.a3;

import java.awt.*;
import java.util.ArrayList;

/*
    Encapsulate information that is not when shading a point on the surface
 */
class ShadeRecord {
    Point4 pSurface;  // Surface position - need this for z-buffering
    Color colour;     // Final colour at that surface point  after applying shading
    boolean isShaded;
}

/*
    Class to encapsulate the properties of a surface that is placed in a 3D scene
 */
public class ThreeDSurface {

    // The type of geometry
    SurfaceGeometry surfaceGeometry;

    // The material reflective properties of the surface
    Material material;

    // The surface colour model: either uniform or from a texture map
    SurfaceColour surfaceColour;

    // The placement of the surface within the scene
    Placement placement;

    public ThreeDSurface() {}

    public double uvScaleU = 1.0;
    public double uvScaleV = 1.0;
    public double uvRotate = 0.0; // in radians


    public ThreeDSurface(SurfaceGeometry sg, Material m, SurfaceColour sc, Placement p) {
        this.surfaceGeometry = sg;
        this.surfaceColour = sc;
        this.material = m;
        this.placement = p;
    }

    /*
    Given a Ray in world coordinates, calculate final shaded colour
     */
    public static int clip01(int c, double f) {
        return (int) Math.round(f * c);
    }

    public static Color rescaleColour(Color c, double f) {
        int r = clip01(c.getRed(), f);
        int g = clip01(c.getGreen(), f);
        int b = clip01(c.getBlue(), f);
        return new Color(r, g, b);
    }

    public RasterMap getRasterMap(Matrix4 pmx, int npixx, int npixy) {
        BoundingBox bb = surfaceGeometry.getBB().transform(pmx);
        RasterMap rm = RasterMap.fromBB(bb, npixx, npixy);
        if (bb.anyNegW) {
            rm.x1 = 0;
            rm.x2 = npixx;
            rm.y1 = 0;
            rm.y2 = npixy;
        }
        return rm;
    }

    public ShadeRecord shadeIt(Ray rayW, Point4 pLightW, ArrayList<ThreeDSurface> surfaces) {

        ShadeRecord sr = new ShadeRecord();

        // Ray in local coordinates of the surface
        Ray ray = Ray.transform(rayW, placement.tWL);

        HitRecord hit = new HitRecord();

        surfaceGeometry.shoot(ray, hit);
        sr.isShaded = hit.isHit;

        if (hit.isHit) {
            Point4 pSurfaceW = placement.toWorld(hit.pSurface);
            Point4 vNormalW  = placement.toWorld(hit.vNormal);     // w=0 so no translation
            Point4 vLightW   = pLightW.minus(pSurfaceW);           // L = light - point
            Point4 vViewW    = rayW.pOrigin.minus(pSurfaceW);      // V = eye - point

            // --- inline hard shadow (no helper class) ---
            final double EPS = 1e-3;

            // vector from hit point to light (world)
            Point4 L = pLightW.minus(pSurfaceW);
            double dist = Math.sqrt(L.x*L.x + L.y*L.y + L.z*L.z);
            Point4 dir = Point4.createVector(L.x/dist, L.y/dist, L.z/dist);

            // finite ray segment from just above the surface towards the light
            Point4 p0 = pSurfaceW.addVector(dir, EPS);                 // avoid self-hit
            Point4 p1 = p0.addVector(dir, dist - 2*EPS);               // stop before light
            Ray shadowRayW = new Ray(p0, p1);

            // test against all other objects
            double ff = 1.0;                                           // 1 = lit
            for (ThreeDSurface s : surfaces) {
                if (s == this) continue;                               // skip self
                Ray srLocal = Ray.transform(shadowRayW, s.placement.tWL);
                HitRecord h = new HitRecord();
                if (s.surfaceGeometry.shoot(srLocal, h) && h.isHit && h.tHit > 0.0 && h.tHit < 1.0) {
                    ff = 0.0;                                          // blocked → shadow
                    break;
                }
            }

            /*
            // shade with shadow factor
            double fShade = material.calculate(vNormalW, vLightW, vViewW, ff);
            Color c1 = surfaceColour.pickColour(hit.u, hit.v);
            sr.colour = rescaleColour(c1, fShade);
            */

            // Shade with shadow factor
            double fShade = material.calculate(vNormalW, vLightW, vViewW, ff);

// --- UV transform: apply rotation and scaling ---
            double cosA = Math.cos(uvRotate);
            double sinA = Math.sin(uvRotate);

// center UVs around 0.5, rotate, and uncenter
            double u = hit.u - 0.5;
            double v = hit.v - 0.5;
            double uRot = u * cosA - v * sinA;
            double vRot = u * sinA + v * cosA;
            uRot += 0.5;
            vRot += 0.5;

// sample the texture with rotation + scaling
            Color c1 = surfaceColour.pickColour(uRot / uvScaleU, vRot / uvScaleV);

// apply material shading
            sr.colour = rescaleColour(c1, fShade);


            sr.pSurface = placement.toWorld(hit.pSurface);
        }

        /*
        if (hit.isHit) {
            Point4 pSurfaceW = placement.toWorld(hit.pSurface);
            Point4 vNormalW  = placement.toWorld(hit.vNormal); //w=0 so translation ignored
            Point4 vLightW   = pLightW.minus(pSurfaceW); //L = light - point
            Point4 vViewW    = rayW.pOrigin.minus(pSurfaceW); //V = eye - point

            // Shadow form factor
            //double ff = FEEL_SHADOWS ? Shadows.feel(surfaces, pSurfaceW, pLightW) : 1.0;

            // Shading factor
            //double fShade = SHADING_ON ? material.calculate(vNormalW, vLightW, vViewW, ff) : 1.0;

            //double fShade = 1.0;

            double ff = 1.0; // or your Shadows.feel(...) later
            double fShade = material.calculate(vNormalW, vLightW, vViewW, ff);
            Color c1 = surfaceColour.pickColour(hit.u, hit.v);
            sr.colour = rescaleColour(c1, fShade);

            sr.pSurface = placement.toWorld(hit.pSurface);
            //System.out.println(fShade + " " + sr.isShaded + " " + sr.pSurface);
        }*/
        return sr;
    }

}
