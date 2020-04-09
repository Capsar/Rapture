#version 120

void main() {

    gl_TexCoord[0] = gl_MiltiTexCoord0;
    gl_Position = gl_ModelViewProjectionMatrix * gl_Vertex;

}
