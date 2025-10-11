/*
Start up package for 159.235 Assignment 3 (Semester 2, 2022)
 */
package nz.ac.massey.a3;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class Main {
    static double ambLight = .45;

    public static ArrayList<ThreeDSurface> makeBox(double x, double y, double z, Point4 center, Color colour, Material mat) {

        ArrayList<ThreeDSurface> faces = new ArrayList<>();
        double hx = x * .5;
        double hy = y * .5;
        double hz = z * .5;//half sizes to position away from center

        SurfaceGeometry quad = new Square();
        SurfaceColour col = new UniformColour(colour);
        //right
        faces.add(new ThreeDSurface(
                quad, mat, col,
                Placement.placeModel(
                        Point4.createPoint(center.x + hx, center.y, center.z),
                        Point4.createPoint(0, Math.toRadians(90), 0),
                        Point4.createPoint(z, y, 1)
                )
        ));
        //left
        faces.add(new ThreeDSurface(
                quad, mat, col,
                Placement.placeModel(
                        Point4.createPoint(center.x - hx, center.y, center.z),
                        Point4.createPoint(0, Math.toRadians(-90), 0),
                        Point4.createPoint(z, y, 1)
                )
        ));
        //front
        faces.add(new ThreeDSurface(
                quad, mat, col,
                Placement.placeModel(
                        Point4.createPoint(center.x, center.y + hy, center.z),
                        Point4.createPoint(Math.toRadians(-90), 0, 0),
                        Point4.createPoint(x, z, 1)
                )
        ));
        //back
        faces.add(new ThreeDSurface(
                quad, mat, col,
                Placement.placeModel(
                        Point4.createPoint(center.x, center.y - hy, center.z),
                        Point4.createPoint(Math.toRadians(90), 0, 0),
                        Point4.createPoint(x, z, 1)
                )
        ));
        //top
        faces.add(new ThreeDSurface(
                quad, mat, col,
                Placement.placeModel(
                        Point4.createPoint(center.x, center.y, center.z + hz),
                        Point4.createPoint(0, 0, 0),
                        Point4.createPoint(x, y, 1)
                )
        ));
        //Bottom
        faces.add(new ThreeDSurface(
                quad, mat, col,
                Placement.placeModel(
                        Point4.createPoint(center.x, center.y, center.z - hz),
                        Point4.createPoint(Math.toRadians(180), 0, 0),
                        Point4.createPoint(x, y, 1)
                )
        ));
        return faces;
    }

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
        Point4 pLightW = Point4.createPoint(80, -40, 100);

        // Get a camera with field of view 15 degrees in y
        double fovy = Math.toRadians(15);
        int npixx = 801;
        int npixy = 801;
        Camera camera = Camera.standardCamera(fovy, npixx, npixy);

        // Position and orientation of the camera in the world scene
        Point4 pCam = Point4.createPoint(90, -100, 30);
        Point4 pTarg = Point4.createPoint(0, 0, 0);
        Point4 vUp = Point4.createVector(0, 0, -1);
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
                new Material(ambLight, 0.5, 10),
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
                new Material(ambLight, 0.5, 10),
                new UniformColour(Color.GREEN),
                Placement.placeModel(
                        Point4.createPoint(0, 0, 0),     // move to y = -3
                        Point4.createPoint(Math.toRadians(0), 0, 0), // rotate so normal points -Y
                        Point4.createPoint(50, 50, 1)     // make it big
                )
        );

        scene.add(ground);

        ThreeDSurface wall = new ThreeDSurface(
                new Square(),                         // z=0 in local coords
                new Material(ambLight, 0.5, 10),
                new UniformColour(Color.GREEN),
                Placement.placeModel(
                        Point4.createPoint(0, 20, 20),     // move to y = -3
                        Point4.createPoint(Math.toRadians(90), 0, 0), // rotate so normal points -Y
                        Point4.createPoint(50, 50, 1)     // make it big
                )
        );

        scene.add(wall);

        ThreeDSurface wall2 = new ThreeDSurface(
                new Square(),                         // z=0 in local coords
                new Material(ambLight-.2, 0.5, 10),
                new UniformColour(Color.GREEN),
                Placement.placeModel(
                        Point4.createPoint(-20, 0, 20),     // move to y = -3
                        Point4.createPoint(0, Math.toRadians(90), 0), // rotate so normal points -Y
                        Point4.createPoint(50, 50, 1)     // make it big
                )
        );

        scene.add(wall2);

        ArrayList<ThreeDSurface> box = makeBox(6, 1, 4,
                Point4.createPoint(8, 3, 4/2.0), Color
                        .RED, new Material(ambLight, 0.5, 20));
        scene.add(box);

        // Render the scene at the given camera and light source
        scene.render(camera, pLightW);

        // Uncomment if you want to verify the camera target point in the scene
        // putAxes(camera.image);

        // Display image in a JPanel/JFrame
        Display.show(camera.image);

        // Uncomment if you want to save your scene in an image file
        //Display.write(camera.image, "scene.png");

    }
}
