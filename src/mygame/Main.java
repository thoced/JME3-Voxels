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
import com.jme3.shadow.DirectionalLightShadowFilter;
import com.jme3.shadow.DirectionalLightShadowRenderer;
import com.jme3.shadow.EdgeFilteringMode;
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
   
    
    private double _angle = 5;
    private double _backAngle = 5;
 
    
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
       
       // viewPort.addProcessor(fpp);

       // creation du AppStateVoxel
        _voxelAppState = new VoxelAppState();
        this.stateManager.attach(_voxelAppState);
            
       // Light
       AmbientLight ambientLight = new AmbientLight();
      // ambientLight.setColor(new ColorRGBA((1f/255f)*157f,1f,(1f/255f)*242f,1f));
       ambientLight.setColor(new ColorRGBA(0.5f,0.4f,0.4f,1));
       rootNode.addLight(ambientLight);
       
       directionalLight = new DirectionalLight();
       directionalLight.setColor(new ColorRGBA(0.6f,0.6f,0.5f,1));
       directionalLight.setDirection(new Vector3f(0.5f,-0.8f,-0.3f).normalizeLocal());
       rootNode.addLight(directionalLight);
       
       // fog
        // Fog
         /** Add fog to a scene */
        FilterPostProcessor fpp=new FilterPostProcessor(assetManager);
        FogFilter fog = new FogFilter();
        fog.setFogColor(new ColorRGBA(0.96f, 0.99f, 0.99f, 1.0f));
        fog.setFogDistance(212);
        fog.setFogDensity(1.2f);
        fpp.addFilter(fog);
       
       // experimantal shadow directional
       /* Drop shadows */
        final int SHADOWMAP_SIZE=5120;
        DirectionalLightShadowRenderer dlsr = new DirectionalLightShadowRenderer(assetManager, SHADOWMAP_SIZE, 3);
        dlsr.setLight(directionalLight);
        dlsr.setEdgesThickness(12);
        dlsr.setRenderBackFacesShadows(true);
        dlsr.setShadowIntensity(0.55f);
        viewPort.addProcessor(dlsr);
        
        DirectionalLightShadowFilter dlsf = new DirectionalLightShadowFilter(assetManager, SHADOWMAP_SIZE, 3);
        dlsf.setLight(directionalLight);
        dlsf.setEnabled(true);
       // FilterPostProcessor fpp = new FilterPostProcessor(assetManager);
        fpp.addFilter(dlsf);
        viewPort.addProcessor(fpp);
  
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
        
        inputManager.addListener(this, "ADD_VOXEL");
        inputManager.addListener(this, "SUB_VOXEL");
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
           
      // mouvement de lumière
      Quaternion q = new Quaternion();
      q.fromAngleAxis((float) Math.toRadians(_angle), new Vector3f(1,0,0));
           
      _angle+= (2f * tpf);
      
      
      if(_angle > 175f)
      {
          //rootNode.removeLight(directionalLight);
           _angle = 5f;
          
      }
       
     // rootNode.removeLight(directionalLight);
     if(Math.abs(_angle - _backAngle) > 0.1f)
     {
      _backAngle = _angle;
      directionalLight.setDirection(q.getRotationColumn(2).normalizeLocal().add(new Vector3f(0.5f,-0.8f,-0.3f)));
     
     }
     // rootNode.addLight(directionalLight);
     // directionalLight.setDirection(_offsetVoxel);
   
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
                    else
                    {
                        // position du voxel a supprimer
                          Vector3f voxelPos = closest.getContactPoint().subtract(closest.getContactNormal().divide(2));
                         _voxelAppState.subVoxelToGrid(voxelPos.add(_offsetVoxel)); // offsetVoxel est égale au 0.5f de décallage
                    }
                        
                }
 
        }
    }
}
