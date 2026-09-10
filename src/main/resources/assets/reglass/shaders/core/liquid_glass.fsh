#version 150

uniform sampler2D SceneSampler;
uniform sampler2D BlurSampler;
uniform vec2 ScreenSize;
uniform vec4 Rect;
uniform float Radius;
uniform vec4 Tint;
uniform vec4 Refraction;
uniform vec4 Fresnel;
uniform vec4 Glare;
uniform vec4 Shadow;
uniform vec4 ShadowColor;
uniform vec2 HoverFocus;
uniform float Time;
uniform float Progress;
uniform vec4 ProgressColor;

in vec2 texCoord;
out vec4 fragColor;

float roundedBox(vec2 p, vec2 b, float r) {
    vec2 q = abs(p) - b + r;
    return length(max(q, 0.0)) + min(max(q.x, q.y), 0.0) - r;
}

float roundedBoxRing(vec2 p, vec2 b, float r, float width) {
    float d = roundedBox(p, b, r);
    return 1.0 - smoothstep(0.0, max(width, 0.001), abs(d));
}

vec3 sampleChromatic(vec2 uv, vec2 offset, float dispersion) {
    float d = clamp(dispersion, 0.0, 32.0) * 0.00035;
    vec2 tangent = normalize(vec2(-offset.y, offset.x) + vec2(0.000001));
    float ar = ScreenSize.y / max(ScreenSize.x, 1.0);
    vec2 chroma = tangent * d * vec2(ar, 1.0);
    return vec3(
        texture(BlurSampler, uv + offset + chroma).r,
        texture(BlurSampler, uv + offset).g,
        texture(BlurSampler, uv + offset - chroma).b
    );
}

void main() {
    vec2 pixel = gl_FragCoord.xy;
    vec2 center = vec2(Rect.x + Rect.z * 0.5, Rect.y + Rect.w * 0.5);
    vec2 halfSize = Rect.zw * 0.5;
    vec2 local = pixel - center;
    float radius = clamp(Radius, 0.0, min(halfSize.x, halfSize.y));
    float d = roundedBox(local, halfSize, radius);

    float aa = max(fwidth(d), 0.75);
    float inside = 1.0 - smoothstep(-aa, aa, d);

    vec4 scene = texture(SceneSampler, texCoord);
    vec2 normal = normalize(local / max(halfSize, vec2(0.001)));
    float edge = exp(-max(-d, 0.0) / max(Refraction.x, 0.001));
    float refractive = clamp(Refraction.y, 0.0, 4.0);
    float offsetAmount = edge * refractive * 0.012;
    vec2 uvOffset = normal * offsetAmount * vec2(ScreenSize.y / max(ScreenSize.x, 1.0), 1.0);

    vec3 refracted = sampleChromatic(texCoord, uvOffset, Refraction.z);
    float fres = pow(clamp(1.0 - max(dot(normal, vec2(0.707, 0.707)), 0.0), 0.0, 1.0), 2.0);
    fres *= clamp(Fresnel.y / 100.0, 0.0, 2.0);

    vec3 color = mix(refracted, Tint.rgb, clamp(Tint.a, 0.0, 1.0));
    color = mix(color, vec3(1.0), fres * clamp(Fresnel.x / 100.0, 0.0, 1.0));

    float glare = pow(max(0.0, 1.0 - abs(d) / max(Glare.x, 0.001)), max(0.5, Glare.y / 20.0));
    float angle = Glare.w;
    vec2 lightDir = vec2(cos(angle), sin(angle));
    glare *= pow(max(dot(normal, lightDir), 0.0), 2.0);
    color += vec3(glare) * clamp(Glare.z / 100.0, 0.0, 1.0) * 0.35;

    float hover = clamp(HoverFocus.x, 0.0, 1.0);
    float focus = clamp(HoverFocus.y, 0.0, 1.0);
    color += vec3(1.0) * (0.05 + 0.18 * hover) * exp(-abs(d) / 8.0);
    color += vec3(1.0) * focus * 0.12 * roundedBoxRing(local, halfSize, radius, 2.0);

    if (Progress >= 0.0) {
        float fillX = mix(Rect.x, Rect.x + Rect.z, clamp(Progress, 0.0, 1.0));
        float fillMask = 1.0 - smoothstep(-aa, aa, max(d, pixel.x - fillX));
        color = mix(color, ProgressColor.rgb, fillMask * ProgressColor.a);
    }

    float shadowExpand = max(Shadow.x, 0.001);
    vec2 shadowLocal = local - Shadow.zw;
    float sd = roundedBox(shadowLocal, halfSize, radius);
    float shadowAlpha = exp(-max(sd, 0.0) / shadowExpand) * Shadow.y * ShadowColor.a;
    float shadowOnly = (1.0 - inside) * shadowAlpha;
    vec3 composited = mix(scene.rgb, ShadowColor.rgb, clamp(shadowOnly, 0.0, 1.0));
    composited = mix(composited, color, inside);

    fragColor = vec4(composited, 1.0);
}
