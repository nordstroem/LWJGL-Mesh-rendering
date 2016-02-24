#version 330

in vec2 fragUV;
in vec3 fragNormal;
in vec3 fragPos;

out vec4 outColor;

uniform sampler2D modelTexture;
uniform int tick;
uniform vec2 mousePos;

void main()
{
	float d = length(fragPos - vec3(mousePos.xy, fragPos.z));
	float light = 1/(1 + 0.001*d + 0.0003*pow(d,2));
    outColor = texture(modelTexture, fragUV);
}
