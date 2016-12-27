/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import com.jme3.bounding.BoundingSphere;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;

/**
 *
 * @author Thonon
 */
public class LightProbe 
{
    private Vector3f _position;
    
    private float    _radius = 10f;

    public LightProbe(Vector3f position,float radius) {
        this._position = _position;
        this._radius = radius;
    }

    public void prepareIllumination(Node node)
    {
        // d'abord on récupère tous les nodes (chunks) qui peuvent être englobé par l'illumination
        BoundingSphere boundingSphere = new BoundingSphere(_radius,_position);
        
        CollisionResults results = new CollisionResults();
        boundingSphere.collideWith(node, results);
        
        // results contient tous les node collisionnés
        // pour chaque node collisionné, 
        
        for(CollisionResult r : results)
        {
           // pour chaque geometry, on récupère le chunk
           Chunk c = r.getGeometry().getUserData("CHUNK");
           
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
