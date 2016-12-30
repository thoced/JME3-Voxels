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
        
        System.out.println("x : " + x + " y : " + y + " z " + z);
        
        if(x < 0 || y < 0 || z < 0)
            return;
        
       
      for(int pz = z - (int)_radius; pz < z + (int)_radius; pz ++)
      {
          for(int px = x - (int)_radius; px < x + (int)_radius; px ++)
          {
               for(int py = y - (int)_radius; py < y + (int)_radius; py ++)
               {
                   // on place dans une liste les voxel plein pour la creation des ombres
                    if((map.getGridMap3d()[(py * map.getzWidth()) + (pz * map.getHeightMap()) + px] & 0xff00) != 0x0000)
                        _listVoxels.add(new Vector3f((int)px,(int)py,(int)pz));
                    else
                    {
                        // si c'est un voxel vide, on passe a l'éclairage
                        try
                        {
                          map.getGridMap3d()[(py * map.getzWidth()) + (pz * map.getHeightMap()) + px] |= 
                                  computeLightFactor(map.getGridMap3d()[(py * map.getzWidth()) + (pz * map.getHeightMap()) + px],x,y,z,px,py,pz);
                        }
                        catch(ArrayIndexOutOfBoundsException a)
                        {

                        }
                    }
                   
               }
          }
      }
      
      // calcul des ombres, pour chaque voxels, on calcul le vecteur directeur
      for(Vector3f v : _listVoxels)
      {
          // calcul du vecteur direction normalize
         float dist = v.distance(_positionScaled);
         Vector3f dir = (v.subtract(_positionScaled)).normalize();
         // a partir de la position du voxel, on avance avec le vecteur directionnel et on ombre les cases
        // projectShadow(v,dir,(int)3,map);
         
      }
        
       
    }
    
    public void projectShadow(Vector3f posVoxel,Vector3f dir, int nbIteration,MapLoader map)
    {
        for(int i=0;i<nbIteration;i++)
        {
            Vector3f p = posVoxel.add(dir.mult(i));
            // on ombre
            map.getGridMap3d()[((int)p.y * map.getzWidth()) + ((int)p.z * map.getHeightMap()) + (int)p.x] |= 0x0000;
        }
    }
    
    
    public short computeLightFactor(short lightFactor,int x, int y, int z, int px,int py,int pz)
    {
        // formule = _radius - distance
        Vector3f v1 = new Vector3f(px,py,pz);
        Vector3f v2 = new Vector3f(x,y,z);
       //Vector3f sous = v2.subtract(v1);
        float dist = (float)Math.ceil(v1.distance(v2));  
        short val = (short)_radius;
        short d = (short)(val - (short)dist + lightFactor);
        if(d < 0x0000)
            d = 0x0000;
        if(d > val)
            d = val;
        
        if(d > 0x0000)
        {
            int popo = 0;
        }
        
        return (short)(d);
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
