#version 330

in vec2 fragUV;

out vec4 outColor;

uniform sampler2D texSampler;


void main()
{
    
    outColor = texture(texSampler,fragUV);
	//outColor = vec4(0,1,0,1);
}
