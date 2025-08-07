#version 450

#define PI 3.1415926538
#define NUM_POINT_LIGHTS 2
#define NUM_DIRECTIONAL_LIGHTS 2

// structs defitions
struct PointLight {
    vec3 position;
    vec3 color;
    float intensity;
};

struct DirectionalLight {
    vec3 direction;
    vec3 color;
    float intensity;
};

// inputs
in vec2 uv;
in vec3 unit_vertex_to_camera;
in vec3 vertex_to_light[NUM_POINT_LIGHTS];
in vec3 light_direction[NUM_DIRECTIONAL_LIGHTS];

// uniforms - lights
uniform PointLight pointLights[NUM_POINT_LIGHTS]; // TODO change to a u_pointLights prefix convension
uniform DirectionalLight directionalLights[NUM_DIRECTIONAL_LIGHTS]; // TODO change to a u_directionalLights prefix convension

// uniforms - PBR material
uniform sampler2D u_texture_diffuse;
uniform sampler2D u_texture_metalness;
uniform sampler2D u_texture_roughness;
uniform sampler2D u_texture_normalMap;
uniform sampler2D u_texture_opacity;
uniform vec4 u_color_diffuse;
uniform float u_prop_metallic;
uniform float u_prop_roughness;
uniform float u_prop_opacity;

// outputs
layout (location = 0) out vec4 out_color_0;
layout (location = 1) out vec4 out_color_1;

// functions
float distribution_GGX(vec3 N, vec3 H, float roughness)
{
    float a = roughness * roughness;
    float a2 = a * a;
    float NdotH = max(dot(N, H), 0.0);
    float NdotH2 = NdotH * NdotH;
    float nom   = a2;
    float denom = (NdotH2 * (a2 - 1.0) + 1.0);
    denom = PI * denom * denom;
    return nom / denom;
}
// ----------------------------------------------------------------------------
float GeometrySchlickGGX(float NdotV, float roughness)
{
    float r = (roughness + 1.0);
    float k = (r * r) / 8.0;
    float nom   = NdotV;
    float denom = NdotV * (1.0 - k) + k;
    return nom / denom;
}
// ----------------------------------------------------------------------------
float geometry_smith(vec3 N, vec3 V, vec3 L, float roughness)
{
    float NdotV = max(dot(N, V), 0.0);
    float NdotL = max(dot(N, L), 0.0);
    float ggx2 = GeometrySchlickGGX(NdotV, roughness);
    float ggx1 = GeometrySchlickGGX(NdotL, roughness);

    return ggx1 * ggx2;
}
// ----------------------------------------------------------------------------
vec3 fresnel_schlick(float cosTheta, vec3 F0)
{
    return F0 + (1.0 - F0) * pow(clamp(1.0 - cosTheta, 0.0, 1.0), 5.0);
}

//https://github.com/NCCA/NormalMapping/blob/main/shaders/NormalMapVert.glsl
void main()
{
    //vec3 albedo = u_color_diffuse.rgb * pow(texture(u_texture_diffuse, uv).rgb, vec3(2.2)); // Not getting good results with this. Too dark.
    // TODO: optimize
    vec3 albedo = pow(u_color_diffuse.rgb, 1.0 / vec3(2.2)) * texture(u_texture_diffuse, uv).rgb; // TODO: remap when the material loads, not on the fragment shader
    float metalness = u_prop_metallic * texture(u_texture_metalness, uv).r;
    float roughness = u_prop_roughness * texture(u_texture_roughness, uv).g;
    float opacity = u_prop_opacity * texture(u_texture_opacity, uv).a;
    vec3 N = normalize(texture(u_texture_normalMap, uv).rgb * 2.0 - 1.0);
    vec3 V = unit_vertex_to_camera;
    vec3 F0 = mix(vec3(0.04), albedo, metalness);

    vec3 Lo = vec3(0.0);

    // summation over all point light sources
    for (int i = 0; i < NUM_POINT_LIGHTS; i++) {
//        float distance_to_light = length(vertex_to_light[i]);
//        float attenuation = 1.0 / (distance_to_light);
//        vec3 radiance = pointLights[i].intensity * pointLights[i].color * attenuation;
//        vec3 L = normalize(vertex_to_light[i]);
//        vec3 H = normalize(V + L);
//        float NDF = distribution_GGX(N, H, roughness);
//        float G = geometry_smith(N, V, L, roughness);
//        vec3 F = fresnel_schlick(max(dot(H, V), 0.0), F0);
//        vec3 numerator = NDF * G * F;
//        float denominator = 4.0 * max(dot(N, V), 0.0) * max(dot(N, L), 0.0) + 0.0001;
//        vec3 specular = numerator / denominator;
//        vec3 kS = F;
//        vec3 kD = vec3(1.0) - kS;
//        kD *= 1.0 - metalness;
//        float NdotL = max(dot(N, L), 0.0);
//        Lo += (kD * albedo / PI + specular) * radiance * NdotL;
    }

    // summation over all directional light sources
    for (int i = 0; i < NUM_DIRECTIONAL_LIGHTS; i++) {
        vec3 radiance = directionalLights[i].intensity * directionalLights[i].color;
        vec3 L = light_direction[i];
        vec3 H = normalize(V + L);
        float NDF = distribution_GGX(N, H, roughness);
        float G = geometry_smith(N, V, L, roughness);
        vec3 F = fresnel_schlick(max(dot(H, V), 0.0), F0);
        vec3 numerator = NDF * G * F;
        float denominator = 4.0 * max(dot(N, V), 0.0) * max(dot(N, L), 0.0) + 0.0001;
        vec3 specular = numerator / denominator;
        vec3 kD = mix(vec3(1.0) - F, vec3(0.0), metalness);
        float NdotL = max(dot(N, L), 0.0);
        Lo += (kD * albedo / PI + specular) * radiance * NdotL;
    }

    // add ambient light
    vec3 color = vec3(0.80) * albedo + Lo; // ambient light + calculated light

    // HDR tonemapping
    //color = color / (color + vec3(1.0)); // TODO: this gives washed out results
    //color = pow(color, vec3(1.0 / 2.2)); // TODO: figure this out. Should you leave the gamma correction?
    out_color_0 = vec4(color, opacity);
    out_color_1 = vec4(0.0,1.0,0.0, 1.0);
    //out_color = vec4(u_prop_metallic, u_prop_metallic, u_prop_metallic, 1.0);
    //out_color = vec4(roughness, roughness, roughness, 1.0);
}

