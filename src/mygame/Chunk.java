/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import ModelsBloc.BlockBase;
import com.jme3.bounding.BoundingBox;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.VertexBuffer;
import com.jme3.scene.VertexBuffer.Type;
import com.jme3.scene.shape.Quad;
import com.jme3.util.BufferUtils;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import jme3tools.optimize.GeometryBatchFactory;
import mygame.Voxel.TypeVoxel;

/**
 *
 * @author Thonon
 */
public class Chunk
{
    private String _nameChunk;
    
    private Vector2f _worldPosition;
    
    private MapLoader _mapLoader;
   
    private int _height;
    
    //private Collection<Voxel> _listVoxels;
    
   // private short[] _gridChunk;
    
    private static int _sizeChunk = 16;
    
    private Mesh _meshChunk;
    
    private Mesh _meshTree;
    
    public Chunk(Vector2f worldPosition,MapLoader mapLoader) 
    {
        _worldPosition = worldPosition;
        _mapLoader = mapLoader;
        _nameChunk = "[" + (int)_worldPosition.x + "][" + (int)_worldPosition.y + "]"; 
      
        // instance du MeshChunk
        _meshChunk = new Mesh();
        // instance du Mesh Tree
        _meshTree  = new Mesh();
        
        // construction du MeshChunk
        makeMeshChunk();
        // construction du patch tree
        makeTreePatch();
    }
    
    public void updateMeshChunk()
    {
        // update du chunk car une modification a eu lieu
        // suppresion des vertexBuffer
        _meshChunk.clearBuffer(Type.Position);
        _meshChunk.clearBuffer(Type.TexCoord);
        _meshChunk.clearBuffer(Type.Index);
        _meshChunk.clearBuffer(Type.Normal);
        
        // appel au makemeshchunk
        makeMeshChunk();
 
    }
    
    private void makeTreePatch(){
       Spatial treeS = SingleModelAsset.getInstance().getListAsset().get("tree01");
       Collection<Geometry> treeCollection = new ArrayList<Geometry>();
       
       for(int y = (int)_worldPosition.y; y < (int)_worldPosition.y + 16; y++){
           for(int x = (int)_worldPosition.x;x < (int)_worldPosition.x + 16; x++){
             
               short h = _mapLoader.getTreeGrid()[x][y];
               if(h != -1){
                   // si le h est négatif, alors il n'y a pas d'arbre à générer
                Spatial t = treeS.clone();
                t.setLocalTranslation(x, h, y);
                treeCollection.add((Geometry)((Node)t).getChild("arbre"));
               }
           }
       }
       if(!treeCollection.isEmpty()){
       GeometryBatchFactory.mergeGeometries(treeCollection, _meshTree);
       _meshTree.updateBound();
       }
    }
    
    private void makeMeshChunk()
    {
     
      Vector3f addRelative = new Vector3f();
        
      Collection<Vector3f> vBuff = new ArrayList<Vector3f>();
      Collection<Vector2f> vText = new ArrayList<Vector2f>();
      Collection<Vector3f> vNorm = new ArrayList<Vector3f>();
      
     
      int width = _mapLoader.getWidthMap();
      int height =  _mapLoader.getHeightMap();
      
      // reception du singleton uvselect
      SingleUvSelect uv = SingleUvSelect.getInstance();
          
        for(int z=(int)_worldPosition.y; z < (int)_worldPosition.y + 16 ;z++)
        {
            for(int x=(int)_worldPosition.x;x < (int)_worldPosition.x + 16;x++)
            {
                for(int y=0;y<256;y++)
                {

                   short tb = _mapLoader.getGridMap3d()[(y*_mapLoader.getzWidth())+(z * height)+x];
                    
                    if(tb != 0) // ce n'est pas un bloc vide
                    {
                           BlockBase b = null;
                           // obtention du type de block
                           if(tb == 1)
                           b = SingleUvSelect.getInstance().getUv("BRICK");
                           else if(tb == 2){
                           b = SingleUvSelect.getInstance().getUv("GRASS");
                           }
                        
                           addRelative.set(x, y, z);
                           int rx = x,ry = y,rz = z;
                           
                            ry = y + 1;
                            if(ry > 255 || _mapLoader.getGridMap3d()[(ry*_mapLoader.getzWidth())+(z * height)+x] == 0)
                            {
                                 // top
                                 vBuff.add(new Vector3f(-.5f,+.5f,+.5f).add(addRelative));
                                 vBuff.add(new Vector3f(+.5f,+.5f,+.5f).add(addRelative));
                                 vBuff.add(new Vector3f(+.5f,+.5f,-.5f).add(addRelative));
                                 vBuff.add(new Vector3f(-.5f,+.5f,-.5f).add(addRelative));
                                 // TextCoord
                                 vText.add(b.getTop()[0]); 
                                 vText.add(b.getTop()[1]); 
                                 vText.add(b.getTop()[2]);  
                                 vText.add(b.getTop()[3]);  
                                 // Normal
                                 vNorm.add(new Vector3f(0,1,0));
                                 vNorm.add(new Vector3f(0,1,0));
                                 vNorm.add(new Vector3f(0,1,0));
                                 vNorm.add(new Vector3f(0,1,0));


                            }
                          
                           
                           ry = y - 1;
                          
                           if(ry < 0 ||  _mapLoader.getGridMap3d()[(ry*_mapLoader.getzWidth())+(z * height)+x] == 0)
                           {
                                // down
                                vBuff.add(new Vector3f(+.5f,-.5f,+.5f).add(addRelative));
                                vBuff.add(new Vector3f(-.5f,-.5f,+.5f).add(addRelative));
                                vBuff.add(new Vector3f(-.5f,-.5f,-.5f).add(addRelative));
                                vBuff.add(new Vector3f(+.5f,-.5f,-.5f).add(addRelative));
                                // TextCoord
                                 vText.add(b.getDown()[0]); 
                                 vText.add(b.getDown()[1]); 
                                 vText.add(b.getDown()[2]);  
                                 vText.add(b.getDown()[3]); 
                                 // Normal
                                vNorm.add(new Vector3f(0,-1,0));
                                vNorm.add(new Vector3f(0,-1,0));
                                vNorm.add(new Vector3f(0,-1,0));
                                vNorm.add(new Vector3f(0,-1,0));
                               
                           }
                          
                           
                           rx = x + 1;
                           
                            if(rx > width-1 || _mapLoader.getGridMap3d()[(y*_mapLoader.getzWidth())+(z * height)+rx] == 0)
                            {
                                 // right
                                 vBuff.add(new Vector3f(+.5f,+.5f,-.5f).add(addRelative));
                                 vBuff.add(new Vector3f(+.5f,+.5f,+.5f).add(addRelative));
                                 vBuff.add(new Vector3f(+.5f,-.5f,+.5f).add(addRelative));
                                 vBuff.add(new Vector3f(+.5f,-.5f,-.5f).add(addRelative));
                                 // TextCoord
                                 vText.add(b.getRight()[0]); 
                                 vText.add(b.getRight()[1]); 
                                 vText.add(b.getRight()[2]);  
                                 vText.add(b.getRight()[3]); 
                                  // Normal
                                 vNorm.add(new Vector3f(1,0,0));
                                 vNorm.add(new Vector3f(1,0,0));
                                 vNorm.add(new Vector3f(1,0,0));
                                 vNorm.add(new Vector3f(1,0,0));

                            }
                           
                           
                           rx = x - 1;
                           
                            if(rx < 0 || _mapLoader.getGridMap3d()[(y*_mapLoader.getzWidth())+(z * height)+rx] == 0)
                            {
                                // left
                                vBuff.add(new Vector3f(-.5f,+.5f,+.5f).add(addRelative));
                                vBuff.add(new Vector3f(-.5f,+.5f,-.5f).add(addRelative));
                                vBuff.add(new Vector3f(-.5f,-.5f,-.5f).add(addRelative));
                                vBuff.add(new Vector3f(-.5f,-.5f,+.5f).add(addRelative));
                                // TextCoord
                                 vText.add(b.getLeft()[0]); 
                                 vText.add(b.getLeft()[1]); 
                                 vText.add(b.getLeft()[2]);  
                                 vText.add(b.getLeft()[3]); 
                                 // Normal
                                vNorm.add(new Vector3f(-1,0,0));
                                vNorm.add(new Vector3f(-1,0,0));
                                vNorm.add(new Vector3f(-1,0,0));
                                vNorm.add(new Vector3f(-1,0,0));
                                
                            }
                           
                           
                           rz = z - 1;
                           
                           if(rz < 0 || _mapLoader.getGridMap3d()[(y*_mapLoader.getzWidth())+(rz * height)+x] == 0)
                           {
                                // front
                                vBuff.add(new Vector3f(-.5f,+.5f,-.5f).add(addRelative));
                                vBuff.add(new Vector3f(+.5f,+.5f,-.5f).add(addRelative));
                                vBuff.add(new Vector3f(+.5f,-.5f,-.5f).add(addRelative));
                                vBuff.add(new Vector3f(-.5f,-.5f,-.5f).add(addRelative));
                                // TextCoord
                                 vText.add(b.getFront()[0]); 
                                 vText.add(b.getFront()[1]); 
                                 vText.add(b.getFront()[2]);  
                                 vText.add(b.getFront()[3]); 
                                 // Normal
                                vNorm.add(new Vector3f(0,0,-1));
                                vNorm.add(new Vector3f(0,0,-1));
                                vNorm.add(new Vector3f(0,0,-1));
                                vNorm.add(new Vector3f(0,0,-1));
                                
                           }
                          
                           
                           rz = z + 1;
                          
                           if(rz > height-1|| _mapLoader.getGridMap3d()[(y*_mapLoader.getzWidth())+(rz * height)+x] == 0)
                           {
                                // back
                                vBuff.add(new Vector3f(+.5f,+.5f,+.5f).add(addRelative));
                                vBuff.add(new Vector3f(-.5f,+.5f,+.5f).add(addRelative));
                                vBuff.add(new Vector3f(-.5f,-.5f,+.5f).add(addRelative));
                                vBuff.add(new Vector3f(+.5f,-.5f,+.5f).add(addRelative));
                                // TextCoord
                                 vText.add(b.getBehind()[0]); 
                                 vText.add(b.getBehind()[1]); 
                                 vText.add(b.getBehind()[2]);  
                                 vText.add(b.getBehind()[3]); 
                                 // getBehind
                                vNorm.add(new Vector3f(0,0,1));
                                vNorm.add(new Vector3f(0,0,1));
                                vNorm.add(new Vector3f(0,0,1));
                                vNorm.add(new Vector3f(0,0,1));
                                
                           }
                           
                    }
                           
                           
                }
            }
        }
          
           // indices
                       
 
         int[] ind = new int[(vBuff.size() / 4) * 6];
         int i=0;
         for(int j=0;j<ind.length;j+=6)
         {
           ind[j] = 0+(i*4);
           ind[j+1] = 1+(i*4);
           ind[j+2] = 2+(i*4);
                           
           ind[j+3] = 2+(i*4);
           ind[j+4] = 3+(i*4);
           ind[j+5] = 0+(i*4);
           i++;
          }
           // 0,1,2 2,3,0
                       
            // vertexbuffer
            Vector3f[] vb = vBuff.toArray(new Vector3f[vBuff.size()]);
            // Textcoord
            Vector2f[] tb = vText.toArray(new Vector2f[vText.size()]);
            // Normal
            Vector3f[] vn = vNorm.toArray(new Vector3f[vNorm.size()]);
            
            _meshChunk.setBuffer(Type.Position, 3, BufferUtils.createFloatBuffer(vb));
            _meshChunk.setBuffer(Type.Index, 3 ,BufferUtils.createIntBuffer(ind));   
            _meshChunk.setBuffer(Type.TexCoord,2,BufferUtils.createFloatBuffer(tb));
            _meshChunk.setBuffer(Type.Normal,3,BufferUtils.createFloatBuffer(vn));
                   
            _meshChunk.setMode(Mesh.Mode.Triangles);
           
         // update du boundingBox
         _meshChunk.updateBound();
         // creation du mesh de collision
         _meshChunk.createCollisionData();
    }

    public Mesh getMeshTree() {
        return _meshTree;
    }
    
    

    public Mesh getMeshChunk() {
        return _meshChunk;
    }

    public Vector2f getWorldPosition() {
        return _worldPosition;
    }

    public String getNameChunk() {
        return _nameChunk;
    }

    public void setNameChunk(String _nameChunk) {
        this._nameChunk = _nameChunk;
    }

    
}
