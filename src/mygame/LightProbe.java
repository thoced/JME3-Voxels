/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import com.jme3.bounding.BoundingSphere;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Ray;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

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

    public void prepareIllumination(Node node)
    {
        // creation du rayon
        Ray ray = new Ray();
        // creatin du boundingsphere
        BoundingSphere bSphere = new BoundingSphere(_radius,_position);
        //pour chaque node enfants
        for(Spatial s : node.getChildren())
        {
            // si le node est intersect avec la boundingsphere
            if(s.getWorldBound().intersectsSphere(bSphere))
            {
                //récupération des position vertex
                Chunk c = s.getUserData("CHUNK");
                Vector3f[] pb = c.getPositionBuffer();
                Vector3f[] nb = c.getNormalBuffer();
                ColorRGBA[] cb = c.getColorBuffer();
                
                // pour chaque vertex position dans le boundingsphere
                for(int i=0;i<pb.length;i++)
                {
                    // si il est contenu dans le boundingSphere
                    if(bSphere.contains(pb[i]))
                    {
                       // Vector3f dir = pb[i].subtract(_position);
                        Vector3f dir =_position.subtract(pb[i]);
                        dir.normalizeLocal();
                        // test dot product 
                        float dot = nb[i].dot(dir);
                        if(dot <= 0f || dot >=1f)
                            continue;
                        
                        // on lance un rayon entre le pb et la position du lightprobe
                          
                        ray.setOrigin(pb[i]);
                        ray.setDirection(dir);
                        CollisionResults results = new CollisionResults();
                        node.collideWith(ray, results);
                        
                        if(results.size() > 0 && !results.getClosestCollision().getContactPoint().equals(pb[i]))
                        {
                            // il y a une collision, on applique pas de lumière
                        }
                        else
                        {
                            // il n'y a pas de collision, on applique de la lumière
                            cb[i].addLocal(ColorRGBA.White);
                            
                            
                        }
                        
                    }
                }
                
                c.updateLightProbeColor(cb);
            }
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
