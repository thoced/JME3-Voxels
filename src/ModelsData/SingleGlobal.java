/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ModelsData;

/**
 *
 * @author thonon
 */
public class SingleGlobal {
    
    private static SingleGlobal m_instance;
    
    // GLOBAL VAR
    // GameMode
    public enum Mode {BUILD,SELECT,ENTITY};
    private Mode gameMode;
    // Deplacement
    public enum ModeCamera {TRANSLATION,ROTATION,ZOOM};
    private ModeCamera cameraMode;
    // Placement d'objet
    private String nameEntityToBePlaced;
    
    // PathFinder
    
    
    
    private SingleGlobal(){
        gameMode = Mode.SELECT;
        
    }
    
    public static SingleGlobal getInstance(){
        if(m_instance != null)
            return m_instance;
        else{
            m_instance = new SingleGlobal();
            return m_instance;
        }
            
    }

    public ModeCamera getCameraMode() {
        return cameraMode;
    }

    public void setCameraMode(ModeCamera cameraMode) {
        this.cameraMode = cameraMode;
    }

    public Mode getGameMode() {
        return gameMode;
    }

    public void setGameMode(Mode gameMode) {
        this.gameMode = gameMode;
    }

    public String getNameEntityToBePlaced() {
        return nameEntityToBePlaced;
    }

    public void setNameEntityToBePlaced(String nameEntityToBePlaced) {
        this.nameEntityToBePlaced = nameEntityToBePlaced;
    }
    
    
    
    
    
}
