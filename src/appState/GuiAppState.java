/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package appState;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.niftygui.NiftyJmeDisplay;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.NiftyMethodInvoker;
import de.lessvoid.nifty.controls.Button;
import de.lessvoid.nifty.controls.button.builder.ButtonBuilder;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author thonon
 */
public class GuiAppState extends AbstractAppState implements ScreenController {
    
    public enum typeButtonAction {ADD,SUB};
    
    private typeButtonAction typeAction;
    
    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
    
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
    
   
    Element niftyElement = nifty.getCurrentScreen().findElementById("buttonAdd");
    niftyElement.getElementInteraction().getPrimary().setOnClickMethod(new NiftyMethodInvoker(nifty, "buttonAdd()", this));
    
    Element niftyElement2 = nifty.getCurrentScreen().findElementById("buttonSub");
    niftyElement2.getElementInteraction().getPrimary().setOnClickMethod(new NiftyMethodInvoker(nifty, "buttonSub()", this));
    
    
    // nifty.fromXml("Interface/helloworld.xml", "start", new MySettingsScreen(data));
    // attach the Nifty display to the gui view port as a processor
      
    app.getGuiViewPort().addProcessor(niftyDisplay);

    }
    
    @Override
    public void update(float tpf) {
        //TODO: implement behavior during runtime
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
        System.out.println("ADD");
    }
    
    public void buttonSub(){
        typeAction = typeButtonAction.SUB;
         System.out.println("SUB");
    }
    
}
