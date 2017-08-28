/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ModelsBloc;

import com.jme3.math.Vector2f;
import mygame.SingleUvSelect;

/**
 *
 * @author thonon
 */
public class BGrass extends BlockBase {

    public BGrass() {
       super();
    }

    @Override
    public Vector2f[] getTop() {
        return UvGrass;
    }

    @Override
    public Vector2f[] getDown() {
        return UvBrackWithGrass;
    }

    @Override
    public Vector2f[] getLeft() {
        return UvBrackWithGrass;
    }

    @Override
    public Vector2f[] getRight() {
         return UvBrackWithGrass;
    }

    @Override
    public Vector2f[] getFront() {
         return UvBrackWithGrass;
    }

    @Override
    public Vector2f[] getBehind() {
         return UvBrackWithGrass;
    }
    
}
