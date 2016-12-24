/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import com.jme3.math.Vector2f;
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
    
    public ChunkManager(MapLoader mapLoader) 
    {
        _mapLoader = mapLoader;
        _listChunks = new ArrayList<Chunk>();
        
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
             
          }
          
      }
    }

    public Collection<Chunk> getListChunks() {
        return _listChunks;
    }
    
    
    
}
