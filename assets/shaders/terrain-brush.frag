#version 450

// inputs
in vec4 color;
in vec2 uv;

// uniforms
uniform sampler2D u_texture_reveal; // reveal
uniform sampler2D u_texture; // brush
uniform float u_width;
uniform float u_height;

// outputs
layout (location = 0) out vec4 out_color;

void main() {
    float mask = texture(u_texture, uv).a;
    vec2 revealUV = gl_FragCoord.xy / vec2(u_width, u_height);
    vec3 reveal = texture(u_texture_reveal, revealUV).rgb;

    out_color = color * vec4(reveal, mask);
    //out_color = vec4(revealUV.x,0,0,1);
}