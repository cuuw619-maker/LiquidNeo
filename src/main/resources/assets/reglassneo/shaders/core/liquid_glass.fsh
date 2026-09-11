#version 150

// Slot 0: current GUI/color source. Slot 1: captured screen background. Slot 2: glass normal/noise map.
uniform sampler2D Sampler0;
uniform sampler2D Sampler1;
uniform sampler2D Sampler2;
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

float roundedBoxSdf(vec2 p, vec2 b, float r) {
    vec2 q = abs(p) - b + r;
    return length(max(q, vec2(0.0))) + min(max(q.x, q.y), 0.0) - r;
}

float aaMask(float d) {
    float aa = max(fwidth(d), 0.65);
    return 1.0 - smoothstep(-aa, aa, d);
}

vec3 blurHorizontal(vec2 uv, vec2 texel, float radius) {
    vec2 r = vec2(texel.x * radius, 0.0);
    vec3 c = texture(Sampler1, uv).rgb * 0.30;
    c += texture(Sampler1, uv + r).rgb * 0.20;
    c += texture(Sampler1, uv - r).rgb * 0.20;
    c += texture(Sampler1, uv + r * 2.0).rgb * 0.15;
    c += texture(Sampler1, uv - r * 2.0).rgb * 0.15;
    return c;
}

// Second separable pass. Nine effective taps keep the shader practical on Adreno.
vec3 blurFrosted(vec2 uv, vec2 texel, float radius) {
    vec3 c = blurHorizontal(uv, texel, radius) * 0.58;
    vec2 r = vec2(0.0, texel.y * radius);
    c += texture(Sampler1, uv + r).rgb * 0.105;
    c += texture(Sampler1, uv - r).rgb * 0.105;
    c += texture(Sampler1, uv + r * 2.0).rgb * 0.105;
    c += texture(Sampler1, uv - r * 2.0).rgb * 0.105;
    return c;
}

void main() {
    vec2 pixel = gl_FragCoord.xy;
    vec2 center = Rect.xy + Rect.zw * 0.5;
    vec2 halfSize = Rect.zw * 0.5;
    vec2 local = pixel - center;
    float radius = clamp(Radius, 0.0, min(halfSize.x, halfSize.y));
    float d = roundedBoxSdf(local, halfSize, radius);
    float inside = aaMask(d);

    vec2 shadowLocal = local - Shadow.zw;
    float shadowD = roundedBoxSdf(shadowLocal, halfSize, radius);
    float shadowWidth = max(2.0, Shadow.x);
    float shadowAlpha = exp(-max(shadowD, 0.0) / shadowWidth)
        * (1.0 - inside) * clamp(Shadow.y, 0.0, 1.0)
        * clamp(ShadowColor.a, 0.0, 1.0);

    vec2 uv = clamp(pixel / ScreenSize, vec2(0.001), vec2(0.999));
    vec2 texel = 1.0 / ScreenSize;
    float blurRadius = clamp(1.35 + Refraction.y * 1.25, 1.5, 4.5);

    // Frost is evaluated from the captured screen texture and is strictly masked by SDF.
    vec3 background = blurFrosted(uv, texel, blurRadius);
    background *= inside;

    // Sampler2 is a tileable tangent-space noise/normal map. Sampling it over time makes
    // the edge refraction fluid without a per-fragment procedural noise loop.
    vec2 noiseUv = fract(uv * 5.0 + vec2(Time * 0.008, -Time * 0.005));
    vec2 normalMap = texture(Sampler2, noiseUv).rg * 2.0 - 1.0;
    vec2 radial = normalize(vec2(
        local.x / max(halfSize.x, 1.0),
        local.y / max(halfSize.y, 1.0)
    ) + vec2(0.0001));

    float edgeDistance = max(-d, 0.0);
    float edgeThickness = max(1.0, Refraction.x * 0.18);
    float edge = 1.0 - smoothstep(0.0, edgeThickness, edgeDistance);
    float edgeCurve = 1.0 - smoothstep(0.0, max(1.0, radius), edgeDistance);

    vec2 distortion = radial * texel * Refraction.z * 1.65 * edge * edgeCurve;
    distortion += normalMap * texel * Refraction.z * 1.15 * edge;
    distortion += vec2(-radial.y, radial.x) * texel * Refraction.z * 0.42 * edge;

    vec2 refractUv = clamp(uv + distortion, vec2(0.001), vec2(0.999));
    vec2 refractUvInv = clamp(uv - distortion, vec2(0.001), vec2(0.999));
    vec3 refracted = vec3(
        texture(Sampler1, refractUv).r,
        texture(Sampler1, uv).g,
        texture(Sampler1, refractUvInv).b
    );
    background = mix(background, refracted, clamp(0.16 + Refraction.z * 0.032, 0.0, 0.46) * edge);

    // Keep Sampler0 alive and use it as the color carrier for subtle material response.
    vec3 sourceColor = texture(Sampler0, uv).rgb;
    background = mix(background, sourceColor, 0.025 * inside);

    float fresnel = pow(clamp(edge, 0.0, 1.0), max(0.5, Fresnel.x * 0.08));
    fresnel *= clamp(Fresnel.y * 0.035, 0.0, 1.0);

    // Soft inner volume and a thin upper lip provide the Apple-like depth cue.
    float innerDepth = 1.0 - smoothstep(0.0,
        max(1.0, min(halfSize.x, halfSize.y) * 0.55), edgeDistance);
    float rimA = smoothstep(0.0, max(1.0, Refraction.x * 0.22), edgeDistance);
    float rimB = 1.0 - smoothstep(max(1.0, Refraction.x * 0.22),
        max(2.0, Refraction.x * 0.60), edgeDistance);
    float rim = rimA * rimB;

    vec3 body = mix(background, Tint.rgb, clamp(Tint.a * 0.72, 0.0, 0.78));
    float bodyAlpha = max(0.18, clamp(Tint.a + 0.08, 0.0, 0.92));
    body += vec3(fresnel * 0.24 + rim * 0.08);
    body *= 0.88 + innerDepth * 0.12;

    float topDistance = halfSize.y - local.y;
    float highlightWidth = max(1.0, radius * 0.22 + Glare.x * 0.08);
    float topHighlight = exp(-max(topDistance, 0.0) / highlightWidth);
    topHighlight *= smoothstep(-1.0, 2.0,
        local.y - (halfSize.y - highlightWidth * 1.5));
    float pulse = 0.82 + 0.18 * sin(Time * 1.6);
    float directional = 0.78 + 0.22 * cos(
        local.x / max(halfSize.x, 1.0) + Time * 0.55 + Glare.w);
    body += vec3(topHighlight * pulse * directional
        * clamp(Glare.z * 0.004, 0.0, 0.75));

    float hover = clamp(HoverFocus.x, 0.0, 1.0);
    float focus = clamp(HoverFocus.y, 0.0, 1.0);
    body += vec3(0.035 + hover * 0.10) * edge;
    body += vec3(focus * 0.16) * (1.0 - smoothstep(0.0, 2.0, abs(d)));

    if (Progress >= 0.0) {
        float progressX = mix(Rect.x, Rect.x + Rect.z, clamp(Progress, 0.0, 1.0));
        float progressMask = 1.0 - smoothstep(-1.0, 1.0, pixel.x - progressX);
        body = mix(body, ProgressColor.rgb,
            progressMask * inside * clamp(ProgressColor.a, 0.0, 1.0));
    }

    float alpha = clamp(shadowAlpha + inside * bodyAlpha, 0.0, 1.0);
    vec3 color = mix(ShadowColor.rgb, body,
        inside * bodyAlpha / max(alpha, 0.0001));
    fragColor = vec4(color, alpha);
}
