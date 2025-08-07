#version 450

// uniforms - PBR material
uniform vec4 u_color; // TODO

// outputs
layout (location = 0) out vec4 out_color;

void main()
{
    out_color = vec4(1,0,0,1);
}