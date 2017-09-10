/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package appState;

import ModelsData.SingleGlobal;
import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseAxisTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Ray;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import controllers.bonController;
import mygame.VoxelAppState;

/**
 *
 * @author thonon
 */
public class CameraScrollAppState extends AbstractAppState implements  ActionListener,AnalogListener {
       
    private SimpleApplication app;

    private Camera cam;
    
    private InputManager input;
    
    private VoxelAppState voxel;
    
    private CollisionResults results;
    
    private Vector2f  centerScreen;
    
    private float speed = 16;
    
    private float speedZoom = 64f;
    private float speedZoomlerp = 4f;
    
    private float speedRotate = 64f;
    private float speedRotateLerp = 8f;
    
    private float zoom = 0f;
    
    private boolean activeRotation = false;
    
    private Node centerViewNodeCamera;
    private Vector3f  posNew;
    
    private Node nodeCamera;
    
    private float rotateValue = 0f; 
    private float rotateChoose = 0f;
    
    private Vector3f _offsetVoxel;
    
    private GuiAppState guiAppState;
    
    // Node relatif au cursor de rotation
    Node nodeCursorRotation;

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
        this.app = (SimpleApplication)app;
        this.cam = app.getCamera();
        this.input = app.getInputManager();
        
        // réception des appstate
        voxel = stateManager.getState(VoxelAppState.class);
        guiAppState = stateManager.getState(GuiAppState.class);
        
        results = new CollisionResults();
        
        nodeCamera = new Node();
        centerViewNodeCamera = new Node();
        centerViewNodeCamera.setLocalTranslation(-32,32,32);
        posNew = centerViewNodeCamera.getWorldTranslation();
        
        centerViewNodeCamera.attachChild(nodeCamera);
        nodeCamera.setLocalTranslation(0f,16f,-16f);
        
        this.app.getRootNode().attachChild(centerViewNodeCamera);
        
        // désactivation du curseur
        input.setCursorVisible(true);
        input.addListener(this, "MOUSE_CLICK_RIGHT");
        input.addListener(this, "MOUSE_ROTATE_RIGHT");
        input.addListener(this, "MOUSE_ROTATE_LEFT");
        
        input.addMapping("MOUSE_ROTATE_RIGHT", new MouseAxisTrigger(MouseInput.AXIS_X,true));
        input.addMapping("MOUSE_ROTATE_LEFT", new MouseAxisTrigger(MouseInput.AXIS_X,false));
        input.addMapping("MOUSE_CLICK_RIGHT", new MouseButtonTrigger(MouseInput.BUTTON_RIGHT));
        
        // centre screen
        centerScreen = new Vector2f(cam.getWidth()/2f,cam.getHeight()/2);
        // offset
         _offsetVoxel = new Vector3f(0.5f,0.5f,0.5f);
        // init key
        initKeys();
        // init mark
        initMark();
      
    } 
    
    private void initMark(){
    
        nodeCursorRotation = new Node();
        Spatial select = app.getAssetManager().loadModel("Models/Utils/Select/select.j3o");
        select.setLocalTranslation(0,0.05f,0);
        nodeCursorRotation.attachChild(select);
        nodeCursorRotation.setShadowMode(RenderQueue.ShadowMode.Receive);
        nodeCursorRotation.setCullHint(Spatial.CullHint.Always);
        app.getRootNode().attachChild(nodeCursorRotation);
       
     }
    
     private void initKeys(){
        // input du bouton centrale de la souris
        input.addMapping("ZOOM_CAMERA_IN", new MouseAxisTrigger(MouseInput.AXIS_WHEEL,false));
        input.addMapping("ZOOM_CAMERA_OUT", new MouseAxisTrigger(MouseInput.AXIS_WHEEL,true));
        
 
        input.addMapping("CLIC_LEFT",
          new KeyTrigger(KeyInput.KEY_SPACE),
          new MouseButtonTrigger(MouseInput.BUTTON_LEFT)); 
        
              
         input.addMapping("START_PATH", 
                new KeyTrigger(KeyInput.KEY_1));
         
         input.addMapping("GOAL_PATH", 
                new KeyTrigger(KeyInput.KEY_2));
         
          input.addMapping("BON", 
                new KeyTrigger(KeyInput.KEY_B));
        
        input.addListener(this, "ZOOM_CAMERA_IN");
        input.addListener(this, "ZOOM_CAMERA_OUT");
        input.addListener(this, "CLIC_LEFT");
      //  inputManager.addListener(this, "SUB_VOXEL");
        input.addListener(this, "START_PATH");
        input.addListener(this, "GOAL_PATH");
        input.addListener(this, "BON");
     }

    public float getZoom() {
        return zoom;
    }

    public void setZoom(float zoom) {
        this.zoom = zoom;
    }


    public Camera getCam() {
        return cam;
    }

    public boolean isActiveRotation() {
        return activeRotation;
    }

    public void setActiveRotation(boolean activeRotation) {
        this.activeRotation = activeRotation;
    }

    
    public Vector3f getDirectionCursor(){
       
        Vector3f click3d = cam.getWorldCoordinates(input.getCursorPosition(), 0f).clone();
        return cam.getWorldCoordinates(input.getCursorPosition(), 1f).subtractLocal(click3d).normalizeLocal();
        
    }
    
    @Override
    public void update(float tpf) {
       Vector2f posCursor = input.getCursorPosition();
       float distance = posCursor.distance(centerScreen);
       Vector2f diff = (centerScreen.subtract(posCursor)).normalize();
    
      // creation du vecteur NodeCam to CenterNodeCam
       Vector3f vCamDir = cam.getDirection();
      // produit scalaire pour obtenir le vecteur right
       Vector3f right = Vector3f.UNIT_Y.cross(vCamDir);
       right.normalizeLocal().multLocal(speed * tpf);

      // produit scalaire pour obtenir le vecteur en avant
       Vector3f dir = Vector3f.UNIT_Y.cross(right);
       dir.normalizeLocal().multLocal(speed * tpf);
          
          
          // calcul de la hauteur de la caméra
          results.clear();
          Vector3f posH = centerViewNodeCamera.getWorldTranslation();
          Ray r = new Ray(nodeCamera.getWorldTranslation(),cam.getDirection());
          voxel.getNodeVoxelChunk().collideWith(r, results);
          if(results.size() > 0){
              Vector3f vClosest = results.getClosestCollision().getContactPoint();
              Vector3f end = posH.interpolateLocal(vClosest, tpf * speedRotateLerp);
              posH.y = end.y;
          }
          
           // affichage du cursor de rotation
       if(activeRotation){
           nodeCursorRotation.setLocalTranslation(centerViewNodeCamera.getWorldTranslation());
       }else{
            // translation
          if(posCursor.x > cam.getWidth() - 12)
              posNew.addLocal(right.negate());
          else  if(posCursor.x < 12)
              posNew.addLocal(right);
          else if(posCursor.y < 12)
              posNew.addLocal(dir);
          else if(posCursor.y > cam.getHeight() - 12)
              posNew.addLocal(dir.negate());

          Vector3f v = centerViewNodeCamera.getWorldTranslation();
          v.interpolateLocal(posNew, tpf);
          centerViewNodeCamera.setLocalTranslation(v);
       }
           
        // rotation
        Quaternion q = centerViewNodeCamera.getWorldRotation().clone();
        Quaternion nQ = new Quaternion();
        nQ.fromAngleAxis(rotateChoose, Vector3f.UNIT_Y);
        q.slerp(nQ, tpf * speedRotateLerp);
        centerViewNodeCamera.setLocalRotation(q);
        
        // zoom
        if(zoom != 0){
            if(zoom > 0){
                nodeCamera.setLocalTranslation(nodeCamera.getLocalTranslation().add(Vector3f.UNIT_Y.negate()));
            }else if(zoom < 0){
                nodeCamera.setLocalTranslation(nodeCamera.getLocalTranslation().add(Vector3f.UNIT_Y));
            }
        }
        zoom = 0f;
            

       // la camera doit suivre le nodeCamera
       this.cam.setLocation(nodeCamera.getWorldTranslation());
       this.cam.lookAt(centerViewNodeCamera.getWorldTranslation(), Vector3f.UNIT_Y);
       
    }
    
    private float lerp(float a, float b, float f) 
    {
        return a + f * (b - a);
    }
    
    @Override
    public void cleanup() {
        super.cleanup();
        //TODO: clean up what you initialized in the initialize method,
        //e.g. remove all spatials from rootNode
        //this is called on the OpenGL thread after the AppState has been detached
    }

    @Override
    public void onAnalog(String name, float f, float f1) {
       
        if(activeRotation){
        
            if(name.equals("MOUSE_ROTATE_LEFT")){
                rotateChoose+=f*f1 * speedRotate;

            }else if(name.equals("MOUSE_ROTATE_RIGHT")){
                rotateChoose-=f * f1 * speedRotate;

            }
        }
        
        // gestion ZOOM
        if(name.equals("ZOOM_CAMERA_IN")){
            zoom += f;
        }
        else if(name.equals("ZOOM_CAMERA_OUT")){
            zoom -= f;
        }
        
        
    }

    @Override
    public void onAction(String name, boolean isPressed, float f) {
       
        if(name.equals("MOUSE_CLICK_RIGHT")){
            activeRotation = isPressed;
            if(activeRotation)
                 nodeCursorRotation.setCullHint(Spatial.CullHint.Never);
            else
                 nodeCursorRotation.setCullHint(Spatial.CullHint.Always);
        }
        
         if(isPressed)
        {
                // clear du results
                results.clear();
                // creation du rayon
                Ray r = new Ray(cam.getLocation(),getDirectionCursor());
                voxel.getNodeVoxelChunk().collideWith(r, results);

                if(results.size() > 0)
                {
                    // il y a une collision
                    CollisionResult closest = results.getClosestCollision();
                    
                    if(name.equals("CLIC_LEFT")){
                    
                        if(SingleGlobal.getInstance().getGameMode() == SingleGlobal.Mode.BUILD){
                        
                            if(guiAppState.getTypeAction() == GuiAppState.typeButtonAction.ADD)
                            {
                                // on récupère lecontact point
                                Vector3f voxelPos = closest.getContactPoint().add(closest.getContactNormal().divide(2));
                                voxel.addVoxelToGrid(voxelPos.add(_offsetVoxel)); // offsetVoxel est égale au 0.5f de décallage
                            }
                            else if(guiAppState.getTypeAction() == GuiAppState.typeButtonAction.SUB)
                            {
                                // position du voxel a supprimer
                                  Vector3f voxelPos = closest.getContactPoint().subtract(closest.getContactNormal().divide(2));
                                 voxel.subVoxelToGrid(voxelPos.add(_offsetVoxel)); // offsetVoxel est égale au 0.5f de décallage
                            }
                             else if(guiAppState.getTypeAction() == GuiAppState.typeButtonAction.ADDBON)
                            {

                            }
                        }
                        
                    }
                    
                     /* if(name.equals("START_PATH")){
                          Vector3f voxelPos = closest.getContactPoint().add(closest.getContactNormal().divide(2));
                          _finderAppState.setStartPoint(voxelPos);
                      }
                      
                      if(name.equals("GOAL_PATH")){
                          Vector3f voxelPos = closest.getContactPoint().add(closest.getContactNormal().divide(2));
                          _finderAppState.setGoalPoint(voxelPos);
                      }*/
                      
                      
                        
                }
 
        }
        
        
    }

   
    
}
