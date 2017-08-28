/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ModelsBloc;

import com.jme3.math.Vector2f;

/**
 *
 * @author thonon
 */
public class BEarth extends BlockBase{

    public BEarth() {
        super();
    }

    @Override
    public Vector2f[] getTop() {
        return this.UvEarth;
    }

    @Override
    public Vector2f[] getDown() {
       return this.UvEarth;
    }

    @Override
    public Vector2f[] getLeft() {
        return this.UvEarth;
    }

    @Override
    public Vector2f[] getRight() {
       return this.UvEarth;
    }

    @Override
    public Vector2f[] getFront() {
        return this.UvEarth;
    }

    @Override
    public Vector2f[] getBehind() {
        return this.UvEarth;
    }
    
}
