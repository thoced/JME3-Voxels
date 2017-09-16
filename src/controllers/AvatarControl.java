/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import appState.EntityAppState;
import com.jme3.animation.AnimChannel;
import com.jme3.animation.AnimControl;
import com.jme3.animation.AnimEventListener;
import com.jme3.animation.LoopMode;
import com.jme3.export.InputCapsule;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.OutputCapsule;
import com.jme3.material.RenderState;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import com.jme3.scene.control.Control;
import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;
import mygame.SingleModelAsset;
import mygame.VoxelAppState;
import pathfinding.AStarPathFinder;
import pathfinding.Path;
import pathfinding.Path.Step;
import pathfinding.heuristics.ClosestSquaredHeuristic;

/**
 *
 * @author thonon
 */
public class AvatarControl extends AbstractControl implements AnimEventListener {
    //Any local variables should be encapsulated by getters/setters so they
    //appear in the SDK properties window and can be edited.
    //Right-click a local variable to encapsulate it with getters and setters.

    private EntityAppState m_entityAppState;
    
    private VoxelAppState m_voxelAppState;
    
    private float speed = 16f;
    
    private AnimControl m_animControl;
    
    private AnimChannel m_animChanel;
    
    private Vector3f m_posDestination;
    
    private boolean isSelected = false;
    
    private Spatial m_selectedSpatial;
    
    private Path m_path = null;
    
    private int  m_indPath = 0;
    
    private boolean m_isStepDone = true;
    
    private Vector3f m_stepPosition;
    
    private Quaternion m_quaterPosition = new Quaternion();
    
 
    public AvatarControl(EntityAppState entityAppState,VoxelAppState voxelAppState) {
        this.m_entityAppState = entityAppState;
        this.m_voxelAppState = voxelAppState;
        
       
    }

    
    
    public boolean isIsSelected() {
        return isSelected;
    }

    public void setIsSelected(boolean isSelected) {
        this.isSelected = isSelected;
        if(this.isSelected){
            m_selectedSpatial.setCullHint(Spatial.CullHint.Never);
        }else{
            m_selectedSpatial.setCullHint(Spatial.CullHint.Always);
        }
         
    }

    public Path getPath() {
        return m_path;
    }

    public void setPath(Path path) {
        this.m_path = path;
        this.m_indPath=0;
        this.m_isStepDone = true;
    }

    
    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    @Override
    public void setSpatial(Spatial spatial) {
        super.setSpatial(spatial); //To change body of generated methods, choose Tools | Templates.
        
        // creation des animations
        m_animControl = this.spatial.getControl(AnimControl.class);
        if(m_animControl != null){
            m_animChanel = m_animControl.createChannel();
            m_animControl.addListener(this);
            m_animChanel.setLoopMode(LoopMode.Loop);
            m_animChanel.setSpeed(1f);
            m_animChanel.setAnim("headspin");
        }
        
        // réception du modele de sélection
        m_selectedSpatial = SingleModelAsset.getInstance().getListAsset().get("select_entity").clone();
        m_selectedSpatial.setCullHint(Spatial.CullHint.Always);
        m_selectedSpatial.setShadowMode(RenderQueue.ShadowMode.Off);
        // attachement en position relative
        ((Node)this.spatial).attachChild(m_selectedSpatial);
        
       
    }

    public Vector3f getposDestination() {
        return m_posDestination;
    }

    public void setposDestination(Vector3f m_posDestination) {
        this.m_posDestination = m_posDestination;
       
    }
    
    

    @Override
    protected void controlUpdate(float tpf) {
         
        if(m_path != null && m_isStepDone){
            // on récupère le step suivant
            if(m_path.getLength() > m_indPath + 1){
                Step step  = m_path.getStep(m_indPath);
                m_stepPosition = new Vector3f(step.getX(),step.getY()-0.5f,step.getZ());
                m_isStepDone = false;
                m_indPath++;
            }else{
                m_path = null;
                m_isStepDone = true;
                m_indPath = 0;
            }
            
        }else if(!m_isStepDone){
            // si on est pas encore arrivé à la prochaine étape
            // on doit déplacer l'avatar
                       
            Vector3f dir = m_stepPosition.subtract(this.getSpatial().getLocalTranslation());
            dir.normalizeLocal();
            Vector3f p = this.getSpatial().getLocalTranslation();
            p.addLocal(dir.mult(tpf * 2));
             this.getSpatial().setLocalTranslation(p);
            
         
            if(this.getSpatial().getLocalTranslation().distance(m_stepPosition) < 0.1f)
                m_isStepDone = true;
            
            // rotation de l'avatar pour qu'il soit dirigé vers le sens de marche
            dir.y = 0f;
            m_quaterPosition.lookAt(dir, Vector3f.UNIT_Y);
            this.getSpatial().getLocalRotation().nlerp(m_quaterPosition, tpf* speed);
           
            
        }
        
        

    }
    
    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
        //Only needed for rendering-related operations,
        //not called when spatial is culled.
    }
    
    public Control cloneForSpatial(Spatial spatial) {
        AvatarControl control = new AvatarControl(m_entityAppState,m_voxelAppState);
        control.setSpeed(speed);
        return control;
    }
    
    @Override
    public void read(JmeImporter im) throws IOException {
        super.read(im);
        InputCapsule in = im.getCapsule(this);
     
    }
    
    @Override
    public void write(JmeExporter ex) throws IOException {
        super.write(ex);
        OutputCapsule out = ex.getCapsule(this);
       
    }

    @Override
    public void onAnimCycleDone(AnimControl control, AnimChannel channel, String animName) {
      
    }

    @Override
    public void onAnimChange(AnimControl control, AnimChannel channel, String animName) {
       
    }

   
    
}
