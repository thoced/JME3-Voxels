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
public class BBrick extends BlockBase{

    public BBrick() {
        super();
    }

    @Override
    public Vector2f[] getTop() {
       return UvBrick;
    }

    @Override
    public Vector2f[] getDown() {
        return UvBrick;
    }

    @Override
    public Vector2f[] getLeft() {
        return UvBrick;
    }

    @Override
    public Vector2f[] getRight() {
        return UvBrick;
    }

    @Override
    public Vector2f[] getFront() {
        return UvBrick;
    }

    @Override
    public Vector2f[] getBehind() {
        return UvBrick;
    }

    
   
    
}
