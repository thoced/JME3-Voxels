package mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.effect.ParticleEmitter;
import com.jme3.effect.ParticleMesh;
import com.jme3.font.BitmapText;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Matrix3f;
import com.jme3.math.Quaternion;
import com.jme3.math.Ray;
import com.jme3.math.Transform;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.post.FilterPostProcessor;
import com.jme3.post.filters.FogFilter;
import com.jme3.post.filters.LightScatteringFilter;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Spatial;
import com.jme3.scene.VertexBuffer.Type;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Sphere;
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
public class Main extends SimpleApplication implements ActionListener{

    private VoxelAppState _voxelAppState;
    
    private DirectionalLight directionalLight;
    private Vector3f[] dirLight;
    
    private double angle = 0f;
    
    private Ray _rayView;
    private CollisionResults _viewCollisionResults;
    private Geometry _mark;
    private Vector3f _offsetVoxel;
    
    public static void main(String[] args) {
        Main app = new Main();
        app.start();
    }

    @Override
    public void simpleInitApp() {
       
        // instance
        this.getFlyByCamera().setMoveSpeed(64.0f);
        // distance de vue
        this.getCamera().setFrustumFar(256);
        this.guiViewPort.setBackgroundColor(new ColorRGBA(0.96f, 0.99f, 0.99f, 1.0f));
        // Fog
         /** Add fog to a scene */
        FilterPostProcessor fpp=new FilterPostProcessor(assetManager);
        FogFilter fog = new FogFilter();
        fog.setFogColor(new ColorRGBA(0.96f, 0.99f, 0.99f, 1.0f));
        fog.setFogDistance(212);
        fog.setFogDensity(1.2f);
        fpp.addFilter(fog);
        viewPort.addProcessor(fpp);

       // creation du AppStateVoxel
        _voxelAppState = new VoxelAppState();
        this.stateManager.attach(_voxelAppState);
            
       // Light
       AmbientLight ambientLight = new AmbientLight();
       ambientLight.setColor(new ColorRGBA((1f/255f)*157f,1f,(1f/255f)*242f,1f));
       rootNode.addLight(ambientLight);
       
       directionalLight = new DirectionalLight();
       directionalLight.setColor(new ColorRGBA(1,0.5f,0.5f,1));
       directionalLight.setDirection(new Vector3f(0.5f,-0.8f,-0.3f).normalizeLocal());
       rootNode.addLight(directionalLight);
  
       // initKeys
       this.initKeys();
       // ray view
       _rayView = new Ray();
       _viewCollisionResults= new CollisionResults();
       _offsetVoxel = new Vector3f(0.5f,0.5f,0.5f);
       // initMark
       this.initMark();
 
    }
    
     private void initKeys()
     {
                 
        inputManager.addMapping("ADD_VOXEL",
          new KeyTrigger(KeyInput.KEY_SPACE),
          new MouseButtonTrigger(MouseInput.BUTTON_LEFT)); 
        
        inputManager.addMapping("SUB_VOXEL", 
                new MouseButtonTrigger(MouseInput.BUTTON_RIGHT));
        
        inputManager.addMapping("ADD_LIGHTPROBE", 
                new MouseButtonTrigger(MouseInput.BUTTON_MIDDLE));
        
        
        
        inputManager.addListener(this, "ADD_VOXEL");
        inputManager.addListener(this, "SUB_VOXEL");
        inputManager.addListener(this, "ADD_LIGHTPROBE");
     }
     
     private void initMark()
     {
        Sphere sphere = new Sphere(30, 30, 0.2f);
        _mark = new Geometry("BOOM!", sphere);
        Material mark_mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mark_mat.setColor("Color", ColorRGBA.Red);
        _mark.setMaterial(mark_mat);
        rootNode.attachChild(_mark);
       
     }

    @Override
    public void simpleUpdate(float tpf)
    {
          _rayView.setOrigin(this.getCamera().getLocation());
          _rayView.setDirection(this.getCamera().getDirection());
          
          _viewCollisionResults.clear();
          
          _voxelAppState.getNodeVoxelChunk().collideWith(_rayView, _viewCollisionResults);
          if(_viewCollisionResults.size() > 0)
          {
             CollisionResult result =  _viewCollisionResults.getClosestCollision();
             _mark.setLocalTranslation(result.getContactPoint());
             
          }
              
   
    }

    @Override
    public void simpleRender(RenderManager rm) {
        //TODO: add render code
    }

    @Override
    public void onAction(String name, boolean isPressed, float tpf) 
    {
        if(isPressed)
        {
            
                // creation du collisionresult
                CollisionResults results = new CollisionResults();
                // creation du rayon
                Ray r = new Ray(this.getCamera().getLocation(),this.getCamera().getDirection());
                _voxelAppState.getNodeVoxelChunk().collideWith(r, results);

                if(results.size() > 0)
                {
                    // il y a une collision
                    CollisionResult closest = results.getClosestCollision();
                    
                    
                    if(name.equals("ADD_VOXEL"))
                    {
                        // on récupère lecontact point
                        Vector3f voxelPos = closest.getContactPoint().add(closest.getContactNormal().divide(2));
                        _voxelAppState.addVoxelToGrid(voxelPos.add(_offsetVoxel)); // offsetVoxel est égale au 0.5f de décallage
                    }
                    else if(name.equals("SUB_VOXEL"))
                    {
                        // position du voxel a supprimer
                          Vector3f voxelPos = closest.getContactPoint().subtract(closest.getContactNormal().divide(2));
                         _voxelAppState.subVoxelToGrid(voxelPos.add(_offsetVoxel)); // offsetVoxel est égale au 0.5f de décallage
                    }
                    
                    if(name.equals("ADD_LIGHTPROBE"))
                    {
                         Vector3f voxelPos = closest.getContactPoint().add(closest.getContactNormal().divide(2f));
                         _voxelAppState.addLightProbe(voxelPos);
                    }
                        
                }
 
        }
    }
}
