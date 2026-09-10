#version 150

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

float roundedBoxSdf(vec2 p,vec2 b,float r){vec2 q=abs(p)-b+r;return length(max(q,vec2(0.0)))+min(max(q.x,q.y),0.0)-r;}
float aaMask(float d){float aa=max(fwidth(d),0.65);return 1.0-smoothstep(-aa,aa,d);}

void main(){
    vec2 pixel=gl_FragCoord.xy;
    vec2 center=Rect.xy+Rect.zw*0.5;
    vec2 halfSize=Rect.zw*0.5;
    vec2 local=pixel-center;
    float radius=clamp(Radius,0.0,min(halfSize.x,halfSize.y));
    float d=roundedBoxSdf(local,halfSize,radius);
    float inside=aaMask(d);

    vec2 shadowLocal=local-Shadow.zw;
    float shadowD=roundedBoxSdf(shadowLocal,halfSize,radius);
    float shadowWidth=max(2.0,Shadow.x);
    float shadowAlpha=exp(-max(shadowD,0.0)/shadowWidth)*(1.0-inside)*clamp(Shadow.y,0.0,1.0)*clamp(ShadowColor.a,0.0,1.0);

    float edgeDistance=max(-d,0.0);
    float edgeThickness=max(1.0,Refraction.x*0.18);
    float edge=1.0-smoothstep(0.0,edgeThickness,edgeDistance);
    float fresnel=pow(clamp(edge,0.0,1.0),max(0.5,Fresnel.x*0.08));
    fresnel*=clamp(Fresnel.y*0.035,0.0,1.0);

    vec3 body=Tint.rgb;
    float bodyAlpha=clamp(Tint.a,0.0,1.0);
    body+=vec3(fresnel*0.24);
    float depth=1.0-smoothstep(0.0,max(1.0,min(halfSize.x,halfSize.y)),edgeDistance);
    body*=0.90+depth*0.10;

    float topDistance=halfSize.y-local.y;
    float highlightWidth=max(1.0,radius*0.22+Glare.x*0.08);
    float topHighlight=exp(-max(topDistance,0.0)/highlightWidth);
    topHighlight*=smoothstep(-1.0,2.0,local.y-(halfSize.y-highlightWidth*1.5));
    float pulse=0.82+0.18*sin(Time*1.6);
    float directional=0.78+0.22*cos(local.x/max(halfSize.x,1.0)+Time*0.55+Glare.w);
    body+=vec3(topHighlight*pulse*directional*clamp(Glare.z*0.004,0.0,0.75));

    float hover=clamp(HoverFocus.x,0.0,1.0);
    float focus=clamp(HoverFocus.y,0.0,1.0);
    body+=vec3(0.04+hover*0.10)*edge;
    body+=vec3(focus*0.16)*(1.0-smoothstep(0.0,2.0,abs(d)));

    if(Progress>=0.0){
        float progressX=mix(Rect.x,Rect.x+Rect.z,clamp(Progress,0.0,1.0));
        float progressMask=1.0-smoothstep(-1.0,1.0,pixel.x-progressX);
        body=mix(body,ProgressColor.rgb,progressMask*inside*clamp(ProgressColor.a,0.0,1.0));
    }

    float alpha=clamp(shadowAlpha+inside*bodyAlpha,0.0,1.0);
    vec3 color=mix(ShadowColor.rgb,body,inside*bodyAlpha/max(alpha,0.0001));
    fragColor=vec4(color,alpha);
}
