/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import com.jme3.math.Vector3f;

/**
 *
 * @author Thonon
 */
public class Voxel 
{
    private Vector3f _localPosition;
    
    private TypeVoxel _typeVoxel;
    
    public enum TypeVoxel
    {
        BLOC01,
        BLOC02,
    }
    
    public Voxel(Vector3f localPosition,TypeVoxel typeVoxel) 
    {
        _localPosition = localPosition;
        _typeVoxel = typeVoxel;
    }

    public Vector3f getLocalPosition() {
        return _localPosition;
    }

    public void setLocalPosition(Vector3f _localPosition) {
        this._localPosition = _localPosition;
    }

    public TypeVoxel getTypeVoxel() {
        return _typeVoxel;
    }

    public void setTypeVoxel(TypeVoxel _typeVoxel) {
        this._typeVoxel = _typeVoxel;
    }
    
    
}
