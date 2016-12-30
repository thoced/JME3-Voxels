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
        
        // clear du list voxels
        _listVoxels.clear();
       
         projectLight(x,y,z,map);
       
        
       
    }
    public void projectShadow(MapLoader map, int px,int py,int pz)
    {
         // on parse les cases adjacentes et on calcul le dot product entre le vecteur dir (lumière - case et
                    // le vecteur dir (case - obstacle)
                    // si le dot product == 1 alors il y a de l'ombre
                    // ensuite on place le node dans le listvoxels
                    
        for(Vector3f v : _listVoxels)
        {
            if(v.x == px && v.y == py && v.z == pz)
                return;
        }
        // ajout du voxel current dans la liste voxel pour éviter un futur test inutile
        _listVoxels.add(new Vector3f(px,py,pz));
        
                    
                     System.out.println("LUMIERE: " + _positionScaled.x + " y : " + _positionScaled.z); 
                     System.out.println("BLOC: " + px + " y : " + pz);
                     
                    // calcul de la distance voxel to light
                    float distVoxelLight = _positionScaled.distance(new Vector3f(px,py,pz));
                     
                    int by = py;
                     for(int bz = pz -1 ; bz <= pz + 1 ; bz ++)
                          {
                            for(int bx = px -1 ; bx <= px + 1 ; bx ++)
                             {
                                 if(bx == px && by == py && bz == pz)
                                     continue;
                                 // calcul de la disance current to light
                                 float distCurrentLight =  _positionScaled.distance(new Vector3f(bx,by,bz));
                                 // pour chaque case, on calcul le vecteur dir entre la lumière et la case
                                 Vector3f vDirLight = _positionScaled.subtract(new Vector3f(bx,by,bz));
                                 vDirLight.normalizeLocal();
                                 // calcul du vecteur dir entre l'obstacle et la case
                                 Vector3f vDirVoxel = new Vector3f(px,py,pz).subtract(new Vector3f(bx,by,bz));
                                 vDirVoxel.normalizeLocal();
                                 // calcul du dot product
                                 float dot = vDirLight.dot(vDirVoxel);
                                 System.out.println("dot : " + dot);
                                 // si le dot est proche de 1 alors on assombri
                                 if((dot > 0.8f) && distCurrentLight >= distVoxelLight)
                                 {
                                     map.getGridLightFactor()[(by * map.getzWidth()) + (bz * map.getHeightMap()) + bx] = 0x00;
                                 // on ajoute dans le listvoxels
                                 _listVoxels.add(new Vector3f(bx,by,bz));
                                 
                                 System.out.println("Obscurite sur x : " + bx + " y : " + bz);
                                 
                                 // position du voxel de recherche - on passe toujours la position du voxel de base
                                 projectShadow(map,px,py,pz);
                                 
                                 }
                                
                                 
                             }
                         }
                    
                    
    }
    
    
    public void projectLight(int x,int y, int z,MapLoader map)
    {
        // si le voxel current a déja été testé, on quitte
        for(Vector3f v : _listVoxels)
        {
            if(v.x == x && v.y == y && v.z == z)
                return;
        }
        // ajout du voxel current dans la liste voxel pour éviter un futur test inutile
        _listVoxels.add(new Vector3f(x,y,z));
        
        int py = y;
        for(int pz = z -1 ; pz <= z + 1 ; pz ++)
        {
            for(int px = x -1 ; px <= x + 1 ; px ++)
            {
                if(px == x && py == y && pz == z)
                    continue;
                
                // si on est sur un voxel plein
                if(map.getGridMap3d()[(py * map.getzWidth()) + (pz * map.getHeightMap()) + px] != 0x00)
                {
                   // appel récursif au projectShadow
                    projectShadow(map,px,py,pz);
                }
                
                // on est pas sur un voxel plein, on calcul le coefficient lightfactor
                Vector3f posCurrent = new Vector3f(px,py,pz);
                byte distance = (byte)Math.ceil(posCurrent.distance(_positionScaled));
                
                            
                if(distance < _radius )
                {
                
                    byte lightFactor = ((byte)(_radius - distance)); 
                    // on place le lightfactor dans le current
                    map.getGridLightFactor()[(py * map.getzWidth()) + (pz * map.getHeightMap()) + px] = lightFactor;

                    // appel recursif
                    
                    projectLight(px,py,pz,map);
                }
                
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
