package pq.rapture.shaders.util;

import org.lwjgl.opengl.GL20;

public class UniformVariable<T> {
    private T object;
    private String name;
    private int programId;

    public UniformVariable(T object, String name, int programId) {
        this.object = object;
        this.name = name;
        this.programId = programId;
    }

    public int getID(){
        return GL20.glGetUniformLocation(programId, name);
    }

    public T getObject() {
        return object;
    }

    public void setObject(T object) {
        this.object = object;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
