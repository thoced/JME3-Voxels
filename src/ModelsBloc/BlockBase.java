/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ModelsBloc;

import com.jme3.math.Vector2f;
import com.jme3.texture.TextureArray;
import mygame.SingleUvSelect;

/**
 *
 * @author thonon
 */
public abstract class BlockBase {
    
    protected Vector2f[] UvGrass = new Vector2f[4];
    protected Vector2f[] UvBrick = new Vector2f[4];
    protected Vector2f[] UvBrackWithGrass = new Vector2f[4];
    protected Vector2f[] UvEarth = new Vector2f[4];
    protected Vector2f[] UvEarthWithGrass = new Vector2f[4];
    
    
 
    public BlockBase(){
        // GRASS
        UvGrass[0] = new Vector2f(0f,0f);
        UvGrass[1] = new Vector2f(0.25f,0f);
        UvGrass[2] = new Vector2f(0.25f,0.25f);
        UvGrass[3] = new Vector2f(0f,0.25f);
        
        // BRICK
        UvBrick[0] = new Vector2f(0.5f,0f);
        UvBrick[1] = new Vector2f(0.75f,0f);
        UvBrick[2] = new Vector2f(0.75f,0.25f);
        UvBrick[3] = new Vector2f(0.50f,0.25f);
        
        //Earth
        UvEarth[0] = new Vector2f(0.5f,0.25f);
        UvEarth[1] = new Vector2f(0.75f,0.25f);
        UvEarth[2] = new Vector2f(0.75f,0.50f);
        UvEarth[3] = new Vector2f(0.50f,0.50f);
        
         // BRICK + GRASS
        UvBrackWithGrass[0] = new Vector2f(0.25f,0f);
        UvBrackWithGrass[1] = new Vector2f(0.50f,0f);
        UvBrackWithGrass[2] = new Vector2f(0.50f,0.25f);
        UvBrackWithGrass[3] = new Vector2f(0.25f,0.25f);
        
          // earth + GRASS
        UvEarthWithGrass[0] = new Vector2f(0.25f,0.25f);
        UvEarthWithGrass[1] = new Vector2f(0.50f,0.25f);
        UvEarthWithGrass[2] = new Vector2f(0.50f,0.50f);
        UvEarthWithGrass[3] = new Vector2f(0.25f,0.50f);
    }
    public abstract Vector2f[] getTop();
    public abstract Vector2f[] getDown();
    public abstract Vector2f[] getLeft();
    public abstract Vector2f[] getRight();
    public abstract Vector2f[] getFront();
    public abstract Vector2f[] getBehind();
}
