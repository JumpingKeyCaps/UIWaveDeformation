// wave_deform_multi.shader
uniform shader uTexture;
uniform float2 uResolution;

uniform float uTime;
uniform int uWaveCount;

uniform float2 uWaveCenters[8];     // Position des ondes (pixels)
uniform float uAmplitudes[8];       // Amplitudes
uniform float uFrequencies[8];      // Fréquences spatiales
uniform float uSpeeds[8];           // Vitesse de propagation
uniform float uDampings[8];         // Atténuation exponentielle

half4 main(float2 fragCoord) {
    float2 uv = fragCoord / uResolution;
    float2 finalOffset = float2(0.0);

    for (int i = 0; i < uWaveCount; i++) {
        float2 centerUV = uWaveCenters[i] / uResolution;
        float2 dir = uv - centerUV;
        float dist = length(dir);
        if (dist == 0.0) continue;

        float phase = dist * uFrequencies[i] * 6.2831 - uTime * uSpeeds[i];
        float attenuation = exp(-uDampings[i] * dist);
        float offset = sin(phase) * uAmplitudes[i] * attenuation;
        finalOffset += normalize(dir) * offset;
    }

    float2 warpedUV = uv + finalOffset;
    return uTexture.eval(warpedUV * uResolution);
}