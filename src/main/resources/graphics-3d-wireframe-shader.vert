// https://learnopengl.com/code_viewer_gh.php?code=src/6.pbr/1.2.lighting_textured/1.2.pbr.vs
#version 450
precision highp float;

// attributes
layout(location = 0) in vec3 a_position;

// uniforms
uniform mat4 u_transform;
uniform mat4 u_camera_combined;

void main()
{
    gl_Position = u_camera_combined * u_transform * vec4(a_position, 1.0);
}
