/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package appState;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.scene.Geometry;
import mygame.SingleModelAsset;

/**
 *
 * @author thonon
 */
public class AssetLoaderAppState extends AbstractAppState {
    
    private SingleModelAsset modelAsset;
   
    
    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
      
        modelAsset = SingleModelAsset.getInstance();
        // chargement des tree
        modelAsset.getListAsset().put("tree01", app.getAssetManager().loadModel("Models/Trees/Tree01/tree01.j3o"));
       
        // désactivation de la boucle update
        this.setEnabled(false);
    }
    
    @Override
    public void update(float tpf) {
        //TODO: implement behavior during runtime
    }
    
    @Override
    public void cleanup() {
        super.cleanup();
        //TODO: clean up what you initialized in the initialize method,
        //e.g. remove all spatials from rootNode
        //this is called on the OpenGL thread after the AppState has been detached
    }
    
}
