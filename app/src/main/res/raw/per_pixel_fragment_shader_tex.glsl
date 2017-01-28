precision mediump float;       	// Set the default precision to medium. We don't need as high of a 
								// precision in the fragment shader.
uniform vec3 u_LightPos;       	// The position of the light in eye space.
uniform sampler2D u_Texture;    // The input texture.

varying vec3 v_Position;		// Interpolated position for this fragment.
varying vec3 v_Normal;         	// Interpolated normal for this fragment.
varying vec2 v_TexCoordinate;   // Interpolated texture coordinate per fragment.

// The entry point for our fragment shader.
void main()
{
	// Will be used for attenuation.
    float distance = length(u_LightPos - v_Position);

	// Get a lighting direction vector from the light to the vertex.
    vec3 lightVector = normalize(u_LightPos - v_Position);

	/* Calculate the dot product of the light vector and vertex normal. If the normal and light vector are
	 pointing in the same direction then it will get max illumination.
	Reflectância lambertiana:
	    Para calcular o cosseno do ângulo entre a superfície e a luz, calcula-se o dot product do vetor da luz e a normal
	da superfície. Quando os vetores estão normalizados, o dot product dos vetores retorna o cosseno do ângulo entre eles,
	que é o necessário para calcular a reflectância lambertiana.*/
    float diffuse = max(dot(v_Normal, lightVector), 0.0);

	// Add attenuation.
    diffuse = diffuse * (1.0 / (1.0 + (0.25 * distance)));

    // Add ambient lighting
    diffuse = diffuse + 0.6;

	// Multiply the color by the diffuse illumination level and texture value to get final output color.
    gl_FragColor = (diffuse * texture2D(u_Texture, v_TexCoordinate));
  }

