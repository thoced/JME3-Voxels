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
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Sphere;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import mygame.MapLoader;
import mygame.VoxelAppState;
import pathfinding.AStarPathFinder;
import pathfinding.Path;
import pathfinding.heuristics.ClosestSquaredHeuristic;


/**
 *
 * @author thonon
 */
public class FinderAppState extends AbstractAppState implements Callable<Path> {
    
    private AStarPathFinder m_finder;
    
    private Path path;
    
    private int sx, sy, sz;
    
    private int  gx, gy, gz;
    
    private MapLoader map;
    
    private SimpleApplication sApp;
    
    // Thraed
    private  ScheduledThreadPoolExecutor executor;
    private Future future;
    
    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
        //TODO: initialize your AppState, e.g. attach spatials to rootNode
        //this is called on the OpenGL thread after the AppState has been attached
        
        VoxelAppState voxelAppState = stateManager.getState(VoxelAppState.class);
        map = voxelAppState.getMap();
        
        m_finder = new AStarPathFinder(voxelAppState.getMap(),65535*4,true,new ClosestSquaredHeuristic());
        
        sApp = (SimpleApplication)app;
        
        // Thread
        executor = new ScheduledThreadPoolExecutor(4);
      
       
    }
    
    @Override
    public void update(float tpf) {
       
       
        
    }
    
    @Override
    public void cleanup() {
        super.cleanup();
        //TODO: clean up what you initialized in the initialize method,
        //e.g. remove all spatials from rootNode
        //this is called on the OpenGL thread after the AppState has been detached
    }

   
    public void setStartPoint(Vector3f startPoint){
        
        sx = (int)startPoint.x;
        sy = (int)startPoint.y;
        sz = (int)startPoint.z;
        
        System.out.print(" source X: " + sx );
        System.out.print(" source Y: " + sy );
        System.out.println(" source Z: " + sz );
        
     }


    public void setGoalPoint(Vector3f goalPoint) {
       
        gx = (int)goalPoint.x;
        gy = (int)goalPoint.y;
        gz = (int)goalPoint.z;
        
        System.out.print(" goal X: " + gx );
        System.out.print(" goal Y: " + gy );
        System.out.println(" goal Z: " + gz );
        
        try
        {
        System.out.println("Lancement de la recherche...");
        path = m_finder.findPath(null, sx, sy, sz, gx, gy, gz); // inversion des Y et Z
        System.out.println("Fin de la recherche...");
        if(path != null)
             System.out.println("Un chemin est trouvé...");
        
        }
        catch(java.lang.ArrayIndexOutOfBoundsException a){
             System.out.println("ArrayIndexOutOfBoundsException");
        }
        
    }

    @Override
    public Path call() throws Exception {
              
        return null;
    }
    
   
    
}


