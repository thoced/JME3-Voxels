/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ModelsData;

import com.jme3.math.Vector3f;
import controllers.AvatarControl;
import pathfinding.Path;

/**
 *
 * @author thonon
 */
public class TaskFinder {
    
    public AvatarControl control;
    
    public Vector3f posDestination;
    
    public Path path;

    public TaskFinder(AvatarControl control, Vector3f posDest) {
        this.control = control;
        this.posDestination = posDest;
       
    }

    public Vector3f getPosDestination() {
        return posDestination;
    }

    public void setPosDestination(Vector3f posDestination) {
        this.posDestination = posDestination;
    }

        
    public AvatarControl getControl() {
        return control;
    }

    public void setControl(AvatarControl control) {
        this.control = control;
    }

    public Path getPath() {
        return path;
    }

    public void setPath(Path path) {
        this.path = path;
    }
    
    
}
