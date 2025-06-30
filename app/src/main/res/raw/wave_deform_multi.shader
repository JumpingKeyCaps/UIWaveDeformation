// wave_deform_multi.shader

uniform shader uTexture;
uniform float2 uResolution;        // Taille de l'écran (viewport)
uniform float2 uImageSize;         // Taille de l'image réelle (bitmap)
uniform float uTime;

uniform int uWaveCount;
uniform float2 uWaveCenters[8];
uniform float uAmplitudes[8];
uniform float uFrequencies[8];
uniform float uSpeeds[8];
uniform float uDampings[8];

// Version correcte : crop-fill centré (image remplie et centrée)
float2 getCenterCropFillUV(float2 fragCoord, float2 screenRes, float2 imgSize) {
    float2 screenUV = fragCoord / screenRes;

    float screenAspect = screenRes.x / screenRes.y;
    float imageAspect = imgSize.x / imgSize.y;

    float scale;
    float2 offset;

    if (imageAspect > screenAspect) {
        // Image plus large → crop sur les côtés (scale par hauteur)
        scale = screenRes.y / imgSize.y;
        float scaledWidth = imgSize.x * scale;
        offset = float2((scaledWidth - screenRes.x) * 0.5, 0.0);
    } else {
        // Image plus haute → crop en haut/bas (scale par largeur)
        scale = screenRes.x / imgSize.x;
        float scaledHeight = imgSize.y * scale;
        offset = float2(0.0, (scaledHeight - screenRes.y) * 0.5);
    }

    // Coordonnée dans l'image en pixels (après crop centré)
    float2 imageCoord = (fragCoord + offset) / scale;

    return imageCoord / imgSize; // UV normalisés dans [0..1]
}

half4 main(float2 fragCoord) {
    float2 offsetSum = float2(0.0);

    for (int i = 0; i < 8; i++) {
        if (i >= uWaveCount) break;

        float2 delta = fragCoord - uWaveCenters[i];
        float dist = length(delta);
        if (dist == 0.0) continue;

        float phase = dist * uFrequencies[i] * 6.2831 - uTime * uSpeeds[i];
        float attenuation = exp(-uDampings[i] * dist);
        float waveOffset = sin(phase) * uAmplitudes[i] * attenuation;

        offsetSum += normalize(delta) * waveOffset;
    }

    float2 warpedCoord = clamp(fragCoord + offsetSum, float2(0.0), uResolution);

    // Nouveau center crop fill
    float2 imageUV = getCenterCropFillUV(warpedCoord, uResolution, uImageSize);
    imageUV = clamp(imageUV, float2(0.0), float2(1.0));

    float2 imageCoord = imageUV * uImageSize;
    return uTexture.eval(imageCoord);
}