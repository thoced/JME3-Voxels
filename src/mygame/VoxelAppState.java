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
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Sphere;
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
    
    // root node des Voxels Chunk
    private Node _nodeVoxelChunks;
    // root node des LightProbes
    private Node _nodeLightProbes;
    
    private Material _mat;
    
    @Override
    public void initialize(AppStateManager stateManager, Application app)
    {
        super.initialize(stateManager, app);
        
        _app = app;
        // chargement de la map
        _map = new MapLoader("Textures/map01/map09.png",app.getAssetManager()); 
        // creation des chunks
       // _listChunks = new ArrayList<Chunk>();
        _gridChunk = new Chunk[(_map.getWidthMap()/16) * (_map.getHeightMap()/16)];
        this.manageChunk();
               
         
    }
    
    private void manageChunk()
    {
         // Création d'un material
        _mat = new Material(_app.getAssetManager(),"Common/MatDefs/Light/Lighting.j3md");

        _mat.setBoolean("UseVertexColor",true);
        _mat.setColor("Diffuse", new ColorRGBA(0.2f,0.2f,0.2f,1));
        
        _mat.setTexture("DiffuseMap",
        _app.getAssetManager().loadTexture("Textures/Textures/rock.jpg"));
        _mat.setTexture("NormalMap", 
                _app.getAssetManager().loadTexture("Textures/Textures/rock_n.jpg"));
        _mat.setBoolean("UseMaterialColors", false);
        _mat.setBoolean("VertexLighting", true);
        _mat.setFloat("Shininess", 64f);  // [0,128]
        _mat.getAdditionalRenderState().setFaceCullMode(RenderState.FaceCullMode.Back);
       
        // creation du nodeVoxelChunk
        _nodeVoxelChunks = new Node();
        _nodeVoxelChunks.setName("NODE_VOXEL_CHUNK");
        
        
      int lx=0,ly=0;
      for(int y=0;y<_map.getHeightMap();y+=16)
      { 
          for(int x=0;x<_map.getWidthMap();x+=16)
          {
              // System.out.println(_heightMap[y][x]);
              Chunk c = new Chunk(new Vector2f(x,y),_map);
              //_listChunks.add(c);
              // ajout dans un grid pour pouvoir y accéder plus rapidement
              _gridChunk[(ly * (_map.getHeightMap() / 16)) + lx] = c;
              // ajout dans le scene graph
              // ajout du node
              addNode(c,c.getNameChunk());  
              lx++;
             
          }
          ly++;
          lx = 0;
          
      }
       // attache du nodeVoxelChunks dans le rootNode 
       ((SimpleApplication)_app).getRootNode().attachChild(_nodeVoxelChunks);
    }
    
    private void addNode(Chunk chunk,String name)
    {
         // pour chaque mesh on créer une geometrie
         Geometry geo = new Geometry(name,chunk.getMeshChunk());
         geo.setMaterial(_mat);

         // ajout de la reference du chunk dans le user data
         geo.setUserData("CHUNK",chunk);
         
         // on specifie que le chunk peut emettre une shadow et en recevoir
         geo.setShadowMode(RenderQueue.ShadowMode.CastAndReceive);
         

         // ajout dans le node délégué au Chunk
         _nodeVoxelChunks.attachChild(geo);
    }
    
   
    
    public void addVoxelToGrid(Vector3f p)
    {
        // ajout du voxel dans la grid3d generale
        _map.getGridMap3d()[((int)p.y*_map.getzWidth()) + ((int)p.z * _map.getHeightMap()) + (int) p.x] |= 0x01000000;
        // on détermine le chunk qui correspond à la modification pour réinitialiser le mesh
        Vector2f chunkPos = new Vector2f((int)p.x / 16,(int)p.z / 16);
        // appel à la methode update du chunk
        if(_gridChunk[((int)chunkPos.y * (_map.getHeightMap()) / 16) +  (int)chunkPos.x] != null)
        {
            // update du chunk (remesh)
            //_gridChunk[((int)chunkPos.y * (_map.getHeightMap()) / 16) +  (int)chunkPos.x].updateMeshChunk();
            //detachement du node
            String nameChunkSearch = "[" + (int)chunkPos.x + "][" + (int)chunkPos.y + "]";
            _nodeVoxelChunks.detachChildNamed(nameChunkSearch);
            // ajout du nouveau node avec le nouveau mesh
            this.addNode(_gridChunk[((int)chunkPos.y * (_map.getHeightMap() / 16)) +  (int)chunkPos.x], nameChunkSearch);

        }
        // remesh
        this.remesh((int)chunkPos.x, (int)chunkPos.y);
             
        
    }
    
    public void subVoxelToGrid(Vector3f p)
    {
        // suppresion du voxel dans la grid3d generale
        _map.getGridMap3d()[((int)p.y*_map.getzWidth()) + ((int)p.z * _map.getHeightMap()) + (int) p.x] &= 0x00ffffff;
         // on détermine le chunk qui correspond à la modification pour réinitialiser le mesh
        Vector2f chunkPos = new Vector2f((int)p.x / 16,(int)p.z / 16);
        // appel à la methode update du chunk ainsi que les 8 autres chunk
        for(int dy=-1;dy<2;dy++)
        {
            for(int dx=-1;dx<2;dx++)
            {
                try
                {
                     if(_gridChunk[((int)chunkPos.y * (_map.getHeightMap()) / 16) +  (int)chunkPos.x] != null)
                    {
                        // update du chunk (remesh) ainsi que les 8 autres chunk 
                       // _gridChunk[((int)chunkPos.y * (_map.getHeightMap()) / 16) +  (int)chunkPos.x].updateMeshChunk();
                        //detachement du node
                        String nameChunkSearch = "[" + (int)chunkPos.x + dx + "][" + (int)chunkPos.y + dy + "]";
                        _nodeVoxelChunks.detachChildNamed(nameChunkSearch);
                        // ajout du nouveau node avec le nouveau mesh
                        this.addNode(_gridChunk[((int)chunkPos.y * (_map.getHeightMap() / 16)) +  (int)chunkPos.x], nameChunkSearch);

                    }
                }
                catch(ArrayIndexOutOfBoundsException a)
                {
                    
                }
            }
        }
        // remesh
        this.remesh((int)chunkPos.x, (int)chunkPos.y);
    }
    
    // reset, illumination et remesh
    public void remesh(int x,int y)
    {
              //reset lightFactor du chunk
        
        for(int dy = y - 1; dy <= y + 1;dy++ )
        {
            for(int dx = x - 1;dx <= x + 1;dx++)
            {
                try
                {
                    _gridChunk[(dy * (_map.getHeightMap() / 16)) + dx].resetLightFactor();
    
                }
                  catch(ArrayIndexOutOfBoundsException a)
                  {
                      
                  }
                
            }
        }
        
        // illumination (calcul de l'illumination sur 5 case adjacentes)
        
          for(int dy = y - 2; dy <= y + 2;dy++ )
        {
            for(int dx = x - 2;dx <= x + 2;dx++)
            {
                try
                {

                    _gridChunk[(dy * (_map.getHeightMap() / 16)) + dx].illumination();

                }
                  catch(ArrayIndexOutOfBoundsException a)
                  {
                      
                  }
                
            }
        }
          
          // remech
          
            for(int dy = y - 1; dy <= y + 1;dy++ )
        {
            for(int dx = x - 1; dx <= x + 1;dx++)
            {
                try
                {
                   
                    _gridChunk[(dy * (_map.getHeightMap() / 16)) + dx].updateMeshChunk();
                     
                    
                }
                  catch(ArrayIndexOutOfBoundsException a)
                  {
                      
                  }
                
            }
        } 
    }
    
    public void addLightProbe(Vector3f p)
    {
        // calcul du chunk qui va recevoir le lightprobe
        int x = (int)(p.x / 16);
        int y = (int)(p.z / 16);
        // instance d'un nouveau lightprobe
        LightProbe probe = new LightProbe(p,5f);
        // reception du node contenu le chunk avec les indices x et y
        Spatial sc = _nodeVoxelChunks.getChild("[" + x + "][" + y + "]");
        System.out.println("[" + x + "][" + y + "]");
        // récupération de l'objet chunk
        Chunk cc =  _gridChunk[(y * (_map.getHeightMap())/16) + x];
        // ajout du probe dans le chunk
        cc.addLightProbe(probe);
        // update du 9 chunks pour la reconstruction du mesh et de l'illumination
        
        // remesh
        this.remesh(x,y);
        
        //detachement du node
        String nameChunkSearch = "[" + (int)x + "][" + (int)y + "]";
        _nodeVoxelChunks.detachChildNamed(nameChunkSearch);
        // ajout du nouveau node avec le nouveau mesh
        this.addNode(_gridChunk[((int)y * (_map.getHeightMap() / 16)) +  (int)x], nameChunkSearch);

        
               
    }

    public Node getNodeVoxelChunk() {
        return _nodeVoxelChunks;
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
