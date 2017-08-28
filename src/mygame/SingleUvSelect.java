/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import ModelsBloc.BBrick;
import ModelsBloc.BEarth;
import ModelsBloc.BGrass;
import ModelsBloc.BlockBase;
import com.jme3.math.Vector2f;

/**
 *
 * @author thonon
 */
public class SingleUvSelect {
    
    private static SingleUvSelect instance;
   
    private BBrick brick;
    private BGrass grass;
    private BEarth earth;
   
    
    private SingleUvSelect(){
        
        brick = new BBrick();
        grass = new BGrass();
        earth = new BEarth();
        
        
    }
    
    public BlockBase getUv(String nameCube){

        switch(nameCube){
            
            case "EARTH": return earth;
            
            case "BRICK": return brick;
            
            case "GRASS": return grass;
        
            default: return brick;
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
