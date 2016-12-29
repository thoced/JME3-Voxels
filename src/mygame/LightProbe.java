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

/**
 *
 * @author Thonon
 */
public class LightProbe 
{
    private Vector3f _position;
    
    private Vector3f _positionNormalized;
    
    private float    _radius = 2f;

    public LightProbe(Vector3f position,float radius) 
    {
        this._position = position;
        this._positionNormalized = this._position.normalize();
        this._radius = radius;
        
        
    }

    public void prepareIllumination(MapLoader map)
    {
        // calcul de la position dans le grid3d
        int x = (int)_position.x;
        int y = (int)_position.y;
        int z = (int)_position.z;

        System.out.println("x : " + x + " y : " + y + " z " + z);
        
        if(x < 0 || y < 0 || z < 0)
            return;
        
       
        
       /// up
        for(int p=y;p<y+_radius;p++)
        {
            if(p<0 || p > map.getHeightMap() - 1)
                break;
            
            // si le voxel est vide
            if((map.getGridMap3d()[(p * map.getzWidth()) + (z * map.getHeightMap()) + x] & 0xff00) == 0x0000)
            {
               // on modifie le lightfactor
                map.getGridMap3d()[(p * map.getzWidth()) + (z * map.getHeightMap()) + x] |= 0x0002;
                
            }
            else
               break; // le voxel est remplis, on arrete la projection de lumière
        }
        
        // down
        for(int p=y;p>y-_radius;p--)
        {
              if(p<0 || p > map.getHeightMap() - 1)
                break;
            
            // si le voxel est vide
            if((map.getGridMap3d()[(p * map.getzWidth()) + (z * map.getHeightMap()) + x] & 0xff00) == 0x0000)
            {
               // on modifie le lightfactor
                map.getGridMap3d()[(p * map.getzWidth()) + (z * map.getHeightMap()) + x] |= 0x0002;
                
            }
            else
               break; // le voxel est remplis, on arrete la projection de lumière
        }
        
         // right
        for(int p=x;p < x + _radius;p++)
        {
             if(p<0)
                break;
            
            // si le voxel est vide
            if((map.getGridMap3d()[(y * map.getzWidth()) + (z * map.getHeightMap()) + p] & 0xff00) == 0x0000)
            {
               // on modifie le lightfactor
                map.getGridMap3d()[(y * map.getzWidth()) + (z * map.getHeightMap()) + p] |= 0x0002;
                
            }
            else
               break; // le voxel est remplis, on arrete la projection de lumière
        }
        
         // left
        for(int p=x;p > x - _radius;p--)
        {
             if(p<0)
                break;
             
            // si le voxel est vide
            if((map.getGridMap3d()[(y * map.getzWidth()) + (z * map.getHeightMap()) + p] & 0xff00) == 0x0000)
            {
               // on modifie le lightfactor
                map.getGridMap3d()[(y * map.getzWidth()) + (z * map.getHeightMap()) + p] |= 0x0002;
                
            }
            else
               break; // le voxel est remplis, on arrete la projection de lumière
        }
        
         // front
        for(int p=z;p < z + _radius;p++)
        {
             if(p<0)
                break;
            
            // si le voxel est vide
            if((map.getGridMap3d()[(y * map.getzWidth()) + (p * map.getHeightMap()) + x] & 0xff00) == 0x0000)
            {
               // on modifie le lightfactor
                map.getGridMap3d()[(y * map.getzWidth()) + (p * map.getHeightMap()) + x] |= 0x0002;
                
            }
            else
               break; // le voxel est remplis, on arrete la projection de lumière
        }
        
         // back
        for(int p=z;p > z - _radius;p--)
        {
             if(p<0)
                break;
            
            // si le voxel est vide
            if((map.getGridMap3d()[(y * map.getzWidth()) + (p * map.getHeightMap()) + x] & 0xff00) == 0x0000)
            {
               // on modifie le lightfactor
                map.getGridMap3d()[(y * map.getzWidth()) + (p * map.getHeightMap()) + x] |= 0x0002;
                
            }
            else
               break; // le voxel est remplis, on arrete la projection de lumière
        }
              
        
       
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
