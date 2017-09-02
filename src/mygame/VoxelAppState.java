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
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.input.controls.ActionListener;
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
import com.jme3.scene.shape.Box;
import java.util.ArrayList;
import java.util.Collection;

/**
 *
 * @author Thonon
 */
public class VoxelAppState extends AbstractAppState  {

    private int t=0;
    
    private SimpleApplication _app;
    
    private MapLoader _map;
    
    private RigidBodyControl _landscape;
    
    private BulletAppState _bulletAppState;
    
    //private Collection<Chunk> _listChunks;
    
    private Chunk[] _gridChunk;
    
    private Node _nodeVoxelChunk;
    
    private Node _nodeTreeChunk;
    
    private Material _mat;
    
    private Material _matTree;

    public MapLoader getMap() {
        return _map;
    }
    
    
    @Override
    public void initialize(AppStateManager stateManager, Application app)
    {
        super.initialize(stateManager, app);
        
        _app = (SimpleApplication)app;
        _bulletAppState = _app.getStateManager().getState(BulletAppState.class);
        // chargement de la map
        _map = new MapLoader("newmap",app.getAssetManager());
        // creation des chunks
       // _listChunks = new ArrayList<Chunk>();
        _gridChunk = new Chunk[(_map.getWidthMap() / 16) * (_map.getHeightMap() / 16)];
        // manageChunk
        this.manageChunk();
       
               
         
    }
    

    
    private void manageChunk()
    {
         // Création d'un material
        _mat = new Material(_app.getAssetManager(),"Common/MatDefs/Light/Lighting.j3md");
        _mat.setColor("Diffuse", new ColorRGBA(128,128,128,255));
        _mat.setTexture("DiffuseMap",
        _app.getAssetManager().loadTexture("Textures/Textures/textureAtlas.png"));
      //  _mat.setTexture("NormalMap", 
        //        _app.getAssetManager().loadTexture("Textures/Textures/rock_n.jpg"));
        //_mat.setFloat("Shininess", 64f);  // [0,128]
        _mat.getAdditionalRenderState().setFaceCullMode(RenderState.FaceCullMode.Back);
                   
        // Création du material pour le tree
         _matTree = new Material(_app.getAssetManager(),"Common/MatDefs/Light/Lighting.j3md");
         _matTree.setTexture("DiffuseMap", 
                 _app.getAssetManager().loadTexture("Models/Trees/Tree01/diffuse.png"));
         _matTree.setColor("Diffuse", ColorRGBA.White);
        
        // creation du nodeVoxelChunk
        _nodeVoxelChunk = new Node();
        _nodeVoxelChunk.setName("NODE_VOXEL_CHUNK");
        // creation du nodeTreeChunk
        _nodeTreeChunk = new Node();
        _nodeTreeChunk.setName("NODE_TREE_CHUNK");
        
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
              // pour chaque chunk, on récupère le mesh
              // ajout du node
              addNode(c.getMeshChunk(),c.getNameChunk());  
              // ajout du tree
              addPatchTree(c.getMeshTree(),c.getNameChunk());
              lx++;
             
          }
          ly++;
          lx = 0;
          
      }
       ((SimpleApplication)_app).getRootNode().attachChild(_nodeVoxelChunk);
       ((SimpleApplication)_app).getRootNode().attachChild(_nodeTreeChunk);

    }
    
    private void addPatchTree(Mesh m,String name){
        Geometry geo = new Geometry(name,m);
        geo.setShadowMode(RenderQueue.ShadowMode.Cast);
        geo.setMaterial(_matTree);
        _nodeTreeChunk.attachChild(geo);
        
    }
    
    private void addNode(Mesh m,String name)
    {
         // pour chaque mesh on créer une geometrie
         Geometry geo = new Geometry(name,m);
         geo.setShadowMode(RenderQueue.ShadowMode.CastAndReceive);
         geo.setMaterial(_mat);
         // ajout dans le node délégué au Chunk
      
      if(m.getTriangleCount() != 0){
         // si et seulement si un triangle existe dans le chunk - évite une erreur fatal dans le Jbullet
         
         //   creation de la physique
        CollisionShape sceneShape = CollisionShapeFactory.createMeshShape(geo);
        if(sceneShape != null)
          {
           _landscape = new RigidBodyControl(sceneShape, 0);
           geo.addControl(_landscape);
           _bulletAppState.getPhysicsSpace().add(_landscape);
           
          }

      }
      
       // ajout du node dans le NodeVoxelChunk
         _nodeVoxelChunk.attachChild(geo);
        
        
    }
    
    public void removePhysic(Spatial sp){
         if(sp != null){
         RigidBodyControl c = sp.getControl(RigidBodyControl.class);
         _bulletAppState.getPhysicsSpace().remove(c);
         sp.removeControl(RigidBodyControl.class);
                        }
    }
    
    public void addVoxelToGrid(Vector3f p)
    {
        // ajout du voxel dans la grid3d generale
        _map.getGridMap3d()[((int)p.y*_map.getzWidth()) + ((int)p.z * _map.getHeightMap()) + (int) p.x] = 3;
        // on détermine le chunk qui correspond à la modification pour réinitialiser le mesh
        Vector2f chunkPos = new Vector2f((int)p.x / 16,(int)p.z / 16);
        // appel à la methode update du chunk
        if(_gridChunk[((int)chunkPos.y * (_map.getHeightMap() / 16)) +  (int)chunkPos.x] != null)
        {
            // update du chunk (remesh)
            _gridChunk[((int)chunkPos.y * (_map.getHeightMap() / 16)) +  (int)chunkPos.x].updateMeshChunk();
             // suppression de la physique du node
             String nameChunkSearch = "[" + (int)chunkPos.x + "][" + (int)chunkPos.y + "]";
            Spatial sp = _nodeVoxelChunk.getChild(nameChunkSearch);
            this.removePhysic(sp);
            //detachement du node
            _nodeVoxelChunk.detachChildNamed(nameChunkSearch);
            // ajout du nouveau node avec le nouveau mesh
            this.addNode(_gridChunk[((int)chunkPos.y * (_map.getHeightMap() / 16)) +  (int)chunkPos.x].getMeshChunk(), nameChunkSearch);

        }
             
        
    }
    
    public void subVoxelToGrid(Vector3f p)
    {
        // suppresion du voxel dans la grid3d generale
        _map.getGridMap3d()[((int)p.y*_map.getzWidth()) + ((int)p.z * _map.getHeightMap()) + (int) p.x] = 0;
         // on détermine le chunk qui correspond à la modification pour réinitialiser le mesh
        Vector2f chunkPos = new Vector2f((int)p.x / 16,(int)p.z / 16);
        // appel à la methode update du chunk ainsi que les 8 autres chunk
        for(int dy=-1;dy<2;dy++)
        {
            for(int dx=-1;dx<2;dx++)
            {
                try
                {
                    if(_gridChunk[((int)(chunkPos.y + dy) * (_map.getHeightMap() / 16)) +  (int)chunkPos.x + dx] != null)
                    {
                        // update du chunk (remesh) ainsi que les 8 autres chunk 
                        _gridChunk[((int)(chunkPos.y + dy) * (_map.getHeightMap() / 16)) +  (int)chunkPos.x + dx].updateMeshChunk();
                        //detachement du node
                        String nameChunkSearch = "[" + ((int)chunkPos.x + dx) + "][" + ((int)chunkPos.y + dy) + "]";
                        // suppression de la physique du node
                        Spatial sp = _nodeVoxelChunk.getChild(nameChunkSearch);
                        this.removePhysic(sp);
                        // detachement du node de la scene
                        _nodeVoxelChunk.detachChildNamed(nameChunkSearch);
                        // ajout du nouveau node avec le nouveau mesh
                        this.addNode(_gridChunk[((int)(chunkPos.y + dy) * (_map.getHeightMap() / 16)) +  (int)chunkPos.x + dx].getMeshChunk(), nameChunkSearch);

                    }
                }
                catch(ArrayIndexOutOfBoundsException a)
                {
                    
                }
            }
        }
    }

    public Node getNodeVoxelChunk() {
        return _nodeVoxelChunk;
    }

    @Override
    public void update(float tpf) {
        _bulletAppState.update(tpf);
    }
    
    @Override
    public void cleanup() {
        super.cleanup();
        //TODO: clean up what you initialized in the initialize method,
        //e.g. remove all spatials from rootNode
        //this is called on the OpenGL thread after the AppState has been detached
    }
    
}
