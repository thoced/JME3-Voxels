/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package appState;

import ModelsData.SingleGlobal;
import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.input.InputManager;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.niftygui.NiftyJmeDisplay;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Spatial;
import com.jme3.scene.VertexBuffer;
import com.jme3.scene.VertexBuffer.Type;
import com.jme3.scene.shape.Quad;
import com.jme3.texture.Texture;
import com.jme3.util.BufferUtils;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.NiftyMethodInvoker;
import de.lessvoid.nifty.controls.Button;
import de.lessvoid.nifty.controls.button.builder.ButtonBuilder;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;
import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.java.games.input.Mouse;

/**
 *
 * @author thonon
 */
public class GuiAppState extends AbstractAppState implements ActionListener,ScreenController {

   
    
    public enum typeButtonAction {ADD,SUB,ADDBON};
    
    // Référence vers le simpleapplication
    SimpleApplication app;
    // Référence vers l'input manager
    InputManager input;
    
    private typeButtonAction typeAction;
    
    // Quad de sélection
    private Mesh m_selectionQuad;
    private Vector2f m_VectorSelectionStart;
    private boolean  m_isSelection = false;
    private Geometry m_geoSelection;
    
    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
    
        // copie des références
        this.app = (SimpleApplication)app;
        this.input = app.getInputManager();
        
        // instance du NiftyJmeDisplay
        NiftyJmeDisplay niftyDisplay = NiftyJmeDisplay.newNiftyJmeDisplay(
        app.getAssetManager(), app.getInputManager(), app.getAudioRenderer(), app.getGuiViewPort());
         /** Create a new NiftyGUI object */
        Nifty nifty = niftyDisplay.getNifty();
        try {
            /** Read your XML and initialize your custom ScreenController */
            nifty.validateXml("Interface/IUBase.xml");
        } catch (Exception ex) {
            Logger.getLogger(GuiAppState.class.getName()).log(Level.SEVERE, null, ex);
        }
    
        nifty.registerScreenController(this);
        nifty.fromXml("Interface/IUBase.xml", "start");

        // ajout des action liées aux boutons
        Element niftyElement = nifty.getCurrentScreen().findElementById("buttonAdd");
        niftyElement.getElementInteraction().getPrimary().setOnClickMethod(new NiftyMethodInvoker(nifty, "buttonAdd()", this));

        Element niftyElement2 = nifty.getCurrentScreen().findElementById("buttonSub");
        niftyElement2.getElementInteraction().getPrimary().setOnClickMethod(new NiftyMethodInvoker(nifty, "buttonSub()", this));

        Element niftyElement3 = nifty.getCurrentScreen().findElementById("buttonAddBon");
        niftyElement3.getElementInteraction().getPrimary().setOnClickMethod(new NiftyMethodInvoker(nifty, "buttonAddBon()", this));
        
        Element niftyElement4 = nifty.getCurrentScreen().findElementById("buttonAddCamp");
        niftyElement4.getElementInteraction().getPrimary().setOnClickMethod(new NiftyMethodInvoker(nifty, "buttonAddCamp()", this));
        
        Element niftyElement5 = nifty.getCurrentScreen().findElementById("buttonAddPlayer");
        niftyElement5.getElementInteraction().getPrimary().setOnClickMethod(new NiftyMethodInvoker(nifty, "buttonAddPlayer()", this));
       
        Element niftyElement6 = nifty.getCurrentScreen().findElementById("buttonEarthwork");
        niftyElement6.getElementInteraction().getPrimary().setOnClickMethod(new NiftyMethodInvoker(nifty, "buttonEarthwork()", this));
       
        
        // ajout du niftyDisplay au Processeur GuiViewPort  
        app.getGuiViewPort().addProcessor(niftyDisplay);

        // initialisation du systeme de sélection
        m_selectionQuad = new Mesh();
        Collection<Vector3f> vBuff = new ArrayList<Vector3f>();
        vBuff.add(new Vector3f(0,0,0));
        vBuff.add(new Vector3f(1,0,0));
        vBuff.add(new Vector3f(1,1,0));
        vBuff.add(new Vector3f(0,1,0));
        // set buffer
        Vector3f[] vb = vBuff.toArray(new Vector3f[vBuff.size()]);
        m_selectionQuad.setBuffer(Type.Position,3,BufferUtils.createFloatBuffer(vb));
        
        m_selectionQuad.setMode(Mesh.Mode.LineLoop);
        m_geoSelection= new Geometry("selectionQuad",m_selectionQuad);
        Material mat = new Material(app.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.Yellow);
        mat.getAdditionalRenderState().setFaceCullMode(RenderState.FaceCullMode.Off);
        mat.getAdditionalRenderState().setLineWidth(1f);
        m_geoSelection.setMaterial(mat);
        m_geoSelection.setQueueBucket(RenderQueue.Bucket.Gui);
        m_geoSelection.setCullHint(Spatial.CullHint.Always);
        
        this.app.getGuiNode().attachChild(m_geoSelection);

        // initilisation des Input
        this.input.addListener(this, "CLIC_SELECTION");
        this.input.addMapping("CLIC_SELECTION", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
     
       /** An unshaded textured cube. 
 *  Uses texture from jme3-test-data library! */ 

    }

       
    
    @Override
    public void update(float tpf) {
       
       if(m_isSelection){
           m_geoSelection.setLocalTranslation(m_VectorSelectionStart.x, m_VectorSelectionStart.y, 0);
           // reception de la position actuelle du cursor
           Vector2f v = this.input.getCursorPosition().clone();
           Vector2f diff = v.subtract(m_VectorSelectionStart);
           m_geoSelection.setLocalScale(new Vector3f(diff.x,diff.y,0));
       }
        
        
    }
    
    @Override
    public void cleanup() {
        super.cleanup();
        //TODO: clean up what you initialized in the initialize method,
        //e.g. remove all spatials from rootNode
        //this is called on the OpenGL thread after the AppState has been detached
    }

    public typeButtonAction getTypeAction() {
        return typeAction;
    }

    public void setTypeAction(typeButtonAction typeAction) {
        this.typeAction = typeAction;
    }

    
    
    @Override
    public void bind(Nifty nifty, Screen screen) {
        System.out.println("bind( " + screen.getScreenId() + ")");
        
    }

    @Override
    public void onStartScreen() {
     
    }

    @Override
    public void onEndScreen() {
       
    }
    
    public void buttonAdd(){
        typeAction = typeButtonAction.ADD;
        SingleGlobal.getInstance().setNameEntityToBePlaced("");
        SingleGlobal.getInstance().setGameMode(SingleGlobal.Mode.BUILD);
    }
    
    public void buttonSub(){
        typeAction = typeButtonAction.SUB;
        SingleGlobal.getInstance().setNameEntityToBePlaced("");
        SingleGlobal.getInstance().setGameMode(SingleGlobal.Mode.BUILD);
    }
    
    public void buttonAddBon(){
        typeAction = typeButtonAction.ADDBON;
         SingleGlobal.getInstance().setGameMode(SingleGlobal.Mode.SELECT);                                                                             
    }
    
    public void buttonAddCamp(){
        SingleGlobal.getInstance().setNameEntityToBePlaced("CAMPFIRE");
        SingleGlobal.getInstance().setGameMode(SingleGlobal.Mode.ENTITY);
    }
    
    public void buttonAddPlayer(){
        SingleGlobal.getInstance().setNameEntityToBePlaced("PLAYER");
        SingleGlobal.getInstance().setGameMode(SingleGlobal.Mode.ENTITY);
    }
    
    public void buttonEarthwork(){
        SingleGlobal.getInstance().setNameEntityToBePlaced("");
        SingleGlobal.getInstance().setGameMode(SingleGlobal.Mode.EARTHWORK);
    }
            
    
    @Override
    public void onAction(String name, boolean isPressed, float tpf) {
        
        if(SingleGlobal.getInstance().getGameMode() == SingleGlobal.Mode.SELECT){
        
            if(name.equals("CLIC_SELECTION")){
                if(isPressed){
                    // début de crétion du rectangle de sélection
                    m_isSelection = true;
                    m_geoSelection.setCullHint(Spatial.CullHint.Never);
                    m_VectorSelectionStart = this.input.getCursorPosition().clone();
                }
                else{
                    // fin du rectangle
                    m_isSelection = false;
                    m_geoSelection.setCullHint(Spatial.CullHint.Always);
                }
            }
        }
    }

      
    
}
