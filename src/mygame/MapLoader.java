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
    
    private String _absoluteNameMap;
    private String _absoluteNameTree;
    
    private AssetManager _assetManager;
    
    private int _widthMap;
    private int _heightMap;
    private int _zWidth;
    
    // tableau heightmap
    private short[] _heighMapGrid;
    // tableau tree
    private short[][] _treeGrid;
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
         _absoluteNameMap = "Textures/maps/" + nameFile + "_heightmap.png";
         _absoluteNameTree =  "Textures/maps/" + nameFile + "_tree.png";
           
     
        _assetManager = assetManager;
        
        debug = new ArrayList();
        // chargement map
        loaderMap();
        // chargement des trees
        loaderTree();
     
    }
    
    private void loaderTree(){
        if(_absoluteNameTree == null)
            return;
                
        // chargement des arbres sous forme de textures
        Texture textureTree = _assetManager.loadTexture(_absoluteNameTree);
        
        if(textureTree != null){
        
            Image image = textureTree.getImage();
            int wTree = image.getWidth();
            int hTree = image.getHeight();
            
            // initilisation du treeGrid
            _treeGrid = new short[wTree][hTree];
            
            Type typeTexture = textureTree.getType();
            if(typeTexture == Type.TwoDimensional ){
                ByteBuffer buffer = image.getData(0);
                // positinnement du bufer
                buffer.position(0);
                
                System.out.println(buffer.capacity());
                
                for(int y=0;y<hTree;y++){
                    for(int x=0;x<wTree;x++){
                        int val = ~buffer.get();
                     //  System.out.println(String.format("%x", val));
                       
                        if(val != 0){
                            // la valeur est différente de zéro, il y a donc un arbre
                            short h =  _heighMapGrid[(y * hTree) + x];
                            _treeGrid[x][y] = h;
                            
                        }else{
                            _treeGrid[x][y] = -1;
                        }
                            
                    }
                }
            }
            
        }
    }
    
    private void loaderMap()
    {
        if(_absoluteNameMap == null)
            return;
        
        // chargement de la map sous forme de texture
        Texture textureMap = _assetManager.loadTexture(_absoluteNameMap);
     
        
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
                
                if(image.getFormat() == Format.Luminance8){
                    System.out.println("ok pour le gris");
                 
                    for(int y = 0;y<_heightMap;y++){
                                               
                        for(int x=0;x<_widthMap;x++){
                            //_heighMapGrid[(y * _heightMap) + x] = (short)(buffer.get() & 0xff); // for unsigned byte
                             _heighMapGrid[(y * _heightMap) + x] = (short)~(short)(buffer.get()); // for unsigned byte

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
                prepareGrid(value, x,  y); 
                
               
            }
           
        }
        
      
    }
    
    private void prepareGrid(short value,int x, int z)
    {
        if(value == 0)
            return;
        // 1 = block
        // 2 = block + grass
        for(short y=0;y < value;y++)
        { 
           _gridMap3d[(y*_zWidth)+(z * _heightMap)+x] = 1;
          
        }
        // block grass
        value--;
         _gridMap3d[(value*_zWidth)+(z * _heightMap)+x] = 2;
         
        
        
    }

    public short[][] getTreeGrid() {
        return _treeGrid;
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
       return _heightMap;
    }

    @Override
    public int getWidthInTiles() {
        return _widthMap;
    }

    @Override
    public int getHeightInTiles() {
       return 256;
    }

    @Override
    public void pathFinderVisited(int x, int y, int z) {
       System.out.println("visited: " + x + " " + y + " " + z);
    }

    @Override
    public boolean blocked(PathFindingContext context, int tx, int ty, int tz) {
       
        if(_gridMap3d[(ty*_zWidth)+(tz * _heightMap)+tx] != 0) // un bloc
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

        float cost = 1f;
        
        if(Math.abs(context.getSourceY() - ty) > 0.5f){
            // augment le cout lorsque qu'il faut escalader
            cost += 1f;
        }
        
       // if((Math.abs(context.getSourceX() - tx) != 0) && (Math.abs(context.getSourceZ() - tz) != 0) )
         //  cost += 0.4f;
        
       
       return cost;
            
            
    }
    
    
    
    
}
