// https://learnopengl.com/code_viewer_gh.php?code=src/6.pbr/1.2.lighting_textured/1.2.pbr.vs
#version 450
//https://github.com/NCCA/NormalMapping/blob/main/shaders/NormalMapVert.glsl
// attributes
layout(location = 0) in vec3 a_position;
layout(location = 2) in vec2 a_textCoords0;
layout(location = 4) in vec3 a_normal;
layout(location = 5) in vec3 a_tangent;

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

// uniforms
uniform mat4 u_transform;
uniform vec3 u_camera_position;
uniform mat4 u_camera_combined;
uniform PointLight pointLights[NUM_POINT_LIGHTS];
uniform DirectionalLight directionalLights[NUM_DIRECTIONAL_LIGHTS];

// outputs
out vec3 vertex_to_light[NUM_POINT_LIGHTS];
out vec3 light_direction[NUM_DIRECTIONAL_LIGHTS];
out vec3 unit_vertex_to_camera;
out vec2 uv;

void main()
{
    vec4 vertex_position = u_transform * vec4(a_position, 1.0);
    gl_Position = u_camera_combined * vertex_position;

    //    This one is accurate by taking into account non-uniform scaling, but far more expensinve.
    //    mat3 normal_matrix = mat3(transpose(inverse(u_transform))); // should be uploaded as a uniform.
    //    vec3 T = normalize(vec3(normal_matrix * a_tangent));
    //    vec3 N = normalize(vec3(normal_matrix * a_normal));

    vec3 T = normalize(vec3(u_transform * vec4(a_tangent, 0.0f)));
    vec3 N = normalize(vec3(u_transform * vec4(a_normal, 0.0f)));
    T = normalize(T - dot(T, N) * N); // re-orthogonalize T with respect to N (grahm-schmidt)
    vec3 B = cross(N, T);
    mat3 TBN = mat3(T, B, N);
    mat3 invTBN = transpose(TBN); // TBN is orthogonal therefore inverse(TBN) = transpose(TBN)

    unit_vertex_to_camera = normalize(invTBN * (u_camera_position - vertex_position.xyz));

    for (int i = 0; i < NUM_POINT_LIGHTS; i++) {
        vertex_to_light[i] = invTBN * (pointLights[i].position - vertex_position.xyz);
    }
    for (int i = 0; i < NUM_DIRECTIONAL_LIGHTS; i++) {
        light_direction[i] = normalize(invTBN * -directionalLights[i].direction);
    }

    uv = a_textCoords0;
}
