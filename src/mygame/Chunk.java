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


/**
 *
 * @author Thonon
 */
public class Chunk implements Savable
{
    private String _nameChunk;
    
    private Vector2f _worldPosition;
    
    private MapLoader _mapLoader;
    
    // liste des lightprobes associés au chunk
    private Collection<LightProbe> _lightProbes;
   
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
        
        // instance de la liste des lightprobes
        _lightProbes = new ArrayList<LightProbe>();
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
        
       //reset lightfactor
       //resetLightFactor();

        // appel au makemeshchunk
        makeMeshChunk();
       
 
    }
    
    public void resetLightFactor()
    {
         for(int gz=(int)_worldPosition.y ; gz < (int)_worldPosition.y + 16 ;gz++)
        {
            for(int gx=(int)_worldPosition.x ;gx < (int)_worldPosition.x + 16 ;gx++)
            {
                for(int gy=0;gy<256;gy++)
                {
                     try
                     {
                        _mapLoader.getGridLightFactor()[(gy * _mapLoader.getzWidth()) + (gz * _mapLoader.getHeightMap()) + gx] = 0x00; // lightfactore à 0 
                     }
                     catch(java.lang.ArrayIndexOutOfBoundsException a){}
                }
            }
        }
    }
    
    public void illumination()
    {
         for(LightProbe l : _lightProbes)
            l.prepareIllumination(_mapLoader);
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
      
      // black
      ColorRGBA black = new ColorRGBA(0.05f,0.05f,0.05f,1);
      
          
        for(int z=(int)_worldPosition.y; z < (int)_worldPosition.y + 16 ;z++)
        {
            for(int x=(int)_worldPosition.x;x < (int)_worldPosition.x + 16;x++)
            {
                for(int y=0;y<256;y++)
                {
                    if(_mapLoader.getGridMap3d()[(y*_mapLoader.getzWidth())+(z * height)+x]  != 0x00) // si le type n'est pas egale à 0
                    {
                           addRelative.set(x, y, z);
                           // initialisation des r
                           int rx = x,ry = y,rz = z;
                           
                            ry = y + 1;
                            if(ry > 255 || _mapLoader.getGridMap3d()[(ry*_mapLoader.getzWidth())+(z * height)+x] == 0x00)
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
                                 try
                                 {
                                     // récupération du lightfactor du voxel vide
                                     byte lightFactor = (byte)(_mapLoader.getGridLightFactor()[(ry*_mapLoader.getzWidth())+(z * height)+x]);
                                    
                                    // Color avec application du lightfactor
                                    ColorRGBA light = ColorRGBA.White;
                                    light = light.mult(lightFactor / 7f);
                                    vColor.add(black.add(light));
                                    vColor.add(black.add(light));
                                    vColor.add(black.add(light));
                                    vColor.add(black.add(light));
                                 }
                                 catch(ArrayIndexOutOfBoundsException a)
                                 {
                                     // color sans application du lightfactor (si on est au bout de la carte)
                                     vColor.add(black);
                                     vColor.add(black);
                                     vColor.add(black);
                                     vColor.add(black);
                                 }
                                 
                                 
                            }
                          
                           
                           ry = y - 1;
                          
                            if(ry < 0 ||  _mapLoader.getGridMap3d()[(ry*_mapLoader.getzWidth())+(z * height)+x]  == 0x00)
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
                                 
                               try
                                 {
                                     // récupération du lightfactor du voxel vide
                                     byte lightFactor = (byte)(_mapLoader.getGridLightFactor()[(ry*_mapLoader.getzWidth())+(z * height)+x]);
                                    
                                    // Color avec application du lightfactor
                                    ColorRGBA light = ColorRGBA.White;
                                    light = light.mult(lightFactor / 7f);
                                    vColor.add(black.add(light));
                                    vColor.add(black.add(light));
                                    vColor.add(black.add(light));
                                    vColor.add(black.add(light));
                                 }
                                 catch(ArrayIndexOutOfBoundsException a)
                                 {
                                     // color sans application du lightfactor (si on est au bout de la carte)
                                     vColor.add(black);
                                     vColor.add(black);
                                     vColor.add(black);
                                     vColor.add(black);
                                 }

                            }
                           
                          
                           
                           rx = x + 1;
                           
                            if(rx > width-1 || _mapLoader.getGridMap3d()[(y*_mapLoader.getzWidth())+(z * height)+rx]  == 0x00)
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
                                 
                                  try
                                 {
                                     // récupération du lightfactor du voxel vide
                                     byte lightFactor = (byte)(_mapLoader.getGridLightFactor()[(y*_mapLoader.getzWidth())+(z * height)+rx]);
                                    
                                    // Color avec application du lightfactor
                                    ColorRGBA light = ColorRGBA.White;
                                    light = light.mult(lightFactor / 7f);
                                    vColor.add(black.add(light));
                                    vColor.add(black.add(light));
                                    vColor.add(black.add(light));
                                    vColor.add(black.add(light));
                                 }
                                 catch(ArrayIndexOutOfBoundsException a)
                                 {
                                     // color sans application du lightfactor (si on est au bout de la carte)
                                     vColor.add(black);
                                     vColor.add(black);
                                     vColor.add(black);
                                     vColor.add(black);
                                 }

                            }
                           
                           
                           rx = x - 1;
                           
                            if(rx < 0 || _mapLoader.getGridMap3d()[(y*_mapLoader.getzWidth())+(z * height)+rx] == 0x00)
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
                                
                                try
                                 {
                                     // récupération du lightfactor du voxel vide
                                     byte lightFactor = (byte)(_mapLoader.getGridLightFactor()[(y*_mapLoader.getzWidth())+(z * height)+rx]);
                                    
                                    // Color avec application du lightfactor
                                    ColorRGBA light = ColorRGBA.White;
                                    light = light.mult(lightFactor / 7f);
                                    vColor.add(black.add(light));
                                    vColor.add(black.add(light));
                                    vColor.add(black.add(light));
                                    vColor.add(black.add(light));
                                 }
                                 catch(ArrayIndexOutOfBoundsException a)
                                 {
                                     // color sans application du lightfactor (si on est au bout de la carte)
                                     vColor.add(black);
                                     vColor.add(black);
                                     vColor.add(black);
                                     vColor.add(black);
                                 }
                                
                                
                                
                            }
                           
                           
                           rz = z - 1;
                           
                           if(rz < 0 || _mapLoader.getGridMap3d()[(y*_mapLoader.getzWidth())+(rz * height)+x] == 0x00)
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
                                
                                 try
                                 {
                                     // récupération du lightfactor du voxel vide
                                     short lightFactor = (byte)(_mapLoader.getGridLightFactor()[(y*_mapLoader.getzWidth())+(rz * height)+x]);
                                    
                                    // Color avec application du lightfactor
                                    ColorRGBA light = ColorRGBA.White;
                                    light = light.mult(lightFactor / 7f);
                                    vColor.add(black.add(light));
                                    vColor.add(black.add(light));
                                    vColor.add(black.add(light));
                                    vColor.add(black.add(light));
                                 }
                                 catch(ArrayIndexOutOfBoundsException a)
                                 {
                                     // color sans application du lightfactor (si on est au bout de la carte)
                                     vColor.add(black);
                                     vColor.add(black);
                                     vColor.add(black);
                                     vColor.add(black);
                                 }
                                
                           }
                          
                           
                           rz = z + 1;
                          
                           if(rz > height-1|| _mapLoader.getGridMap3d()[(y*_mapLoader.getzWidth())+(rz * height)+x] == 0x00)
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
                                
                                 try
                                 {
                                     // récupération du lightfactor du voxel vide
                                     short lightFactor = (byte)(_mapLoader.getGridLightFactor()[(y*_mapLoader.getzWidth())+(rz * height)+x]);
                                    
                                    // Color avec application du lightfactor
                                    ColorRGBA light = ColorRGBA.White;
                                    light = light.mult(lightFactor / 7f);
                                    vColor.add(black.add(light));
                                    vColor.add(black.add(light));
                                    vColor.add(black.add(light));
                                    vColor.add(black.add(light));
                                 }
                                 catch(ArrayIndexOutOfBoundsException a)
                                 {
                                     // color sans application du lightfactor (si on est au bout de la carte)
                                     vColor.add(black);
                                     vColor.add(black);
                                     vColor.add(black);
                                     vColor.add(black);
                                 }
                                
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
       
    
    public void addLightProbe(LightProbe l)
    {
        _lightProbes.add(l);
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
