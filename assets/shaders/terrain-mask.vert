#version 450

// attributes
layout(location = 0) in vec2 a_position;
layout(location = 1) in vec4 a_color;
layout(location = 2) in vec2 a_textCoords0;

// uniforms
uniform mat4 u_camera_combined;

// outputs
out vec2 uv;

void main() {
    uv = a_textCoords0;
    gl_Position = u_camera_combined * vec4(a_position.x, a_position.y, 0.0, 1.0);
};