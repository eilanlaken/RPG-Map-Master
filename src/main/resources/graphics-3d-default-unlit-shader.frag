#version 450

// inputs
in vec2 uv;

// uniforms - PBR material
uniform sampler2D u_texture_diffuse;
uniform sampler2D u_texture_opacity;
uniform vec4 u_color_diffuse;
uniform float u_prop_opacity;

// outputs
layout (location = 0) out vec4 out_color;

void main()
{
    vec4 albedo = u_color_diffuse * texture(u_texture_diffuse, uv);
    float opacity = albedo.a * u_prop_opacity * texture(u_texture_opacity, uv).r;
    out_color = vec4(albedo.rgb, opacity);
}