# UI Wave Deformation - Compose Snapshot + Interactive Overlay

![Platform](https://img.shields.io/badge/platform-Android-green?logo=android)
![Language](https://img.shields.io/badge/language-Kotlin-orange?logo=kotlin)
![UI Framework](https://img.shields.io/badge/UI-Jetpack%20Compose-blue?logo=jetpack-compose)
![License](https://img.shields.io/badge/license-MIT-lightgrey)

---

## 🚀 Project Overview

This mini-project explores applying a **realistic wave deformation effect** on a **Jetpack Compose UI** by combining graphical rendering and user interaction.

The main idea is to simulate a **ripple wave propagating over a 2D grid surface**, then successively apply this deformation on:  
- the grid itself,  
- a bitmap image placed on the grid,  
- a snapshot of the real Compose UI,  
while keeping a **fully interactive UI responsive** via a transparent overlay.

---

## 🎯 Goals

- Implement a realistic wave deformation (ripple/distortion) on a 2D grid.  
- Apply this deformation progressively on more complex graphical content: bitmap, Compose UI snapshot.  
- Maintain UI interactivity with a **transparent interactive overlay** above the deformed rendering.  
- Support multiple simultaneous wave interactions triggered at different screen points.  
- Provide a clean and performant architecture compatible with Jetpack Compose.

---

## 🧩 Challenges

- **How to dynamically deform an entire Compose UI?**  
  Compose doesn’t natively allow transforming the whole composable tree as a single modifiable canvas in real-time.

- **Keeping UI responsiveness despite heavy graphical rendering (snapshot + deformation animations)?**  
  Running deformation animations on a static image without blocking or disabling the real UI layer.

- **Managing multiple independent wave animations triggered by user interactions fluidly and synchronized.**

- **Optimizing performance to avoid frame drops or excessive CPU usage.**

---

## 💡 Approach

1. **Phase 1: Grid deformation**  
   - Draw a 2D grid inside a Compose Canvas.  
   - Apply wave deformation by dynamically transforming grid points using sinusoidal wave functions with damping.

2. **Phase 2: Bitmap deformation**
   
   There are currently three different approaches explored for deforming the bitmap texture with wave effects:

   - **2.a) Full CPU Tile-based Deformation** (Current branch)  
     - The bitmap is subdivided into equal tiles (cells).  
     - Each tile is individually transformed (translated/scaled) according to wave deformation vectors computed on CPU.  
     - Dynamic subdivision is applied near wave origins for finer deformation.  
     - A blurred background layer compensates the visible mosaic effect caused by discrete tile transformations.  
     - Pros: Works on all Android versions, no GPU requirements.  
     - Cons: Visual artifacts due to tile mosaicking, limited smoothness.

   - **2.b) Runtime Shader (AGSL) Displacement** (Planned for Android 13+)  
     - Uses Android 13+ RuntimeShader API with AGSL shaders.  
     - The deformation is performed per-pixel on the GPU, based on a displacement map generated from wave parameters.  
     - Produces smooth, continuous wave distortions without visible tile artifacts.  
     - Pros: High-quality deformation, efficient GPU usage.  
     - Cons: Requires Android 13+, more complex shader development.

   - **2.c) OpenGL ES-based Deformation** (Optional advanced version)  
     - Uses OpenGL ES rendering pipeline with textured vertex grids.  
     - Allows full control over vertex deformation and texture mapping.  
     - Suitable for advanced effects and possibly 3D wave surfaces.  
     - Pros: Maximum flexibility and performance on supported devices.  
     - Cons: More complex setup, heavier maintenance, steeper learning curve.

4. **Phase 3: Compose UI snapshot deformation**  
   - Capture a snapshot (bitmap) of the real Compose UI.  
   - Apply wave deformation on this snapshot.  
   - Render the deformed image as a background.

5. **Maintaining UI interactivity**  
   - Overlay a **transparent, fully interactive Compose UI** on top of the deformed canvas.  
   - This overlay captures touch/click inputs to trigger wave animations.  
   - The UI remains fully functional and responsive, with multiple waves triggered concurrently without blocking.

---

## 📸 Screenshots

| Phase 1 | Phase 2 - full CPU (step I) | Phase 2 - full CPU (step II) | 
|:---:|:---:|:---:|
| ![P1](screenshots/phase1.gif) | ![P2a](screenshots/phase2a.gif) |  ![P2b](screenshots/phase2a2.gif) |
| Phase 2 - Runtime Shader AGSL | Phase 2 - OpenGL ES-based |
|:---:|:---:|
---

## ⚙️ Key Features

- Real-time management of multiple waves with natural propagation and decay.  
- Geometric transformation of grid points using sine functions and damping for smooth waves.  
- Efficient Compose UI snapshot capturing.  
- Synchronized animation and interaction handling.  
- Simple interface to switch between grid, bitmap, and snapshot deformation modes.  
- Modular architecture for easy extension of deformation effects.

---

## 📈 Expected Outcomes

- Ultra-realistic visual ripple effect on any graphical surface.  
- Fully responsive UI with multiple interactive wave triggers.  
- Potential integration into real-world projects for unique visual effects.

---

## 🔧 Technologies & Tools

- Kotlin + Jetpack Compose  
- Compose Canvas for drawing and graphic transformations  
- Compose Snapshot API for UI capture  
- Coroutines and State management for animation and interaction  
- Math & trigonometry for wave modeling  

---

## 🚧 Limitations & Future Work

- Performance constraints on low-end devices with multiple simultaneous waves.  
- Snapshot capture latency depending on UI complexity.  
- No GPU/OpenGL acceleration yet for deformation calculations.  
- Potential to add extra optical effects (blur, reflection, refraction).  
- Adaptation for multi-screen and orientation changes.

---

## 🧠 Conclusion

This project demonstrates how to combine **complex graphical rendering and modern interactive UI** on Android Compose by cleverly using snapshots and layered UI overlays.

It opens the door to unique visual effects while maintaining smooth, responsive user experiences — a common challenge in advanced UI/UX design.

---
