#version 150

uniform sampler2D Sampler0;
uniform vec2 ScreenSize;
uniform vec2 ShadowOffset;
uniform float ShadowExpand;
uniform float ShadowFactor;

in vec2 texCoord;
out vec4 fragColor;

void main() {
    vec2 pixel = gl_FragCoord.xy + ShadowOffset;
    vec3 base = texture(Sampler0, texCoord).rgb;
    float edge = min(min(pixel.x, ScreenSize.x - pixel.x), min(pixel.y, ScreenSize.y - pixel.y));
    float shadow = exp(-max(edge, 0.0) / max(ShadowExpand, 0.001)) * 0.6 * ShadowFactor;
    fragColor = vec4(base - vec3(shadow), 1.0);
}
