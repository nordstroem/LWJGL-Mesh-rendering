
#version 330

layout ( location = 0) in vec3 position;
layout ( location = 1) in vec2 uv;
layout ( location = 2) in vec3 normal;

out vec2 fragUV;
out vec3 fragNormal;
out vec3 fragPos;

uniform mat4 PV;
uniform mat4 M;

void main()
{
    gl_Position = PV*M*vec4(position, 1.0);
    fragUV = vec2(uv.x, 1 - uv.y);
    fragNormal = normalize(mat3(M)*normal);
    fragPos = (M*vec4(position, 1.0)).xyz;
}