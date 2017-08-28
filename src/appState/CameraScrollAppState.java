/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package appState;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.input.InputManager;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;

/**
 *
 * @author thonon
 */
public class CameraScrollAppState extends AbstractAppState {
       
    private SimpleApplication app;

    private Camera cam;
    
    private InputManager input;
    
    private Vector2f  centerScreen;
    
    private float speed = 16f;
    
    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
        this.app = (SimpleApplication)app;
        this.cam = app.getCamera();
        this.input = app.getInputManager();
        
        Quaternion quat = new Quaternion();
        quat.lookAt(new Vector3f(0,-0.3f,0.5f), Vector3f.UNIT_Y);             
       
        cam.setLocation(new Vector3f(128,32,128));
       // cam.setRotation(quat);
        cam.setAxes(quat);
        
        // centre screen
        centerScreen = new Vector2f(cam.getWidth()/2f,cam.getHeight()/2);
        
      
    } 

    public Camera getCam() {
        return cam;
    }
    
    
    
    public Vector3f getDirectionCursor(){
       
        Vector3f click3d = cam.getWorldCoordinates(input.getCursorPosition(), 0f).clone();
        Vector3f dir = cam.getWorldCoordinates(input.getCursorPosition(), 1f).subtractLocal(click3d).normalizeLocal();
        return dir;
        
    }
    
    @Override
    public void update(float tpf) {
       Vector2f posCursor = input.getCursorPosition();
       float distance = posCursor.distance(centerScreen);
       Vector2f diff = (centerScreen.subtract(posCursor)).normalize();
       
       if(distance > cam.getWidth()*0.1f){
           diff.multLocal(speed * tpf * (distance*0.001f));
           cam.setLocation(cam.getLocation().add(new Vector3f(diff.x,0,-diff.y)));
       }
       
       
       
    }
    
    @Override
    public void cleanup() {
        super.cleanup();
        //TODO: clean up what you initialized in the initialize method,
        //e.g. remove all spatials from rootNode
        //this is called on the OpenGL thread after the AppState has been detached
    }
    
}
