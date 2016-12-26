package mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.font.BitmapText;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Matrix3f;
import com.jme3.math.Quaternion;
import com.jme3.math.Transform;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Spatial;
import com.jme3.scene.VertexBuffer.Type;
import com.jme3.scene.shape.Box;
import com.jme3.util.BufferUtils;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import jme3tools.optimize.GeometryBatchFactory;

/**
 * This is the Main Class of your Game. You should only do initialization here.
 * Move your Logic into AppStates or Controls
 * @author normenhansen
 */
public class Main extends SimpleApplication {

    private DirectionalLight directionalLight;
    private Vector3f[] dirLight;
    
    private double angle = 0f;
    
    public static void main(String[] args) {
        Main app = new Main();
        app.start();
    }

    @Override
    public void simpleInitApp() {
       
        this.getFlyByCamera().setMoveSpeed(64.0f);
        
        // mesh 01
        Mesh mesh = new Mesh();
        Vector3f [] vertices = new Vector3f[4];
        vertices[0] = new Vector3f(0,0,0);
        vertices[1] = new Vector3f(3,0,0);
        vertices[2] = new Vector3f(0,3,0);
        vertices[3] = new Vector3f(3,3,0);
        // texture coord
        Vector2f[] texCoord = new Vector2f[4];
        texCoord[0] = new Vector2f(0,0);
        texCoord[1] = new Vector2f(1,0);
        texCoord[2] = new Vector2f(0,1);
        texCoord[3] = new Vector2f(1,1);
        // indices
        int [] indexes = { 2,0,1, 1,3,2 };
        // create meshbuffer
        mesh.setBuffer(Type.Position, 3, BufferUtils.createFloatBuffer(vertices));
        mesh.setBuffer(Type.TexCoord, 2, BufferUtils.createFloatBuffer(texCoord));
        mesh.setBuffer(Type.Index,    3, BufferUtils.createIntBuffer(indexes));
        mesh.updateBound();
        
     
        // chargement de la map
        MapLoader map = new MapLoader("Textures/map01/map07.png",this.assetManager);
       
        
        // instance du chunkmanager
        ChunkManager chunkManager = new ChunkManager(map);
        
        // Création d'un materel
        Material mat = new Material(assetManager,"Common/MatDefs/Light/Lighting.j3md");
        mat.setColor("Diffuse", new ColorRGBA(128,128,128,255));
        
        mat.setTexture("DiffuseMap",
        assetManager.loadTexture("Textures/Textures/rock.jpg"));
        
        mat.setTexture("NormalMap", 
                assetManager.loadTexture("Textures/Textures/rock_n.jpg"));
        
        mat.setFloat("Shininess", 64f);  // [0,128]
      //  mat.setTexture("SpecularMap", assetManager.loadTexture("Textures/Textures/grass_specular.png"));
        //mat.setColor("Specular",ColorRGBA.White);
        mat.getAdditionalRenderState().setFaceCullMode(RenderState.FaceCullMode.Back);
      
        
        
        // ajout des mesh dans le scenegraph
       Collection<Chunk> lc = chunkManager.getListChunks();
       for(Chunk c : lc)
       {
           // pour chaque chunk, on récupère le mesh
           Mesh m = c.getMeshChunk();
           // pour chaque mesh on créer une geometrie
           Geometry geo = new Geometry("chunk",m);
  
          // geo.setLocalTranslation(c.getWorldPosition().x, 0, c.getWorldPosition().y);
           geo.setMaterial(mat);
           rootNode.attachChild(geo);
           
          
           
       }
       
       // Light
       AmbientLight ambientLight = new AmbientLight();
       ambientLight.setColor(new ColorRGBA((1f/255f)*157f,1f,(1f/255f)*242f,1f));
       rootNode.addLight(ambientLight);
       
       directionalLight = new DirectionalLight();
       directionalLight.setColor(new ColorRGBA(1,1,1,1));
       directionalLight.setDirection(new Vector3f(0.5f,-0.5f,-0.5f).normalizeLocal());
       rootNode.addLight(directionalLight);
  
       
        
    }

    @Override
    public void simpleUpdate(float tpf)
    {
          
        
              
   
    }

    @Override
    public void simpleRender(RenderManager rm) {
        //TODO: add render code
    }
}
