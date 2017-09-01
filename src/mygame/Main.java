package mygame;

import appState.AssetLoaderAppState;
import appState.CameraScrollAppState;
import appState.FinderAppState;
import appState.MovementAppState;
import com.jme3.app.SimpleApplication;
import com.jme3.bullet.BulletAppState;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.effect.ParticleEmitter;
import com.jme3.effect.ParticleMesh;
import com.jme3.font.BitmapText;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseAxisTrigger;
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
import com.jme3.post.ssao.SSAOFilter;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.VertexBuffer.Type;
import com.jme3.scene.control.CameraControl;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Sphere;
import com.jme3.shadow.CompareMode;
import com.jme3.shadow.DirectionalLightShadowFilter;
import com.jme3.shadow.DirectionalLightShadowRenderer;
import com.jme3.shadow.EdgeFilteringMode;
import com.jme3.util.BufferUtils;
import com.jme3.util.SkyFactory;
import com.jme3.water.WaterFilter;
import controllers.bonController;
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
public class Main extends SimpleApplication implements ActionListener,AnalogListener{

    private BulletAppState _bulletAppState;
    private VoxelAppState _voxelAppState;
    private FinderAppState _finderAppState;
    private MovementAppState _movementAppState;
    private CameraScrollAppState _cameraAppState;
    
    private DirectionalLight directionalLight;
    private Vector3f[] dirLight;
    
    private double angle = 0f;
    
    private Ray _rayView;
    private CollisionResults _viewCollisionResults;
    private Node _mark;
    private Vector3f _offsetVoxel;
    
    private Spatial bon;
     
    public static void main(String[] args) {
        Main app = new Main();
        app.start();
    }

    @Override
    public void simpleInitApp() {
       
        // instance
        this.getFlyByCamera().setMoveSpeed(32.0f);
        this.getFlyByCamera().setEnabled(false);
        // distance de vue
        this.getCamera().setFrustumFar(312);
        this.guiViewPort.setBackgroundColor(new ColorRGBA(0.96f, 0.99f, 0.99f, 1.0f));
        
        // creation du AssetAppState
        this.stateManager.attach(new AssetLoaderAppState());
        
        // creation de la physique
        _bulletAppState = new BulletAppState();
        this.stateManager.attach(_bulletAppState);
        
       // creation du AppStateVoxel
        _voxelAppState = new VoxelAppState();
        this.stateManager.attach(_voxelAppState);
        
       // creation du AppStateFinder
       _finderAppState = new FinderAppState();
       this.stateManager.attach(_finderAppState);
       
       // creation du MovementAppState
       _movementAppState = new MovementAppState();
       //this.stateManager.attach(_movementAppState);
       
       // creation du CameraScrollAppState
       _cameraAppState = new CameraScrollAppState();
       this.stateManager.attach(_cameraAppState);
            
       // Light
       AmbientLight ambientLight = new AmbientLight();
        ambientLight.setColor(new ColorRGBA(0.04f,0.075f,0.075f,1));
       rootNode.addLight(ambientLight);
       
       directionalLight = new DirectionalLight();
       directionalLight.setColor(new ColorRGBA(0.6f,0.6f,0.5f,1));
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
       // init camera
       this.initCamera();
 
       //test bonhomme
       bon = assetManager.loadModel("Models/bilou/bilou.j3o");
       bon.addControl(new bonController());
       bon.setShadowMode(RenderQueue.ShadowMode.Cast);
       
       // Fog
         /** Add fog to a scene */
        FilterPostProcessor fpp=new FilterPostProcessor(assetManager);
        FogFilter fog = new FogFilter();
        fog.setFogColor(new ColorRGBA(0.96f, 0.99f, 0.99f, 1.0f));
        fog.setFogDistance(340f);
        fog.setFogDensity(0.4f);
        fpp.addFilter(fog);
        viewPort.addProcessor(fpp);

       //shadow
       final int SHADOWMAP_SIZE=4096;
        DirectionalLightShadowFilter dlsf = new DirectionalLightShadowFilter(assetManager, SHADOWMAP_SIZE, 3);
        dlsf.setLight(directionalLight);
        dlsf.setEdgeFilteringMode(EdgeFilteringMode.Nearest);
        dlsf.setEnabled(true);
        dlsf.setShadowCompareMode(CompareMode.Hardware);
        fpp.addFilter(dlsf);
       viewPort.addProcessor(fpp);
       
      
        
           
       // water see
       Vector3f lightDir = new Vector3f(-4.9f, -1.3f, 5.9f);
      // fpp = new FilterPostProcessor(assetManager);
       WaterFilter water = new WaterFilter(rootNode, lightDir);
       water.setWaterHeight(0);
       water.setFoamIntensity(0.2f);
       water.setWaterHeight(0.8f);
       water.setSpeed(0.4f);
       water.setMaxAmplitude(2f);
       water.setDeepWaterColor(new ColorRGBA(0.2f,0.5f,0.8f,1f));
       fpp.addFilter(water);
       viewPort.addProcessor(fpp);
       
       // sky
       getRootNode().attachChild(SkyFactory.createSky(getAssetManager(), "Textures/Sky/Skysphere.jpg", SkyFactory.EnvMapType.SphereMap));
       
    }
    
    private void initCamera()
    {
        
        
        
    }
    
     private void initKeys()
     {
        // input du bouton centrale de la souris
        inputManager.addMapping("ZOOM_CAMERA_IN", new MouseAxisTrigger(MouseInput.AXIS_WHEEL,false));
        inputManager.addMapping("ZOOM_CAMERA_OUT", new MouseAxisTrigger(MouseInput.AXIS_WHEEL,true));
        
        
                 
        inputManager.addMapping("ADD_VOXEL",
          new KeyTrigger(KeyInput.KEY_SPACE),
          new MouseButtonTrigger(MouseInput.BUTTON_LEFT)); 
        
        inputManager.addMapping("SUB_VOXEL", 
                new MouseButtonTrigger(MouseInput.BUTTON_RIGHT));
        
         inputManager.addMapping("START_PATH", 
                new KeyTrigger(KeyInput.KEY_1));
         
         inputManager.addMapping("GOAL_PATH", 
                new KeyTrigger(KeyInput.KEY_2));
         
          inputManager.addMapping("BON", 
                new KeyTrigger(KeyInput.KEY_B));
        
        inputManager.addListener(this, "ZOOM_CAMERA_IN");
        inputManager.addListener(this, "ZOOM_CAMERA_OUT");
        inputManager.addListener(this, "ADD_VOXEL");
      //  inputManager.addListener(this, "SUB_VOXEL");
        inputManager.addListener(this, "START_PATH");
        inputManager.addListener(this, "GOAL_PATH");
        inputManager.addListener(this, "BON");
     }
     
     private void initMark()
     {
       /* Sphere sphere = new Sphere(30, 30, 0.2f);
        _mark = new Geometry("BOOM!", sphere);
        Material mark_mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mark_mat.setColor("Color", ColorRGBA.Red);
        _mark.setMaterial(mark_mat);*/
        _mark = new Node();
         Spatial select = this.getAssetManager().loadModel("Models/Utils/Select/select.j3o");
         select.setLocalTranslation(0,0.05f,0);
         _mark.attachChild(select);
         _mark.setShadowMode(RenderQueue.ShadowMode.Receive);
         
        rootNode.attachChild(_mark);
       
     }

    @Override
    public void simpleUpdate(float tpf)
    {
          _rayView.setOrigin(_cameraAppState.getCam().getLocation());
          _rayView.setDirection(_cameraAppState.getDirectionCursor());
          
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

                
                Ray r = new Ray(_cameraAppState.getCam().getLocation(),_cameraAppState.getDirectionCursor());
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
                    
                      if(name.equals("START_PATH")){
                          Vector3f voxelPos = closest.getContactPoint().add(closest.getContactNormal().divide(2));
                          _finderAppState.setStartPoint(voxelPos);
                      }
                      
                      if(name.equals("GOAL_PATH")){
                          Vector3f voxelPos = closest.getContactPoint().add(closest.getContactNormal().divide(2));
                          _finderAppState.setGoalPoint(voxelPos);
                      }
                      
                      if(name.equals("BON")){
                          
                          Spatial nB = bon.clone();
                          bonController c = nB.getControl(bonController.class);
                          c.setSpeed((float)Math.random() * 2f);
                          
                          Vector3f voxelPos = closest.getContactPoint();
                          nB.setLocalTranslation(voxelPos);
                          rootNode.attachChild(nB);
                      }
                        
                }
 
        }
    }

    @Override
    public void onAnalog(String name, float f, float f1) {
        
        System.out.println(f);
        
        if(name.equals("ZOOM_CAMERA_IN"))
            _cameraAppState.setZoom(f); 
          if(name.equals("ZOOM_CAMERA_OUT"))
            _cameraAppState.setZoom(-f); 
    }
}
