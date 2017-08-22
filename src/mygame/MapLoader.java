/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import com.jme3.asset.AssetManager;
import com.jme3.texture.Image;
import com.jme3.texture.Image.Format;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture.Type;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import pathfinding.PathFindingContext;
import pathfinding.TileBasedMap;

/**
 *
 * @author Thonon
 */
public class MapLoader implements TileBasedMap
{
    
    private String _nameFile = null;
    
    private AssetManager _assetManager;
    
    private int _widthMap;
    private int _heightMap;
    private int _zWidth;
    
    // tableau heightmap
    private short[] _heighMapGrid;
    // tableau du grid 3d
    private short[] _gridMap3d;
    
    //debug
    private Collection debug;

    public MapLoader(AssetManager assetManager) 
    {
        _assetManager = assetManager;
        
        loaderMap();
    }
    
    public MapLoader(String nameFile, AssetManager assetManager) 
    {
        _nameFile = nameFile;
     
        _assetManager = assetManager;
        
        debug = new ArrayList();
        // chargement map
        loaderMap();
     
    }
    
    private void loaderMap()
    {
        if(_nameFile == null)
            return;
        
        // chargement de la map sous forme de texture
        Texture textureMap = _assetManager.loadTexture(_nameFile);
     
        
        // parse de la texture
        if(textureMap != null)
        {
            Image image = textureMap.getImage();
            _widthMap = image.getWidth();
            _heightMap = image.getHeight();
            
            
            Type typeTexture = textureMap.getType();
                    
            if(typeTexture == Type.TwoDimensional)
            {
              
                ByteBuffer buffer = image.getData(0); 
                System.out.println(buffer.capacity());
                
                // on positionne l'indice du buffer à 0
                buffer.position(0);
               
                // instance de la mappixels
                _heighMapGrid = new short[_widthMap * _heightMap];
                
                System.out.println(image.getFormat());
                
                if(image.getFormat() == Format.Luminance8)
                {
                    System.out.println("ok pour le gris");
                 
                    for(int y = 0;y<_heightMap;y++)
                    {
                                               
                        for(int x=0;x<_widthMap;x++)
                        {
                                           
                            _heighMapGrid[(y * _heightMap) + x] = (short)(buffer.get() & 0xff); // for unsigned byte
                          
                        }
                    }
                    
                   
                    // instance du gridmap3d
                    _gridMap3d = new short[_widthMap * _heightMap * 256];
                    for(int i=0;i<_gridMap3d.length;i++)
                        _gridMap3d[i] = 0;
                    
                    // create de ZWith
                    _zWidth = _widthMap * _heightMap;
                    
                    // make grid
                    makeGrid3d();
                   
                    int test = 0;
                }
                
                
            }
        }
    }
    
    private void makeGrid3d()
    {
       
        // on parse le heightmapgrid a partir de la position de monde
        for(int y = 0;y<_heightMap; y++)
        {
            for(int x = 0;x<_widthMap ; x++)
            {
                // pour chaque position y on créer le nombre de voxel nécessaire
                short value = _heighMapGrid[(y * _heightMap) + x];
                // création de la liste de hauteur de voxels
                
               // System.out.println("val X "+ x + "val y " + y + "value:  " + (int)value);
               // _listVoxels.add(new Voxel(new Vector3f(lx,ly,value),TypeVoxel.BLOC01));
                prepareGrid(value, x,  y);
               
            }
           
        }
        
      
    }
    
    private void prepareGrid(short value,int x, int z)
    {
        for(short y=0;y < value;y++)
        { 
           _gridMap3d[(y*_zWidth)+(z * _heightMap)+x] = 1;
          
        }
        
        
       
        
    }
    

    public short[] getHeighMapGrid() {
        return _heighMapGrid;
    }

    public short[] getGridMap3d() {
        return _gridMap3d;
    }
        
    public int getWidthMap() {
        return _widthMap;
    }

    public int getHeightMap() {
        return _heightMap;
    }

    public int getzWidth() {
        return _zWidth;
    }

    @Override
    public int getDepthInTiles() {
       return 256;
    }

    @Override
    public int getWidthInTiles() {
        return _widthMap;
    }

    @Override
    public int getHeightInTiles() {
       return _heightMap;
    }

    @Override
    public void pathFinderVisited(int x, int y, int z) {
       System.out.println("visited: " + x + " " + y + " " + z);
    }

    @Override
    public boolean blocked(PathFindingContext context, int tx, int ty, int tz) {
       
        if(_gridMap3d[(ty*_zWidth)+(tz * _heightMap)+tx] == 1) // un bloc
            return true;
       
         if(_gridMap3d[(ty*_zWidth)+(tz * _heightMap)+tx] == 0){ 
             // deux vides l'un au dessus de l'autre ne sont pas franchissable
             ty--;
             if(_gridMap3d[(ty*_zWidth)+(tz * _heightMap)+tx] == 0)
                 return true;
         }
         
        return false;
    }

    @Override
    public float getCost(PathFindingContext context, int tx, int ty, int tz) {

        if(Math.abs(context.getSourceY() - ty) > 0.5f){
            // augment le cout lorsque qu'il faut escalader
            return 0.8f;
        }
    
       return 0.5f;
            
            
    }
    
    
    
    
}
