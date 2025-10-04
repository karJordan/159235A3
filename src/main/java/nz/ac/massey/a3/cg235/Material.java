package nz.ac.massey.a3.cg235;

/*
    This class handles the reflective properties of the surface.

    Makes sense to do it this way. How the shading is done, does depend on the material property
 */
public class Material {

    // Parameters of the Phong reflection model as described in the lectures
    private double alpha, beta, nShiny;

    Material(double a, double b, double n) {
        alpha = a;
        beta = b;
        nShiny = n;
    }

    /*
    Put in your implementation of the Phong model - the result should be some scale factor
    0<=f<=1 which will later be applied to the surface colour.

    Define methods and parameters as you see fit.
     */
}
