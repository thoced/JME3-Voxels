/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package appState;

import ModelsData.SingleGlobal;
import com.jme3.animation.AnimChannel;
import com.jme3.animation.AnimControl;
import com.jme3.animation.LoopMode;
import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.input.InputManager;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.light.Light;
import com.jme3.light.PointLight;
import com.jme3.light.SpotLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Ray;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import controllers.AvatarControl;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import mygame.SingleModelAsset;
import mygame.VoxelAppState;

/**
 *
 * @author thonon
 */
public class EntityAppState extends AbstractAppState implements ActionListener{
    
    // App
    private SimpleApplication m_app;
    // input manager
    private InputManager m_input;
    // CameraAppState
    private CameraScrollAppState m_cam;
    // voxelAppState
    private VoxelAppState m_voxel;
    // RootNodeEntity
    private Node m_rootNodeEntity;
    // liste des entités sélectionnées
    private Collection<Spatial> m_listEntitySelected = new ArrayList<Spatial>();
   
    
    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
        // copie
        m_app = (SimpleApplication)app;
        m_input = app.getInputManager();
        // AppState
        m_voxel = stateManager.getState(VoxelAppState.class);
        m_cam = stateManager.getState(CameraScrollAppState.class);
        //  input
        m_input.addListener(this, "ADD_ENTITY");
        m_input.addMapping("ADD_ENTITY", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
        // instance de RootNodeEntity
        m_rootNodeEntity = new Node();
        m_app.getRootNode().attachChild(m_rootNodeEntity);
        
        
      
       
    }
    
    @Override
    public void update(float tpf) {
     
    }
    
    @Override
    public void cleanup() {
        super.cleanup();
      
    }
    
    public void AddNewEntity(){
        
    }
    
    public void MoveEntityTo(){
        
    }
    
     

    @Override
    public void onAction(String name, boolean isPressed, float tpf) {
       
        if(isPressed){
            
                if(SingleGlobal.getInstance().getGameMode() == SingleGlobal.Mode.ENTITY){

                    if(name.equals("ADD_ENTITY")){
                        // ajout d'une entitée

                        if(SingleGlobal.getInstance().getNameEntityToBePlaced().equals("CAMPFIRE")){
                           // ajout du campfire
                           Vector3f dir = m_cam.getDirectionCursor();
                           Vector3f contact = m_voxel.getCenterBlocSinceDirection(m_cam.getCam().getLocation(), dir);
                           // creation du camp
                           Spatial camp = m_app.getAssetManager().loadModel("Models/campfire/campfire.j3o");
                           camp.setLocalTranslation(contact);
                           m_rootNodeEntity.attachChild(camp);
                           // Light
                           PointLight sl = new PointLight();
                           sl.setPosition(contact.add(new Vector3f(0f,0.1f,0f)));
                           sl.setColor(ColorRGBA.Red);
                           sl.setRadius(3f);
                           m_app.getRootNode().addLight(sl);

                        }else  if(SingleGlobal.getInstance().getNameEntityToBePlaced().equals("PLAYER")){
                            // ajout du player
                           Vector3f dir = m_cam.getDirectionCursor();
                           Vector3f contact = m_voxel.getCenterBlocSinceDirection(m_cam.getCam().getLocation(), dir);
                           // creation de l'avatar
                           Spatial avatar = SingleModelAsset.getInstance().getListAsset().get("avatar").clone();
                           AvatarControl ac = new AvatarControl();
                           avatar.addControl(ac);
                           avatar.setShadowMode(RenderQueue.ShadowMode.CastAndReceive);
                           avatar.setLocalTranslation(contact);
                           m_rootNodeEntity.attachChild(avatar);
                        }

                    }
                }else if(SingleGlobal.getInstance().getGameMode() == SingleGlobal.Mode.SELECT){
                    // lancement d'un rayon pour déterminer si une entiteé est sélectionnée
                    Ray r = new Ray(m_cam.getCam().getLocation(),m_cam.getDirectionCursor());
                    CollisionResults results = new CollisionResults();
                    m_rootNodeEntity.collideWith(r, results);
                    if(results.size() > 0){
                        CollisionResult collide = results.getClosestCollision();
                        Geometry geoCollide = collide.getGeometry();
                        AvatarControl ac = geoCollide.getParent().getControl(AvatarControl.class);
                        if(ac != null){
                           m_listEntitySelected.add(geoCollide.getParent());
                           ac.setIsSelected(true);
                        }
                    }else{
                            // aucun objet n'est touché par le rayon, déselection de tous les objets déja sélectionné
                            for(Spatial sp : m_listEntitySelected){
                                AvatarControl avatarControl = sp.getControl(AvatarControl.class);
                                if(avatarControl != null){
                                    avatarControl.setIsSelected(false);
                                }
                            }
                            m_listEntitySelected.clear();
                        }
                    
                    
                }
        }
        
        
    }
    
    
}
