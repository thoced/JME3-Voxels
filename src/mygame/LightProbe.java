/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import com.jme3.asset.AssetManager;
import com.jme3.bounding.BoundingBox;
import com.jme3.bounding.BoundingSphere;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Ray;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Sphere;
import java.util.ArrayList;
import java.util.Collection;

/**
 *
 * @author Thonon
 */
public class LightProbe 
{
    private Vector3f _position;
    
    private Vector3f _positionScaled;
    
    private Vector3f _positionNormalized;
    
    private float    _radius = 2f;
    
    private Collection<Vector3f> _listVoxels;
    
    private AssetManager _asset;
    
    private Node _root;

    public LightProbe(Vector3f position,float radius) 
    {
       
        
        this._position = position;
        this._positionNormalized = this._position.normalize();
        this._radius = radius;
        _listVoxels = new ArrayList<Vector3f>();
        
        
        // calcul de la position dans le grid3d
        int x = (int)_position.x;
        int y = (int)_position.y;
        int z = (int)_position.z;
        
        // position lightprobe scaled
        _positionScaled = new Vector3f(x,y,z);
        
        
    }

    public void prepareIllumination(MapLoader map)
    {
        // mise à zero de la liste
        _listVoxels.clear();
        
        // calcul de la position dans le grid3d
        int x = (int)_positionScaled.x;
        int y = (int)_positionScaled.y;
        int z = (int)_positionScaled.z;
        
        // on place la lumière maximal à l'endroit du lightprobe
        map.getGridLightFactor()[(y * map.getzWidth()) + (z * map.getHeightMap()) + x] = 0x07;
        
        if(x < 0 || y < 0 || z < 0)
            return;
        
       int py = y;
      for(int pz = z - (int)_radius; pz <= z + (int)_radius; pz ++)
      {
          for(int px = x - (int)_radius; px <= x + (int)_radius; px ++)
          {
             
              // for(int py = y - (int)_radius; py <= y + (int)_radius; py ++)
              // {
                   // on place dans une liste les voxel plein pour la creation des ombres
                    if(map.getGridMap3d()[(py * map.getzWidth()) + (pz * map.getHeightMap()) + px] != 0x00)
                        _listVoxels.add(new Vector3f((int)px,(int)py,(int)pz));
                  
                        // si c'est un voxel vide, on passe a l'éclairage
                        try
                        {
                          map.getGridLightFactor()[(py * map.getzWidth()) + (pz * map.getHeightMap()) + px] = 
                                  computeLightFactor(map.getGridLightFactor()[(py * map.getzWidth()) + (pz * map.getHeightMap()) + px],x,y,z,px,py,pz);
                        }
                        catch(ArrayIndexOutOfBoundsException a)
                        {

                        }
                   
                       
              // }
          }
      }
      
     
      
      // calcul des ombres, pour chaque voxels, on calcul le vecteur directeur
      for(Vector3f v : _listVoxels)
      {
          // calcul du vecteur direction normalize
      
         // a partir de la position du voxel, on avance avec le vecteur directionnel et on ombre les cases
         projectShadow(v,map);
         
      }
        
       
    }
    
   
    
    public void projectShadow(Vector3f p,MapLoader map)
    {
        // pour chaque voxel, on regarde autour et tout ce qui est inférieur à sa valeur de lightfactor est diminué de 1
        // et ensuite appel récursif en positionne le posVoxel à la position du node vérifié
        
         byte lightFactorVoxel = map.getGridLightFactor()[((int)p.y * map.getzWidth()) + ((int)p.z * map.getHeightMap()) + (int)p.x];
         
         float distVoxelToLight = p.distance(_positionScaled);
         
        int y = (int)p.y;
        for(int z=(int)p.z-1 ; z <= (int)p.z + 1;z++)
        {
            for(int x=(int)p.x-1 ; x <= (int)p.x + 1;x++)
            {
                //for(int y=(int)p.y-1 ; y <= (int)p.y + 1;y++)
                //{
                    if(x == (int)p.x && y == (int)p.y && z == (int)p.z)
                        continue;
                   
                   Vector3f pCurrent = new Vector3f(x,y,z);
                    // calcul de la distance current node to light
                   float distCurrentToLight =pCurrent.distance(_positionScaled);
                   // si la distance current to light est plus grand que la distance voxel to light
                   if(distCurrentToLight > distVoxelToLight)
                   {
                       // calcul du vecteur dir entre le current node et la lumière
                       Vector3f vDirCurrentToLight = (_positionScaled.subtract(pCurrent)).normalize();
                       // calcul du vecteur di entre le voxel et la lumière
                       Vector3f vDirVoxelToLight = (_positionScaled.subtract(p)).normalize();
                       // calcul dot product
                       float dot = vDirCurrentToLight.dot(vDirVoxelToLight);
                       // si le dot vaut 1 alors il y a sombrage
                       if(dot > 0.9f)
                       {
                           // ombrage
                           map.getGridLightFactor()[((int)y * map.getzWidth()) + ((int)z* map.getHeightMap()) + (int)x] = 0x01;
                           // ajout dans le listvoxel
                           _listVoxels.add(pCurrent);
                       }
                   }
                   
                   

                    
                //}
            }
            
        }
        
        
    }
    
    
    public byte computeLightFactor(byte currentlightFactor,int x, int y, int z, int px,int py,int pz)
    {
        // formule = _radius - distance
        Vector3f v1 = new Vector3f(px,py,pz);
        Vector3f v2 = new Vector3f(x,y,z);
       //Vector3f sous = v2.subtract(v1);
        float dist = (float)Math.ceil(v1.distance(v2));  
        byte val = (byte)_radius;
        byte d = (byte)(currentlightFactor + (val - dist));
      
         d = (byte)(val - dist + currentlightFactor);
         
          if(d < currentlightFactor)
            d = currentlightFactor;
            
        return (byte)(d);
    }
     
    
    public Vector3f getPosition() {
        return _position;
    }

    public void setPosition(Vector3f _position) {
        this._position = _position;
    }

    public float getRadius() {
        return _radius;
    }

    public void setRadius(float _radius) {
        this._radius = _radius;
    }

   
    
    
    
    
}
