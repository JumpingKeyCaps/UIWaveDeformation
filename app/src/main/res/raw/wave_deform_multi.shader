// wave_deform_multi.shader

uniform shader uTexture;
uniform float2 uResolution;
uniform float2 uImageSize;

uniform int uWaveCount;
uniform float2 uWaveCenters[16];
uniform float uAmplitudes[16];
uniform float uFrequencies[16];
uniform float uAges[16];
uniform float uDampings[16];

const int MAX_WAVES = 16;

// crop-fill centré : image remplie et centrée
float2 getCenterCropFillUV(float2 fragCoord, float2 screenRes, float2 imgSize) {
    float2 screenUV = fragCoord / screenRes;

    float screenAspect = screenRes.x / screenRes.y;
    float imageAspect = imgSize.x / imgSize.y;

    float scale;
    float2 offset;

    if (imageAspect > screenAspect) {
        // image plus large que l'écran → crop sur les côtés (scale par hauteur)
        scale = screenRes.y / imgSize.y;
        float scaledWidth = imgSize.x * scale;
        offset = float2((scaledWidth - screenRes.x) * 0.5, 0.0);
    } else {
        // image plus haute que l'écran → crop en haut/bas (scale par largeur)
        scale = screenRes.x / imgSize.x;
        float scaledHeight = imgSize.y * scale;
        offset = float2(0.0, (scaledHeight - screenRes.y) * 0.5);
    }

    float2 imageCoord = (fragCoord + offset) / scale;
    return imageCoord / imgSize; // UV normalisées dans [0..1]
}

half4 main(float2 fragCoord) {
    float2 offsetSum = float2(0.0);

    for (int i = 0; i < MAX_WAVES; i++) {
        if (i >= uWaveCount) break;
        if (uAmplitudes[i] <= 0.0) continue;

        float2 delta = fragCoord - uWaveCenters[i];
        float dist = length(delta);
        if (dist == 0.0) continue;

        float age = uAges[i];
        if (age > 3.0) continue; // ignore vagues trop vieilles (3 sec par exemple)

        // Ligne modifiée : soustraire dist * uFrequencies[i]
        float phase = -dist * uFrequencies[i] * 6.283185 + age * 3.0;

        float distanceAttenuation = exp(-uDampings[i] * dist);
        float timeAttenuation = 1.0 - smoothstep(0.0, 3.0, age);

        float totalAttenuation = distanceAttenuation * timeAttenuation;
        float waveOffset = sin(phase) * uAmplitudes[i] * totalAttenuation;

        offsetSum += normalize(delta) * waveOffset;
    }

    float2 warpedCoord = clamp(fragCoord + offsetSum, float2(0.0), uResolution);

    float2 imageUV = getCenterCropFillUV(warpedCoord, uResolution, uImageSize);
    imageUV = clamp(imageUV, float2(0.0), float2(1.0));

    float2 imageCoord = imageUV * uImageSize;
    return uTexture.eval(imageCoord);
}