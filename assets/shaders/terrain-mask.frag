#version 450

// inputs
in vec2 uv;

// uniforms
uniform sampler2D u_texture_0;
uniform sampler2D u_texture_map_0;
uniform sampler2D u_texture_1;
uniform sampler2D u_texture_map_1;

uniform sampler2D u_texture;
uniform sampler2D u_texture_steepness;
uniform sampler2D u_texture_mask;
// TODO: add blendmap


// outputs
layout (location = 0) out vec4 out_color;

void main() {
    vec4 base = texture(u_texture, uv);
    float alpha = texture(u_texture_mask, uv).r;
    // Additional textures and their blend maps
    vec4 tex0 = texture(u_texture_0, uv);
    float blend0 = texture(u_texture_map_0, uv).r * alpha;

    vec4 tex1 = texture(u_texture_1, uv);
    float blend1 = texture(u_texture_map_1, uv).r * alpha;

    vec4 blended1 = mix(base, tex0, blend0);
    vec4 blended2 = mix(blended1, tex1, blend1);
    vec4 tex2 = texture(u_texture_steepness, uv) * base;

    float bias = 0.3; // >1 favors u_texture, <1 favors u_steepness
    float t = alpha / ((1.0 / bias - 2.0) * (1.0 - alpha) + 1.0);
    vec4 color = mix(tex2, blended2, t);

    out_color = vec4(color.rgb, alpha);
}