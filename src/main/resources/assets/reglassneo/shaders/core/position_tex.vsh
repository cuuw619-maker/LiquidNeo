#version 150

in vec3 Position;
in vec2 UV0;
uniform vec2 ScreenSize;
out vec2 texCoord;

void main() {
    vec2 ndc=vec2(Position.x/ScreenSize.x*2.0-1.0,Position.y/ScreenSize.y*2.0-1.0);
    gl_Position=vec4(ndc,0.0,1.0);
    texCoord=UV0;
}
