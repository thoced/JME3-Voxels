/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import com.jme3.bounding.BoundingBox;

import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.Savable;
import com.jme3.math.ColorRGBA;

import com.jme3.math.Ray;

import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Mesh;
import com.jme3.scene.VertexBuffer;
import com.jme3.scene.VertexBuffer.Type;
import com.jme3.scene.shape.Quad;
import com.jme3.util.BufferUtils;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import mygame.Voxel.TypeVoxel;

/**
 *
 * @author Thonon
 */
public class Chunk implements Savable
{
    private String _nameChunk;
    
    private Vector2f _worldPosition;
    
    private MapLoader _mapLoader;
   
    private int _height;
    
    //private Collection<Voxel> _listVoxels;
    
   // private short[] _gridChunk;
    
    private static int _sizeChunk = 16;
    
    private Mesh _meshChunk;
    
    // vecteur de PositionBuffer
    private Vector3f[] _vb;
    // vecteur de NormalBuffer
    private Vector3f[] _vn;
    // vecteur de TextCoordBuffer
    private Vector2f[] _tb;
    // vecteur de ColorBuffer
    private ColorRGBA[] _cb;
    
    public Chunk(Vector2f worldPosition,MapLoader mapLoader) 
    {
        _worldPosition = worldPosition;
        _mapLoader = mapLoader;
        _nameChunk = "[" + (int)_worldPosition.x + "][" + (int)_worldPosition.y + "]"; 
        // instance du listVoxels
        //_listVoxels = new ArrayList<Voxel>();
       //_gridChunk = new short[65536];
       
        // makemeshchunk
        //makeGridChunk();
        // make mesh chunk
        _meshChunk = new Mesh();
        //makeMeshChunk();
        makeMeshChunk();
    }
    
    public void updateMeshChunk()
    {
        // update du chunk car une modification a eu lieu
        // suppresion des vertexBuffer
        _meshChunk.clearBuffer(Type.Position);
        _meshChunk.clearBuffer(Type.TexCoord);
        _meshChunk.clearBuffer(Type.Index);
        _meshChunk.clearBuffer(Type.Normal);
        _meshChunk.clearBuffer(Type.Color);
        
        // appel au makemeshchunk
        makeMeshChunk();
        // update lightprobe
        updateLightProbeColor();
 
    }
    
    private void makeMeshChunk()
    {
     
      Vector3f addRelative = new Vector3f();
        
      Collection<Vector3f> vBuff = new ArrayList<Vector3f>();
      Collection<Vector2f> vText = new ArrayList<Vector2f>();
      Collection<Vector3f> vNorm = new ArrayList<Vector3f>();
      Collection<ColorRGBA> vColor = new ArrayList<ColorRGBA>(); 
      
     
      int width = _mapLoader.getWidthMap();
      int height =  _mapLoader.getHeightMap();
      
          
        for(int z=(int)_worldPosition.y; z < (int)_worldPosition.y + 16 ;z++)
        {
            for(int x=(int)_worldPosition.x;x < (int)_worldPosition.x + 16;x++)
            {
                for(int y=0;y<256;y++)
                {
                    if(_mapLoader.getGridMap3d()[(y*_mapLoader.getzWidth())+(z * height)+x] == 1)
                    {
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
                                 vText.add(new Vector2f(0,0)); 
                                 vText.add(new Vector2f(1f,0));
                                 vText.add(new Vector2f(1f,1f));
                                 vText.add(new Vector2f(0,1f));
                                 // Normal
                                 vNorm.add(new Vector3f(0,1,0));
                                 vNorm.add(new Vector3f(0,1,0));
                                 vNorm.add(new Vector3f(0,1,0));
                                 vNorm.add(new Vector3f(0,1,0));
                                 // Color
                                 vColor.add(new ColorRGBA(1,1,1,1));
                                 vColor.add(new ColorRGBA(1,1,1,1));
                                 vColor.add(new ColorRGBA(1,1,1,1));
                                 vColor.add(new ColorRGBA(1,1,1,1));
                                 

                                
                                 
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
                                 vText.add(new Vector2f(0,0)); 
                                 vText.add(new Vector2f(1f,0));
                                 vText.add(new Vector2f(1f,1f));
                                 vText.add(new Vector2f(0,1f));
                                  // Normal
                                 vNorm.add(new Vector3f(0,-1,0));
                                 vNorm.add(new Vector3f(0,-1,0));
                                 vNorm.add(new Vector3f(0,-1,0));
                                 vNorm.add(new Vector3f(0,-1,0));
                                  // Color
                                 vColor.add(new ColorRGBA(1,1,1,1));
                                 vColor.add(new ColorRGBA(1,1,1,1));
                                 vColor.add(new ColorRGBA(1,1,1,1));
                                 vColor.add(new ColorRGBA(1,1,1,1));

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
                                 vText.add(new Vector2f(0,0)); 
                                 vText.add(new Vector2f(1f,0));
                                 vText.add(new Vector2f(1f,1f));
                                 vText.add(new Vector2f(0,1f));
                                  // Normal
                                 vNorm.add(new Vector3f(1,0,0));
                                 vNorm.add(new Vector3f(1,0,0));
                                 vNorm.add(new Vector3f(1,0,0));
                                 vNorm.add(new Vector3f(1,0,0));
                                  // Color
                                 vColor.add(new ColorRGBA(1,1,1,1));
                                 vColor.add(new ColorRGBA(1,1,1,1));
                                 vColor.add(new ColorRGBA(1,1,1,1));
                                 vColor.add(new ColorRGBA(1,1,1,1));

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
                                vText.add(new Vector2f(0,0)); 
                                vText.add(new Vector2f(1f,0));
                                vText.add(new Vector2f(1f,1f));
                                vText.add(new Vector2f(0,1f));
                                 // Normal
                                vNorm.add(new Vector3f(-1,0,0));
                                vNorm.add(new Vector3f(-1,0,0));
                                vNorm.add(new Vector3f(-1,0,0));
                                vNorm.add(new Vector3f(-1,0,0));
                                // Color
                                vColor.add(new ColorRGBA(1,1,1,1));
                                vColor.add(new ColorRGBA(1,1,1,1));
                                vColor.add(new ColorRGBA(1,1,1,1));
                                vColor.add(new ColorRGBA(1,1,1,1));
                                
                                
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
                                vText.add(new Vector2f(0,0)); 
                                vText.add(new Vector2f(1f,0));
                                vText.add(new Vector2f(1f,1f));
                                vText.add(new Vector2f(0,1f));
                                 // Normal
                                vNorm.add(new Vector3f(0,0,-1));
                                vNorm.add(new Vector3f(0,0,-1));
                                vNorm.add(new Vector3f(0,0,-1));
                                vNorm.add(new Vector3f(0,0,-1));
                                 // Color
                                vColor.add(new ColorRGBA(1,1,1,1));
                                vColor.add(new ColorRGBA(1,1,1,1));
                                vColor.add(new ColorRGBA(1,1,1,1));
                                vColor.add(new ColorRGBA(1,1,1,1));
                                
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
                                vText.add(new Vector2f(0,0)); 
                                vText.add(new Vector2f(1f,0));
                                vText.add(new Vector2f(1f,1f));
                                vText.add(new Vector2f(0,1f));
                                 // Normal
                                vNorm.add(new Vector3f(0,0,1));
                                vNorm.add(new Vector3f(0,0,1));
                                vNorm.add(new Vector3f(0,0,1));
                                vNorm.add(new Vector3f(0,0,1));
                                 // Color
                                vColor.add(new ColorRGBA(1,1,1,1));
                                vColor.add(new ColorRGBA(1,1,1,1));
                                vColor.add(new ColorRGBA(1,1,1,1));
                                vColor.add(new ColorRGBA(1,1,1,1));
                                
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
            _vb = vBuff.toArray(new Vector3f[vBuff.size()]);
            // Textcoord
            _tb = vText.toArray(new Vector2f[vText.size()]);
            // Normal
            _vn = vNorm.toArray(new Vector3f[vNorm.size()]);
            // Color
            _cb = vColor.toArray(new ColorRGBA[vColor.size()]);
            
            _meshChunk.setBuffer(Type.Position, 3, BufferUtils.createFloatBuffer(_vb));
            _meshChunk.setBuffer(Type.Index, 3 ,BufferUtils.createIntBuffer(ind));   
            _meshChunk.setBuffer(Type.TexCoord,2,BufferUtils.createFloatBuffer(_tb));
            _meshChunk.setBuffer(Type.Normal,3,BufferUtils.createFloatBuffer(_vn));
            _meshChunk.setBuffer(Type.Color, 4, BufferUtils.createFloatBuffer(_cb));
                   
            
  
         // update du boundingBox
         _meshChunk.updateBound();
         // creation du mesh de collision
         _meshChunk.createCollisionData();
    }
    
    public void updateLightProbeColor()
    {
        _meshChunk.clearBuffer(Type.Color);
        
        _meshChunk.setBuffer(Type.Color, 4, BufferUtils.createFloatBuffer(_cb));
       
        
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

    public Vector3f[] getPositionBuffer() {
        return _vb;
    }

    public Vector3f[] getNormalBuffer() {
        return _vn;
    }

    public Vector2f[] getTextCoordBuffer() {
        return _tb;
    }

    public ColorRGBA[] getColorBuffer() {
        return _cb;
    }
    
    

    @Override
    public void write(JmeExporter ex) throws IOException {
        
    }

    @Override
    public void read(JmeImporter im) throws IOException {
       
    }

    
}
