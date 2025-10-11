package nz.ac.massey.a3;

import java.util.jar.Manifest;

/*
    This class handles the reflective properties of the surface.

    Makes sense to do it this way. How the shading is done, does depend on the material property
 */
public class Material {

    // Parameters of the Phong reflection model as described in the lectures
    //a = ambient, b=diffuse, n=shininess
    private double alpha, beta, nShiny;

    Material(double a, double b, double n) {
        alpha = a; //0 to 1
        beta = b; //0 to 1
        nShiny = n; // >=1
    }

    //clamp
    private static double clamp01(double x){return x < 0? 0:(x> 1 ? 1 : x);}

    // Phong scale factor in [0,1]
    public double calculate(Point4 nW, Point4 lW, Point4 vW, double shadowFF){
        //Normalize copies (treat as vectors)
        Point4 N = Point4.createVector(nW.x,nW.y,nW.z); N.normalize();
        Point4 L = Point4.createVector(lW.x,lW.y,lW.z); L.normalize();
        Point4 V = Point4.createVector(vW.x,vW.y,vW.z); V.normalize();

        double NL = Math.max(0.0,Point4.dot(N,L)); //diffuse

        //reflection R = 2(N*L)N-L
        Point4 R = Point4.createVector(2*NL*N.x - L.x, 2*NL*N.y - L.y, 2*NL*N.z - L.z);
        R.normalize();
        double RV = Math.max(0.0, Point4.dot(R,V)); //specular

        double spec = Math.pow(RV,nShiny);

        // ambient, diffuse, specular, then shadow
        //double f = alpha + beta*NL + (1.0 -alpha -beta)*spec;
        //return clamp01(f *shadowFF);

        // Specular weight = whatever remains after ambient (alpha) and diffuse (beta). Never negative.
        double ks = Math.max(0.0, 1.0 - alpha - beta);

// Ad-hoc brightness gain for the light-dependent terms.
        double intensity = 1.0;

// Phong scale: ambient + (shadowed, brightened) * (diffuse + specular)
// diffuse term = beta * NL              (NL = max(0, N·L))
// specular term = ks * spec             (spec = (R·V)^nShiny, computed earlier)
        double f = alpha + shadowFF * intensity * (beta * NL + ks * spec);

// Limit to [0,1] before applying to the base color.
        return clamp01(f);

    }

    /*
    Put in your implementation of the Phong model - the result should be some scale factor
    0<=f<=1 which will later be applied to the surface colour.

    Define methods and parameters as you see fit.
     */
}
