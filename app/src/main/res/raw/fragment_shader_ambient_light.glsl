precision mediump float;       	// Set the default precision to medium. We don't need as high of a 
								// precision in the fragment shader.
uniform vec3 u_LightPos;       	// The position of the light in eye space.
uniform sampler2D u_Texture;    // The input texture.

varying vec3 v_Position;		// Interpolated position for this fragment.
varying vec4 v_Color;          	// This is the color from the vertex shader interpolated across the
  								// triangle per fragment.
varying vec4 l_Color;
varying vec3 v_Normal;         	// Interpolated normal for this fragment.
varying vec2 v_TexCoordinate;   // Interpolated texture coordinate per fragment.

//v_Color = vem do vertex, é a cor do material
//l_color = cor da luz
// The entry point for our fragment shader.
void main()
{
   gl_FragColor = (v_Color * l_Color);
}

