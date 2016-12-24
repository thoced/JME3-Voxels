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
import java.util.List;

/**
 *
 * @author Thonon
 */
public class MapLoader 
{
    
    private String _nameFile = null;
    
    private AssetManager _assetManager;
    
    private int _widthMap;
    private int _heightMap;
    
    // tableau heightmap
    private short[] _heighMapGrid;

    public MapLoader(AssetManager assetManager) 
    {
        _assetManager = assetManager;
        
        loaderMap();
    }
    
    public MapLoader(String nameFile, AssetManager assetManager) 
    {
        _nameFile = nameFile;
     
        _assetManager = assetManager;
        
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
                    
                   
                    
                   
                }
                
                
            }
        }
    }

    public short[] getHeighMapGrid() {
        return _heighMapGrid;
    }

    public int getWidthMap() {
        return _widthMap;
    }

    public int getHeightMap() {
        return _heightMap;
    }
    
    
    
    
}
