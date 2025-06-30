// wave_deform_minimal.shader


uniform shader uTexture;          // Texture source (le bitmap à déformer)
uniform float2 uWaveCenter;       // Centre de l'onde (en pixels)
uniform float uAmplitude;         // Amplitude de l'onde
uniform float uFrequency;         // Fréquence spatiale
uniform float uTime;              // Temps en secondes
uniform float2 uResolution;       // Résolution du bitmap (width, height)

half4 main(float2 fragCoord) {
    // Position normalisée
    float2 uv = fragCoord / uResolution;

    // Distance au centre de l'onde
    float2 waveCenterUV = uWaveCenter / uResolution;
    float dist = distance(uv, waveCenterUV);

    // Déplacement radial en fonction de la distance + temps
    float offset = sin(dist * uFrequency * 2.0 * 3.1415 - uTime * 4.0) * uAmplitude;

    // Direction du déplacement (normalisée)
    float2 dir = normalize(uv - waveCenterUV);

    // Coordonnée déformée
    float2 warpedUV = uv + dir * offset;

    // Échantillonnage de la texture déformée
    return uTexture.eval(warpedUV * uResolution);
}