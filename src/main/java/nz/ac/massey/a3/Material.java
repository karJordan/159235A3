package nz.ac.massey.a3;

/*
    This class handles the reflective properties of the surface.

    Makes sense to do it this way. How the shading is done, does depend on the material property
 */
public class Material {

    // Parameters of the Phong reflection model as described in the lectures
    //alpha = ambient, beta =diffuse, nShiny=shininess
    private double alpha, beta, nShiny;

    Material(double a, double b, double n) {
        alpha = a; //0 to 1
        beta = b; //0 to 1
        nShiny = n; // >=1
    }

    //ensure f is between 0 and 1
    private static double limit01(double x) {
        return x < 0 ? 0 : (x > 1 ? 1 : x);
    }


    // Compute Phong lighting scale factor for a point
    public double calculate(Point4 normWorld, Point4 worldLightDir, Point4 viewDir, double shadow) {
        //Normalize copies (treat as vectors)
        Point4 N = Point4.createVector(normWorld.x, normWorld.y, normWorld.z);
        N.normalize();
        Point4 L = Point4.createVector(worldLightDir.x, worldLightDir.y, worldLightDir.z);
        L.normalize();
        Point4 V = Point4.createVector(viewDir.x, viewDir.y, viewDir.z);
        V.normalize();

        // Diffuse is between 0 and the dot product of N and L
        double NL = Math.max(0.0, Point4.dot(N, L)); //diffuse

        //reflection vector R = 2(N*L)N-L
        Point4 R = Point4.createVector(2 * NL * N.x - L.x, 2 * NL * N.y - L.y, 2 * NL * N.z - L.z);
        R.normalize();


        double RV = Math.max(0.0, Point4.dot(R, V)); //specular
        double spec = Math.pow(RV, nShiny);


        // Specular weight = whatever remains after ambient (alpha) and diffuse (beta). Never negative.
        double ks = Math.max(0.0, 1.0 - alpha - beta);

        double intensity = 1.2;      // light boost
        double specBoost = 2.0;      // visible but not overblown

        //phong lighting model f =
        double f = alpha + shadow * intensity * (beta * NL + ks * specBoost * spec);
        return limit01(f);



    }

    /*
    Put in your implementation of the Phong model - the result should be some scale factor
    0<=f<=1 which will later be applied to the surface colour.

    Define methods and parameters as you see fit.
     */
}
