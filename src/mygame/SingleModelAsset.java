/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import com.jme3.asset.AssetManager;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import java.util.HashMap;

/**
 *
 * @author thonon
 */
public class SingleModelAsset
{
    private static SingleModelAsset _instance;

    private HashMap<String,Spatial> listAsset = new HashMap();
    
    private AssetManager m_assetManager;
    
    private SingleModelAsset(){
      
    }

    public AssetManager getassetManager() {
        return m_assetManager;
    }

    public void setassetManager(AssetManager m_assetManager) {
        this.m_assetManager = m_assetManager;
    }

    
    
    public HashMap<String, Spatial> getListAsset() {
        return listAsset;
    }
    
        
    public static SingleModelAsset getInstance(){
        if(_instance != null)
            return _instance;
        
        synchronized(SingleModelAsset.class){
        _instance = new SingleModelAsset();
        }
        
        return _instance;
    } 
}
