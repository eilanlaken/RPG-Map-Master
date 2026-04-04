#version 450

// inputs
in vec4 color;
in vec2 uv;


uniform sampler2D u_texture_ground_base;
uniform sampler2D u_texture_ground;

uniform sampler2D u_texture_liquid_base;
uniform sampler2D u_texture_liquid;

uniform sampler2D u_texture; // blend map
uniform float u_blendmap_width;
uniform float u_blendmap_height;
uniform sampler2D u_texture_steepness;

uniform float u_width_groundBase;
uniform float u_height_groundBase;
uniform float u_width_liquidBase;
uniform float u_height_liquidBase;

// outputs
layout (location = 0) out vec4 out_color;

void main() {
    vec2 revealUV_ground = gl_FragCoord.xy / vec2(u_width_groundBase, u_height_groundBase);
    vec2 revealUV_liquid = gl_FragCoord.xy / vec2(u_width_liquidBase, u_height_liquidBase);

    // ground
    vec4 groundBase = texture(u_texture_ground_base, revealUV_ground);
    vec4 ground     = texture(u_texture_ground, uv);
    vec3 groundColor = mix(groundBase.rgb, ground.rgb, ground.a);

    // liquid
    vec4 liquidBase = texture(u_texture_liquid_base, revealUV_liquid);
    vec4 liquid     = texture(u_texture_liquid, uv);
    vec3 liquidColor = mix(liquidBase.rgb, liquid.rgb, liquid.a);

    // steepness
    vec2 blend_texel_size = vec2(1.0 / u_blendmap_width, 1.0 / u_blendmap_height);
    float cL = texture(u_texture, uv + vec2(-blend_texel_size.x, 0)).r;
    float cR = texture(u_texture, uv + vec2( blend_texel_size.x, 0)).r;
    float cD = texture(u_texture, uv + vec2(0, -blend_texel_size.y)).r;
    float cU = texture(u_texture, uv + vec2(0,  blend_texel_size.y)).r;
    float dx = cR - cL;
    float dy = cU - cD;
    float steepnessFactor = clamp(length(vec2(dx, dy)), 0.0, 1.0);
    vec3 steepnessColor = texture(u_texture_steepness, revealUV_ground).rgb;


    float biasedSteepness = pow(steepnessFactor, 0.5);
    groundColor = mix(groundColor, steepnessColor, biasedSteepness); // gradient based version

    float blendFactor = texture(u_texture, uv).r;
    vec3 mapColor = mix(liquidColor, groundColor, blendFactor);
    out_color = vec4(mapColor, 1.0);
}

// uniforms
//uniform sampler2D u_texture_0;
//uniform sampler2D u_texture_map_0;
//uniform sampler2D u_texture_1;
//uniform sampler2D u_texture_map_1;
//uniform sampler2D u_texture;
//uniform sampler2D u_texture_steepness;
//uniform sampler2D u_texture_mask;


/*
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
*/