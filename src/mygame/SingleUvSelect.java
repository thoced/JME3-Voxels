/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import com.jme3.math.Vector2f;

/**
 *
 * @author thonon
 */
public class SingleUvSelect {
    
    private static SingleUvSelect instance;
    
    private Vector2f[] UvGrass = new Vector2f[4];
    private Vector2f[] UvBrick = new Vector2f[4];
    
    private SingleUvSelect(){
        
        // GRASS
        UvGrass[0] = new Vector2f(0f,0f);
        UvGrass[1] = new Vector2f(0.25f,0f);
        UvGrass[2] = new Vector2f(0.25f,0.25f);
        UvGrass[3] = new Vector2f(0f,0.25f);
        
        // BRICK
        UvBrick[0] = new Vector2f(0.25f,0f);
        UvBrick[1] = new Vector2f(0.50f,0f);
        UvBrick[2] = new Vector2f(0.50f,0.25f);
        UvBrick[3] = new Vector2f(0.25f,0.25f);
        
    }
    
    public Vector2f getUv(String nameCube,int point){
        
        if((point < 0) || (point > 3))
            return UvGrass[0];
        
        
        
        switch(nameCube){
            case "GRASS": return UvGrass[point];
            
            case "BRICK": return UvBrick[point];
            
            default: return UvGrass[point];
        }
        
    }
    
    public static SingleUvSelect getInstance(){
        
        if(instance != null)
            return instance;
        else{
            instance = new SingleUvSelect();
            return instance;
        }
            
    }
}
