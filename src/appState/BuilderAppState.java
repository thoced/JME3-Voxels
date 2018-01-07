/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package appState;

import appState.CameraScrollAppState;
import ModelsData.SingleGlobal;
import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.material.Material;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.texture.Texture;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import jme3tools.optimize.GeometryBatchFactory;
import mygame.VoxelAppState;

/**
 *
 * @author thonon
 */
public class BuilderAppState extends AbstractAppState implements ActionListener{
    
    private SimpleApplication m_app;
    private VoxelAppState m_voxel;
    private CameraScrollAppState m_cam;
    
    private Vector3f m_posPatroCubeStart;
    private Vector3f m_posPatroCubeEnd;
    private boolean  m_patroEnable = false;
    
    private Spatial m_patroSpatial;
    private Collection<Geometry> m_listGeometryPatro = new ArrayList<Geometry>();
    private Material m_mat;
    
    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
      
        m_app = (SimpleApplication)app;
        // appstate
        m_voxel = stateManager.getState(VoxelAppState.class);
        m_cam = stateManager.getState(CameraScrollAppState.class);
        
        // input
        app.getInputManager().addListener(this, "CLIC_LEFT");
        app.getInputManager().addMapping("CLIC_LEFT", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
        
        // chargement du cube patro
        m_patroSpatial = app.getAssetManager().loadModel("Models/Utils/Patro/cube_patro.j3o");
        //m_app.getRootNode().attachChild(m_patroSpatial);
         m_mat = new Material(m_app.getAssetManager(),"Common/MatDefs/Misc/Unshaded.j3md");
         Texture text = m_app.getAssetManager().loadTexture("Models/Utils/Patro/diffuse_patro.png");
         m_mat.setTexture("ColorMap", text);
        
    }
    
    @Override
    public void update(float tpf) {
       
        if(m_patroEnable){
            // on récupère le cube sélectionné
            m_posPatroCubeEnd = m_voxel.getCenterBlocSinceDirection(m_cam.getCam().getLocation(), m_cam.getDirectionCursor());
            Vector3f diffPatro = m_posPatroCubeEnd.subtract(m_posPatroCubeStart);
            
            // création du mesh Patro
            for(int i=0;i<2;i++){
                Spatial s = m_patroSpatial.clone();
                s.move(new Vector3f(1,0,0));
                    Geometry g = (Geometry) ((Node)s).getChild("geo");
                    m_listGeometryPatro.add(g);
            }
            Mesh outMesh = new Mesh();
            GeometryBatchFactory.mergeGeometries(m_listGeometryPatro, outMesh);
            
            Geometry patroG = new Geometry("patro",outMesh);
            patroG.setMaterial(m_mat);
            patroG.setLocalTranslation(m_posPatroCubeStart);
            
            
            m_app.getRootNode().attachChild(patroG);
        }
    }
    
    @Override
    public void cleanup() {
        super.cleanup();
        //TODO: clean up what you initialized in the initialize method,
        //e.g. remove all spatials from rootNode
        //this is called on the OpenGL thread after the AppState has been detached
    }

    @Override
    public void onAction(String name, boolean isPressed, float tpf) {
       
        if(isPressed){
            
              if(SingleGlobal.getInstance().getGameMode() == SingleGlobal.Mode.EARTHWORK){
                    
                  m_posPatroCubeStart = m_voxel.getCenterBlocSinceDirection(m_cam.getCam().getLocation(), m_cam.getDirectionCursor());
                  m_patroSpatial.setLocalTranslation(m_posPatroCubeStart);
                  m_patroEnable = true;
              }
        }else{
            m_patroEnable = false;
        }
        
        
       
    }
    
}
