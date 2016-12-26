package mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.font.BitmapText;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Matrix3f;
import com.jme3.math.Quaternion;
import com.jme3.math.Transform;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Spatial;
import com.jme3.scene.VertexBuffer.Type;
import com.jme3.scene.shape.Box;
import com.jme3.util.BufferUtils;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import jme3tools.optimize.GeometryBatchFactory;

/**
 * This is the Main Class of your Game. You should only do initialization here.
 * Move your Logic into AppStates or Controls
 * @author normenhansen
 */
public class Main extends SimpleApplication {

    private VoxelAppState _voxelAppState;
    
    private DirectionalLight directionalLight;
    private Vector3f[] dirLight;
    
    private double angle = 0f;
    
    public static void main(String[] args) {
        Main app = new Main();
        app.start();
    }

    @Override
    public void simpleInitApp() {
       
        // instan
        
        this.getFlyByCamera().setMoveSpeed(64.0f);
        
        // creation du AppStateVoxel
        _voxelAppState = new VoxelAppState();
        this.stateManager.attach(_voxelAppState);
            
        
      
       
       // Light
       AmbientLight ambientLight = new AmbientLight();
       ambientLight.setColor(new ColorRGBA((1f/255f)*157f,1f,(1f/255f)*242f,1f));
       rootNode.addLight(ambientLight);
       
       directionalLight = new DirectionalLight();
       directionalLight.setColor(new ColorRGBA(1,1,1,1));
       directionalLight.setDirection(new Vector3f(0.5f,-0.5f,-0.5f).normalizeLocal());
       rootNode.addLight(directionalLight);
  
     
         
    }

    @Override
    public void simpleUpdate(float tpf)
    {
          
              
   
    }

    @Override
    public void simpleRender(RenderManager rm) {
        //TODO: add render code
    }
}
