#ifdef GL_ES
#define LOWP lowp
precision mediump float;
#else
#define LOWP
#endif

const int MAX_LIGHTS = 8;

struct Light {
    vec3 position;   // light position, normalized
    LOWP vec4 color; // light RGBA -- alpha is intensity
    vec3 falloff;
};

varying LOWP vec4 v_color;
varying vec2 v_texCoords;

uniform sampler2D u_texture;
uniform sampler2D u_normalMap;
uniform sampler2D u_heightMap;

uniform vec2 resolution;        // resolution of screen
uniform LOWP vec4 ambientLight; // alpha is intensity

uniform Light lights[MAX_LIGHTS];

void main() {
    vec4 texture = texture2D(u_texture, v_texCoords);
    vec3 normal = texture2D(u_normalMap, v_texCoords).rgb;
    vec3 ambient = ambientLight.rgb * ambientLight.a;
    vec3 sumLight = texture.rgb * ambient; // Start with the ambient light
    for (int i = 0; i < MAX_LIGHTS; i++) {
        if (lights[i].color.a > 0.0) {
            vec3 delta = vec3(lights[i].position.xy - (gl_FragCoord.xy / resolution.xy), lights[i].position.z);
            delta.x *= resolution.x / resolution.y; // Correct for aspect ratio

            float D = length(delta);
            vec3 N = normalize(normal * 2.0 - 1.0);
            vec3 L = normalize(delta);

            vec3 diffuse = (lights[i].color.rgb * lights[i].color.a) * max(dot(N, L), 0.0);
            float attenuation = 1.0 / (lights[i].falloff.x + (lights[i].falloff.y * D) + (lights[i].falloff.z * D * D));
            vec3 intensity = diffuse * attenuation;

            float height = texture2D(u_heightMap, v_texCoords).r;

            if (height <= lights[i].position.z) {
              sumLight += texture.rgb * intensity;
            }
        }
    }
    gl_FragColor = v_color * vec4(sumLight, texture.a);
}