package nz.ac.massey.a3;

/*
    Class to implement a texture mapping procedure
 */


import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

public class TextureMap {
    private BufferedImage texture;

    //constructor read image file and store in 'texture'
    public TextureMap(String filename){
        try{
            texture = ImageIO.read(new File(filename));
        }
        catch (IOException e){
            System.err.println("unable to load" + filename);
            texture = null;
        }
    }

    //sample the texture image
    public Color pickColour(double u, double v){
        if (texture == null){ return Color.black;}//black default if not texture loaded


        //force u,v range to [0,1]
        u = Math.max(0.0,Math.min(1.0, u));
        v = Math.max(0.0,Math.min(1.0, v));

        //convert to pixel coords
        int width = texture.getWidth();
        int height = texture.getHeight();
        int x = (int) ((width -1) * u);
        int y = (int)((height -1)*(1.0-v));

        int rgb = texture.getRGB(x,y);
        return new Color(rgb);
    }

    /*
    Sample the texture image at the given texel values (u,v)

    (u, v) are NORMALIZED, i.e. 0<=u<=1, 0<=v<=1

    Complete the implementation of pickColour().

    Here it just returns a default colour

     */

    //public Color pickColour(double u, double v) { return Color.BLACK;}

}
