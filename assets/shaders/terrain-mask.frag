#version 450

// inputs
in vec2 uv;

// uniforms
uniform sampler2D u_texture;
uniform sampler2D u_texture_steepness;
uniform sampler2D u_texture_mask;
// TODO: add blendmap
// TODO: add steepness texture


// outputs
layout (location = 0) out vec4 out_color;

void main() {
    //vec4 color = texture(u_texture, uv);

    vec4 tex1 = texture(u_texture, uv);
    vec4 tex2 = texture(u_texture_steepness, uv) * tex1;
    float alpha = texture(u_texture_mask, uv).r;

    float bias = 0.3; // >1 favors u_texture, <1 favors u_steepness
    float t = alpha / ((1.0 / bias - 2.0) * (1.0 - alpha) + 1.0);
    vec4 color = mix(tex2, tex1, t);

    out_color = vec4(color.rgb, alpha);
}