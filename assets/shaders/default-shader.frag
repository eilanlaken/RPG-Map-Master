#version 450

// inputs
in vec4 color;
in vec2 uv;

// uniforms
uniform sampler2D u_texture;

// outputs
layout (location = 0) out vec4 out_color;

float hue2rgb(float p, float q, float t);
vec3 rgb2hsl(vec3 c);
vec3 hsl2rgb(vec3 hsl);

void main() {
    vec3 u_hsl = color.rgb;

    vec4 tex = texture(u_texture, uv);
    vec3 hsl = rgb2hsl(tex.rgb);

    // apply controls
    hsl.x = fract(hsl.x + u_hsl.x); // hue shift
    hsl.y = clamp(hsl.y * u_hsl.y, 0.0, 1.0); // saturation scale
    hsl.z = clamp(hsl.z * u_hsl.z, 0.0, 1.0); // lightness scale

    vec3 rgb = hsl2rgb(hsl);

    out_color = color * vec4(rgb, tex.a);
}

vec3 rgb2hsl(vec3 c) {
    float maxc = max(max(c.r, c.g), c.b);
    float minc = min(min(c.r, c.g), c.b);
    float l = (maxc + minc) * 0.5;

    float s = 0.0;
    float h = 0.0;

    if (maxc != minc) {
        float d = maxc - minc;
        s = l > 0.5 ? d / (2.0 - maxc - minc) : d / (maxc + minc);

        if (maxc == c.r)
        h = (c.g - c.b) / d + (c.g < c.b ? 6.0 : 0.0);
        else if (maxc == c.g)
        h = (c.b - c.r) / d + 2.0;
        else
        h = (c.r - c.g) / d + 4.0;

        h /= 6.0;
    }

    return vec3(h, s, l);
}

float hue2rgb(float p, float q, float t) {
    if (t < 0.0) t += 1.0;
    if (t > 1.0) t -= 1.0;
    if (t < 1.0/6.0) return p + (q - p) * 6.0 * t;
    if (t < 1.0/2.0) return q;
    if (t < 2.0/3.0) return p + (q - p) * (2.0/3.0 - t) * 6.0;
    return p;
}

vec3 hsl2rgb(vec3 hsl) {
    float h = hsl.x;
    float s = hsl.y;
    float l = hsl.z;

    float r, g, b;

    if (s == 0.0) {
        r = l;
        g = l;
        b = l;
    } else {
        float q = l < 0.5 ? l * (1.0 + s) : l + s - l * s;
        float p = 2.0 * l - q;
        r = hue2rgb(p, q, h + 1.0/3.0);
        g = hue2rgb(p, q, h);
        b = hue2rgb(p, q, h - 1.0/3.0);
    }

    return vec3(r, g, b);
}

