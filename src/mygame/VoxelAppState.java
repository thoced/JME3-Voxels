/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Spatial;
import java.util.ArrayList;
import java.util.Collection;

/**
 *
 * @author Thonon
 */
public class VoxelAppState extends AbstractAppState {

    private Application _app;
    
    private MapLoader _map;
    
    //private Collection<Chunk> _listChunks;
    
    private Chunk[] _gridChunk;
    
    @Override
    public void initialize(AppStateManager stateManager, Application app)
    {
        super.initialize(stateManager, app);
        
        _app = app;
        // chargement de la map
        _map = new MapLoader("Textures/map01/map07.png",app.getAssetManager());
                 
        // creation des chunks
       // _listChunks = new ArrayList<Chunk>();
        _gridChunk = new Chunk[_map.getWidthMap() * _map.getHeightMap()];
        this.manageChunk();
               
         
    }
    
    private void manageChunk()
    {
         // Création d'un material
        Material mat = new Material(_app.getAssetManager(),"Common/MatDefs/Light/Lighting.j3md");
        mat.setColor("Diffuse", new ColorRGBA(128,128,128,255));
        mat.setTexture("DiffuseMap",
        _app.getAssetManager().loadTexture("Textures/Textures/rock.jpg"));
        mat.setTexture("NormalMap", 
                _app.getAssetManager().loadTexture("Textures/Textures/rock_n.jpg"));
        mat.setFloat("Shininess", 64f);  // [0,128]
        mat.getAdditionalRenderState().setFaceCullMode(RenderState.FaceCullMode.Back);
       
      int lx=0,ly=0;
      for(int y=0;y<_map.getHeightMap();y+=16)
      { 
          for(int x=0;x<_map.getWidthMap();x+=16)
          {
              // System.out.println(_heightMap[y][x]);
              Chunk c = new Chunk(new Vector2f(x,y),_map);
              //_listChunks.add(c);
              // ajout dans un grid pour pouvoir y accéder plus rapidement
              _gridChunk[(ly * _map.getHeightMap()) + lx] = c;
              // ajout dans le scene graph
              // pour chaque chunk, on récupère le mesh
              Mesh m = c.getMeshChunk();
              // pour chaque mesh on créer une geometrie
              Geometry geo = new Geometry(c.getNameChunk(),m);
              geo.setMaterial(mat);
              ((SimpleApplication)_app).getRootNode().attachChild(geo);
              
              lx++;
             
          }
          ly++;
          lx = 0;
          
          
          
      }
    }
    
    public void addVoxelToGrid(Vector3f p)
    {
        // ajout du voxel dans la grid3d generale
        _map.getGridMap3d()[((int)p.y*_map.getzWidth()) + ((int)p.z * _map.getHeightMap()) + (int) p.x] = 1;
        // on détermine le chunk qui correspond à la modification pour réinitialiser le mesh
        Vector2f chunkPos = new Vector2f((int)p.x / 16,(int)p.z / 16);
        // appel à la methode update du chunk
        if(_gridChunk[((int)chunkPos.y * _map.getHeightMap()) +  (int)chunkPos.x] != null)
        {
            _gridChunk[((int)chunkPos.y * _map.getHeightMap()) +  (int)chunkPos.x].updateMeshChunk();
            
            // modification du node
            String nameChunkSearch = "[" + (int)chunkPos.x + "][" + (int)chunkPos.y + "]";
            Spatial s = ((SimpleApplication)_app).getRootNode().getChild(nameChunkSearch);
            if(s != null)
                ((Geometry)s).setMesh(_gridChunk[((int)chunkPos.y * _map.getHeightMap()) +  (int)chunkPos.x].getMeshChunk());
           
        }
        
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
