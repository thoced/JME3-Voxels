/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Mesh;
import com.jme3.scene.VertexBuffer;
import com.jme3.scene.VertexBuffer.Type;
import com.jme3.scene.shape.Quad;
import com.jme3.util.BufferUtils;
import java.util.ArrayList;
import java.util.Collection;
import mygame.Voxel.TypeVoxel;

/**
 *
 * @author Thonon
 */
public class Chunk
{
    private Vector2f _worldPosition;
    
    private MapLoader _mapLoader;
   
    private int _height;
    
    //private Collection<Voxel> _listVoxels;
    
   // private short[] _gridChunk;
    
    private static int _sizeChunk = 16;
    
    private Mesh _meshChunk;
    
    public Chunk(Vector2f worldPosition,MapLoader mapLoader) 
    {
        _worldPosition = worldPosition;
        _mapLoader = mapLoader;
        // instance du listVoxels
        //_listVoxels = new ArrayList<Voxel>();
       //_gridChunk = new short[65536];
       
        // makemeshchunk
        //makeGridChunk();
        // make mesh chunk
        _meshChunk = new Mesh();
        //makeMeshChunk();
        makeMeshChunkV2();
    }
    
    
    private void makeMeshChunkV2()
    {
     
      Vector3f addRelative = new Vector3f();
        
      Collection<Vector3f> vBuff = new ArrayList<Vector3f>();
      Collection<Vector2f> vText = new ArrayList<Vector2f>();
      Collection<Vector3f> vNorm = new ArrayList<Vector3f>();
      
     
      int width = _mapLoader.getWidthMap();
      int height =  _mapLoader.getHeightMap();
      
          
        for(int z=(int)_worldPosition.y; z < (int)_worldPosition.y + 16 ;z++)
        {
            for(int x=(int)_worldPosition.x;x < (int)_worldPosition.x + 16;x++)
            {
                for(int y=0;y<256;y++)
                {
                    if(_mapLoader.getGridMap3d()[(y*256)+(z*height)+x] == 1)
                    {
                           addRelative.set(x, y, z);
                           int rx = x,ry = y,rz = z;
                           
                           ry = y + 1;
                          
                            if(ry > 255 || _mapLoader.getGridMap3d()[(ry*256)+(z*height)+x] == 0)
                            {
                                 // top
                                 vBuff.add(new Vector3f(-.5f,+.5f,+.5f).add(addRelative));
                                 vBuff.add(new Vector3f(+.5f,+.5f,+.5f).add(addRelative));
                                 vBuff.add(new Vector3f(+.5f,+.5f,-.5f).add(addRelative));
                                 vBuff.add(new Vector3f(-.5f,+.5f,-.5f).add(addRelative));
                                 // TextCoord
                                 vText.add(new Vector2f(0,0)); 
                                 vText.add(new Vector2f(1f,0));
                                 vText.add(new Vector2f(1f,1f));
                                 vText.add(new Vector2f(0,1f));
                                 // Normal
                                 vNorm.add(new Vector3f(0,1,0));
                                 vNorm.add(new Vector3f(0,1,0));
                                 vNorm.add(new Vector3f(0,1,0));
                                 vNorm.add(new Vector3f(0,1,0));


                            }
                          
                           
                           ry = y - 1;
                          
                           if(ry < 0 ||  _mapLoader.getGridMap3d()[(ry*256)+(z*height)+x] == 0)
                           {
                                // down
                                vBuff.add(new Vector3f(+.5f,-.5f,+.5f).add(addRelative));
                                vBuff.add(new Vector3f(-.5f,-.5f,+.5f).add(addRelative));
                                vBuff.add(new Vector3f(-.5f,-.5f,-.5f).add(addRelative));
                                vBuff.add(new Vector3f(+.5f,-.5f,-.5f).add(addRelative));
                                // TextCoord
                                vText.add(new Vector2f(0,0)); 
                                vText.add(new Vector2f(1f,0));
                                vText.add(new Vector2f(1f,1f));
                                vText.add(new Vector2f(0,1f));
                                 // Normal
                                vNorm.add(new Vector3f(0,-1,0));
                                vNorm.add(new Vector3f(0,-1,0));
                                vNorm.add(new Vector3f(0,-1,0));
                                vNorm.add(new Vector3f(0,-1,0));
                               
                           }
                          
                           
                           rx = x + 1;
                           
                            if(rx > width-1 || _mapLoader.getGridMap3d()[(y*256)+(z*height)+rx] == 0)
                            {
                                 // right
                                 vBuff.add(new Vector3f(+.5f,+.5f,-.5f).add(addRelative));
                                 vBuff.add(new Vector3f(+.5f,+.5f,+.5f).add(addRelative));
                                 vBuff.add(new Vector3f(+.5f,-.5f,+.5f).add(addRelative));
                                 vBuff.add(new Vector3f(+.5f,-.5f,-.5f).add(addRelative));
                                 // TextCoord
                                 vText.add(new Vector2f(0,0)); 
                                 vText.add(new Vector2f(1f,0));
                                 vText.add(new Vector2f(1f,1f));
                                 vText.add(new Vector2f(0,1f));
                                  // Normal
                                 vNorm.add(new Vector3f(1,0,0));
                                 vNorm.add(new Vector3f(1,0,0));
                                 vNorm.add(new Vector3f(1,0,0));
                                 vNorm.add(new Vector3f(1,0,0));

                            }
                           
                           
                           rx = x - 1;
                           
                            if(rx < 0 || _mapLoader.getGridMap3d()[(y*256)+(z*height)+rx] == 0)
                            {
                                // left
                                vBuff.add(new Vector3f(-.5f,+.5f,+.5f).add(addRelative));
                                vBuff.add(new Vector3f(-.5f,+.5f,-.5f).add(addRelative));
                                vBuff.add(new Vector3f(-.5f,-.5f,-.5f).add(addRelative));
                                vBuff.add(new Vector3f(-.5f,-.5f,+.5f).add(addRelative));
                                // TextCoord
                                vText.add(new Vector2f(0,0)); 
                                vText.add(new Vector2f(1f,0));
                                vText.add(new Vector2f(1f,1f));
                                vText.add(new Vector2f(0,1f));
                                 // Normal
                                vNorm.add(new Vector3f(-1,0,0));
                                vNorm.add(new Vector3f(-1,0,0));
                                vNorm.add(new Vector3f(-1,0,0));
                                vNorm.add(new Vector3f(-1,0,0));
                                
                            }
                           
                           
                           rz = z - 1;
                           
                           if(rz < 0 || _mapLoader.getGridMap3d()[(y*256)+(rz*height)+x] == 0)
                           {
                                // front
                                vBuff.add(new Vector3f(-.5f,+.5f,-.5f).add(addRelative));
                                vBuff.add(new Vector3f(+.5f,+.5f,-.5f).add(addRelative));
                                vBuff.add(new Vector3f(+.5f,-.5f,-.5f).add(addRelative));
                                vBuff.add(new Vector3f(-.5f,-.5f,-.5f).add(addRelative));
                                // TextCoord
                                vText.add(new Vector2f(0,0)); 
                                vText.add(new Vector2f(1f,0));
                                vText.add(new Vector2f(1f,1f));
                                vText.add(new Vector2f(0,1f));
                                 // Normal
                                vNorm.add(new Vector3f(0,0,1));
                                vNorm.add(new Vector3f(0,0,1));
                                vNorm.add(new Vector3f(0,0,1));
                                vNorm.add(new Vector3f(0,0,1));
                                
                           }
                          
                           
                           rz = z + 1;
                          
                           if(rz > height-1|| _mapLoader.getGridMap3d()[(y*256)+(rz*height)+x] == 0)
                           {
                                // back
                                vBuff.add(new Vector3f(+.5f,+.5f,+.5f).add(addRelative));
                                vBuff.add(new Vector3f(-.5f,+.5f,+.5f).add(addRelative));
                                vBuff.add(new Vector3f(-.5f,-.5f,+.5f).add(addRelative));
                                vBuff.add(new Vector3f(+.5f,-.5f,+.5f).add(addRelative));
                                // TextCoord
                                vText.add(new Vector2f(0,0)); 
                                vText.add(new Vector2f(1f,0));
                                vText.add(new Vector2f(1f,1f));
                                vText.add(new Vector2f(0,1f));
                                 // Normal
                                vNorm.add(new Vector3f(0,0,-1));
                                vNorm.add(new Vector3f(0,0,-1));
                                vNorm.add(new Vector3f(0,0,-1));
                                vNorm.add(new Vector3f(0,0,-1));
                                
                           }
                           
                    }
                           
                           
                }
            }
        }
        
        /*
        // ----------
        for(int z=0;z<16;z++)
        {
            for(int x=0;x<16;x++)
            {
               for(int y=0;y<256;y++)
               {
                   if(_gridChunk[(y*256)+(z*16)+x] == 1)
                   {
                       int rx = x,ry = y,rz = z;
                       Vector3f addRelative = new Vector3f(x,y,z);//.add(new Vector3f(_worldPosition.x,0,_worldPosition.y));
                       
                       // top
                       ry = y + 1;
                       if(ry > 255 || _gridChunk[(ry*256)+(rz*16)+rx] == 0)
                       {
                          
                           vBuff.add(new Vector3f(-1f,+1f,+1f).add(addRelative));
                           vBuff.add(new Vector3f(+1f,+1f,+1f).add(addRelative));
                           vBuff.add(new Vector3f(+1f,+1f,-1f).add(addRelative));
                           vBuff.add(new Vector3f(-1f,+1f,-1f).add(addRelative));
                          
                         
                       }
                       /*
                       // down
                       rz = z - 1;
                       if(rz < 0 || _gridChunk[(rz*256)+(ry*16)+rx] == 0)
                       {
                         
                           
                           
                           vBuff.add(new Vector3f(-1f,-1f,+1f).add(addRelative));
                           vBuff.add(new Vector3f(+1f,-1f,+1f).add(addRelative));
                           vBuff.add(new Vector3f(+1f,-1f,-1f).add(addRelative));
                           vBuff.add(new Vector3f(-1f,-1f,-1f).add(addRelative));
                            
                       }
                       /*
                       // right
                       rx = x + 1;
                       if(rx > 16 || _gridChunk[(rz*256)+(ry*16)+rx] == 0)
                       {
                           
                           box[1] = new Vector3f(+.5f,+.5f,+.5f);
                           box[2] = new Vector3f(+.5f,+.5f,-.5f);
                           box[2] = new Vector3f(+.5f,-.5f,-.5f);
                           box[3] = new Vector3f(-.5f,-.5f,+.5f);
                       }
                       
                    
                   
                     
                   }
               }
            }
            
            
        }
        */
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
                   
       
          _meshChunk.updateBound();
    }
    
    

    public Mesh getMeshChunk() {
        return _meshChunk;
    }

    public Vector2f getWorldPosition() {
        return _worldPosition;
    }

    
}
