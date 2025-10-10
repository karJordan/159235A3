/*
Start up package for 159.235 Assignment 3 (Semester 2, 2022)
 */
package nz.ac.massey.a3;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Main {

    /* Draw black lines at the centre of the image. Call this to verify the camera
    pointing direction
     */
    public static void putAxes(BufferedImage image) {
        int npixx = image.getWidth();
        int npixy = image.getHeight();
        int L = (int) (Math.floor(0.0625 * npixx));
        for (int k = 0; k < L; ++k) {
            int rgb = 0;
            image.setRGB(k + (npixx - 1) / 2, (npixy - 1) / 2, rgb);
            image.setRGB((npixx - 1) / 2, k + (npixy - 1) / 2, rgb);
        }
    }

    /* Generate a test planar surface with a uniform colour
        pD : x,y,z position in scene
        pA : rotation angles around x, y, z-axes
        pS : scaling in x,y,z
     */
    public static ThreeDSurface testSurface(Point4 pD, Point4 pA, Point4 pS) {
        return new ThreeDSurface(
                new Square(),
                new Material(0.0, 0.5, 10),
                new UniformColour(Color.GRAY),
                Placement.placeModel(pD, pA, pS)
        );
    }


    public static void main(String[] args) {

        // Position of light source in world coordinates
        //Point4 pLightW = Point4.createPoint(50, 50, 100);
        Point4 pLightW = Point4.createPoint(0, -40, 100);

        // Get a camera with field of view 15 degrees in y
        double fovy = Math.toRadians(15);
        int npixx = 801;
        int npixy = 801;
        Camera camera = Camera.standardCamera(fovy, npixx, npixy);

        // Position and orientation of the camera in the world scene
        Point4 pCam  = Point4.createPoint(0,-80, 20);
        Point4 pTarg = Point4.createPoint(0, 0, 0);
        Point4 vUp   = Point4.createVector(0, 0, -1);
        camera.rePoint(pCam, pTarg, vUp);

        // Get a scene graph that manages the list of surfaces to be rendered
        SceneGraph scene = new SceneGraph();


        //Add a planar square surface at the origin
        //Point4 pD = Point4.createPoint(0,0,0);
        //Point4 pA = Point4.createPoint(0,0,0);
        //Point4 pS = Point4.createPoint(10,10,1);
       // scene.add(testSurface(pD, pA, pS));


        // add this:
        ThreeDSurface sphere = new ThreeDSurface(
                new Sphere(3.0),
                new Material(0.0, 0.5, 10),
                new UniformColour(Color.BLUE),
                Placement.placeModel(
                        Point4.createPoint(0, 0, 3),     // position
                        Point4.createPoint(0, 0, 0),     // rotation (radians)
                        Point4.createPoint(1, 1, 1)      // scale
                )
        );
        scene.add(sphere);



        // Main.java (after you add the sphere)
        ThreeDSurface ground = new ThreeDSurface(
                new Square(),                         // z=0 in local coords
                new Material(0.0, 0.5, 10),
                new UniformColour(Color.GREEN),
                Placement.placeModel(
                        Point4.createPoint(0, -3, 0),     // move to y = -3
                        Point4.createPoint(Math.toRadians(0), 0, 0), // rotate so normal points -Y
                        Point4.createPoint(50, 50, 1)     // make it big
                )
        );

        scene.add(ground);



        // Render the scene at the given camera and light source
        scene.render(camera, pLightW);

        // Uncomment if you want to verify the camera target point in the scene
        putAxes(camera.image);

        // Display image in a JPanel/JFrame
        Display.show(camera.image);

        // Uncomment if you want to save your scene in an image file
        //Display.write(camera.image, "scene.png");

    }
}
