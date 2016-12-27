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
    
    private float    _radius = 10f;

    public LightProbe(Vector3f position,float radius) {
        this._position = position;
        this._radius = radius;
    }

    public void prepareIllumination(Node node)
    {
        // d'abord on récupère tous les nodes (chunks) qui peuvent être englobé par l'illumination
        BoundingSphere boundingSphere = new BoundingSphere(_radius,_position);
        
        CollisionResults results = new CollisionResults();
       
        
       
        for(Spatial s : node.getChildren())
        {
            s.getWorldBound().collideWith(boundingSphere, results);
        
        // results contient tous les node collisionnés
        // pour chaque node collisionné, 
        CollisionResults resultsCollisions = new CollisionResults();
        
        for(CollisionResult r : results)
        {
            System.out.println("01");
           // pour chaque geometry, on récupère le chunk
           if(r.getGeometry() != null)
           {
           Chunk c = r.getGeometry().getUserData("CHUNK");
           // pour chaque chunk, on récupère le positino buffer
           Vector3f[] pb = c.getPositionBuffer();
           ColorRGBA[] cb = c.getColorBuffer();
           // pour chaque pb, on regarde si il se trouve dans le boudingbox
           
           System.out.println(pb.length);
           for(int i=0;i<pb.length;i++)
           {
               
               if(boundingSphere.contains(pb[i]))
               {
                   // si le point est dans les phere, on lance un rayon vers la position du lightprobe pour si il n'y a pas de collision
                   Ray ray = new Ray(pb[i],_position.subtract(pb[i]).normalizeLocal());
                   resultsCollisions.clear();
                   node.collideWith(ray, resultsCollisions);
                   if(resultsCollisions.size() > 0)
                   {
                       // il y a une collision donc pas de modification de la lumière
                       continue;
                   }
                   else
                   {
                      // il n'y a pas de collision donc le vertexColor est influencé par la lumière
                      cb[i].add(ColorRGBA.White);
                   }
               }
           }
           
           
           //update du mesh
           c.updateLightProbeColor();
           }
           
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
