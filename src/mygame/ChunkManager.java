/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import java.util.ArrayList;
import java.util.Collection;

/**
 *
 * @author Thonon
 */
public class ChunkManager 
{
    private MapLoader _mapLoader;
    
    private Collection<Chunk> _listChunks;
    
    private Chunk[] _gridChunk;
    
    public ChunkManager(MapLoader mapLoader) 
    {
        _mapLoader = mapLoader;
        _listChunks = new ArrayList<Chunk>();
        
        _gridChunk = new Chunk[_mapLoader.getWidthMap() * _mapLoader.getHeightMap()];
        
        manageChunk();
    }
    
    private void manageChunk()
    {
      for(int y=0;y<_mapLoader.getHeightMap();y+=16)
      { 
          for(int x=0;x<_mapLoader.getWidthMap();x+=16)
          {
              // System.out.println(_heightMap[y][x]);
              Chunk c = new Chunk(new Vector2f(x,y),_mapLoader);
              _listChunks.add(c);
              // ajout dans un grid pour pouvoir y accéder plus rapidement
              _gridChunk[(y * _mapLoader.getHeightMap()) + x] = c;
             
          }
          
      }
    }
    
    public void addVoxelToGrid(Vector3f p)
    {
        // ajout du voxel dans la grid3d generale
        _mapLoader.getGridMap3d()[((int)p.y*_mapLoader.getzWidth()) + ((int)p.z * _mapLoader.getHeightMap()) +(int) p.x] = 1;
        // on détermine le chunk qui correspond à la modification pour réinitialiser le mesh
        Vector2f chunkPos = new Vector2f((int)p.x / 16,(int)p.z / 16);
        
        
    }
    
    

    public Collection<Chunk> getListChunks() {
        return _listChunks;
    }
    
    
    
}
