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
   - Project a bitmap texture onto the deformed grid.  
   - The graphical deformation simulates a “water surface” distorting the image.

3. **Phase 3: Compose UI snapshot deformation**  
   - Capture a snapshot (bitmap) of the real Compose UI.  
   - Apply wave deformation on this snapshot.  
   - Render the deformed image as a background.

4. **Maintaining UI interactivity**  
   - Overlay a **transparent, fully interactive Compose UI** on top of the deformed canvas.  
   - This overlay captures touch/click inputs to trigger wave animations.  
   - The UI remains fully functional and responsive, with multiple waves triggered concurrently without blocking.

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
