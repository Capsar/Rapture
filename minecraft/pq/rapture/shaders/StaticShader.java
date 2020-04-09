package pq.rapture.shaders;

import org.lwjgl.util.vector.Matrix4f;
import pq.rapture.shaders.util.UniformVariable;

import java.util.ArrayList;
import java.util.List;

public class StaticShader extends ShaderLoader {

    public static final String VERTEX_FILE = "src/pq/rapture/shaders/vertexShader.glsl";
    public static final String FRAGMENT_FILE = "src/pq/rapture/shaders/vertexShader.glsl";

    private int diffuse_location;

    public StaticShader() {
       super(VERTEX_FILE, FRAGMENT_FILE);
    }



    @Override
    protected void getAllUniformLocations() {
        diffuse_location = super.getUniformLocation("diffuse_location");
    }

    @Override
    protected void bindAttributes() {
        super.bindAttribute(0, "position");
    }

}
