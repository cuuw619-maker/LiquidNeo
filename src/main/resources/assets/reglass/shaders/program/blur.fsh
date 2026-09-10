#version 150

uniform sampler2D DiffuseSampler;
uniform vec2 ScreenSize;
uniform vec2 Direction;
uniform float Radius;

in vec2 texCoord;
out vec4 fragColor;

float gaussian(float x, float sigma) {
    return exp(-(x * x) / (2.0 * sigma * sigma));
}

void main() {
    float radius = clamp(Radius, 0.0, 32.0);
    float sigma = max(radius * 0.5, 0.75);
    vec4 sum = vec4(0.0);
    float total = 0.0;
    for (int i = -32; i <= 32; ++i) {
        float fi = float(i);
        if (abs(fi) > radius) continue;
        float w = gaussian(fi, sigma);
        sum += texture(DiffuseSampler, texCoord + Direction * fi / ScreenSize) * w;
        total += w;
    }
    fragColor = sum / max(total, 0.0001);
}
