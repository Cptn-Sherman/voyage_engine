


# **PLANNED FEATURES**

---
### **APPLICATION:**
- [X] Internal Game loop
- [X] Frame rate target
- [X] Update rate target
- [X] Debug system information
- [X] Tick with delta time
- [X] Slow tick
- [X] Cleanup on close
#### **Bugs:**
#### **Notes:**
- The engines application class is extended by remnants game class.
- Engine takes the opportunity to finish loading content when a frame does not need to be rendered.

---

### **STATE:**
- [X] IState interface
- [ ] Layered state rendering
- [ ] Main menu state
- [ ] Settings state
- [ ] Pause Menu state
- [ ] Game State
- [ ] Image gallery viewer state
- [ ] Save game list
#### **Bugs:**
#### **Notes:**
- States double as user interface bundles. Pause menu and save game list are not "technically" states but are implemented the same way just rendered as layered entities. The user only has access to one state to make inputs.

---

### **MULTITHREAD:**
- [X] Generate thread pool
- [X] Stop thread pool
- [X] Queue task
- [ ] Dequeue task
- [X] Return partial task for main thread completion.
#### **Bugs:**
- [ ]
#### **Notes:**
- Number of threads generated is based on query about number of cores the system has.

---

### **ASSET & CONTENT:**
- [X] Asset (Abstract)
- [X] Font
- [X] Mesh
- [X] Screenshot
- [X] Texture
- [X] Shader
- [ ] Compute Shader
- [X] Content (Abstract)
- [ ] Content Data
- [ ] Content Script
- [ ] Voxel
- [ ] Voxel Structure
- [ ] Biome
- [ ] Weather
- [ ] Save
- [ ] Action
- [ ] Metadata
- [ ] Quest
- [ ] Quest arc
- [ ] Start scenario
- [ ] Service
- [ ] Actor
- [ ] Creature (extends Actor)
#### **Bugs:**
#### **Notes:**
- All asset and content classes need proper documentation as these will be the main thing modders interact with to add new content.

---

### **ASSET & CONTENT MANAGEMENT:**
- [X] Manifest
- [X] Multithreaded asset loading
- [X] Asset Cache used per State instance
- [X] Asset Manager
- [ ] Content Manager
- [X] Asset & Content reference id's.
- [X] Asset & Content reference count.
- [X] Asset & Content unload when refernce count is zero.
- [ ] Manifest asset & content id collisions.
- [ ] Hot load .class which extend Asset & Content classes.
- [ ] Screenshots folder in "data" needs to be ignored by the manifest.
#### **Bugs:**
- [ ] `Surprise.jpg` is loaded by asset manager and the reference is higher than expected. (Was 3 expected 2). This results in asset staying loaded when it should not be.
- [ ] manifest.json highestID contains the wrong value. Should not be lower than any values in folderToLastID.
```json
  "folderToLastID": {
    "data\\screenshots": 32797, <-- A
    "data\\base": 34
  },
  "highestID": 32767 <--- should be higher than A
```
#### **Notes:**
- Pools of threads distribute tasks evenly.
- Asset and content managers can load and unload when there are not instance of the asset in use.
- Each Mod pack is allowed ~32,768 asset or content id's to use. When added to the users game each item is added to the master manifest file. If a mod is updated with new assets or content it is appended using the last used id for that mod pack.
- Each Mod is assigned a 16-bit value bit shifted to the upper half of an asset id's 32-bit value. 
- GSON is used to write and read json files. 

---

### **MODDING & USER SHARED CONTENT:**
- [ ] Instructions to pack a user jar with data folder included.
- [ ] Load external jars.
- [ ] Update added mod.
- [ ] Users can attempt to force load a mod jar with missmatched versions.
- [ ] Load new mod jars at runtime.
- [ ] Load unpacked java project for development of mods.
#### **Bugs:**
#### **Notes:**
- 

---

### **ACTORS:**
- [ ] Task.
#### **Bugs:**
- [ ]
#### **Notes:**

--

### **PLATE TECTONIC SIMULATION:**
- [ ] 
#### **Bugs:**
- [ ] Unintentional index values being written to the plate id array, which crashes the attempt to render to a texture. 
#### **Notes:**
- note.
  
---

### **WORLD MAP CONTROLS:**
- [ ] Zoom 
- [ ] Pan
- [ ] Infinite map panning 
- [ ] Options for rendering: 
  - plate ID, height, biome, annual mouisture, annual solar energy, source rock
- [ ] Extrude heights (I.E. 3D map rendering)
- [ ] Show plate direction vectors
- [ ] Show water level
- [ ] Plate information on hover (plate id, plate mass, plate speed)
- [ ] Render waterways & bodies of water
- [ ] Manual plate direction changes
- [ ] Ability to name set the name of the world & or randomize
#### **Bugs:**
#### **Notes:**

---

### **HISTORICAL SIMULATION:**
- [ ] Faction creation
- [ ] Location generation
- [ ] Terrestrial travel networks
- [ ] Oceanic travel networks
- [ ] Population tracking
- [ ] Conflicts
#### **Bugs:**
#### **Notes:**

---

### **USER INTERFACE:**
- [X] Basic Font
- [ ] Font sets
- [X] Label
- [ ] Buttons
- [ ] Sliders
- [ ] Checkbox
- [ ] Text input
    - Hold control + delete to delete full words or move cursor by word
    - Clear text button at the end (optional)
    - Predictive typing based on array list of inputs or tree for faster search? (optional)
- [ ] Drop-down box
    - Array list input for selection
    - Partial typing to auto search the list for a valid match
- [ ] SVG icon
- [ ] Image
- [ ] Image Gallery viewer (screenshot gallery)
- [ ] Video embed
- [ ] Notification pop-up in the right corner
- [ ] Grid view panel
- [ ] List view panel
- [ ] Cordinate view panel
- [ ] Editable interface settings saved to file and sharable.
- [ ] UI Transition interface/abstract class.
#### **Bugs:**
- [X] Font rendering has weird scaling.
#### **Notes:**
- Mondern and flat user interface styling.
- No onscreen minimaps
- No onscreen compass with map icons
- Onscreen compass with North, South, East, and West are fine.
- Cordinate view system is a pixel offset from a set point. This point defaults to the center of the screen but can be anywhere on the screen.

---

### **TERRAIN MESH GENERATION:**
- [ ] Generate meshes with compute shader? or on cpu thread
- [ ] Distant terrain level of detail (LOD)
- [ ] Tri planar texturing
- [ ] Material/texture edge blending
- [ ] Voxel smoothing/interpolation
- [ ] Greedy cube meshing
- [ ] Instance render terrain chunks
#### **Bugs:**
#### **Notes:**

---

### **GRAPHICS:**
- [X] 3D Camera
- [X] 2D Camera
- [X] Color class
- [X] Window class
- [X] OpenGL function class
- [X] IRenderable interface
- [ ] GPU instanced rendering
- [ ] Frustrum culling
- [ ] Occlusion culling
- [ ] Volumentric lighting
- [ ] Subsurface scattering
- [ ] God rays
- [ ] DLSS 2.1
- [ ] SSAO
- [ ] Faked planetary curvature.
- [ ] Deferred rendering pipeline
#### **Bugs:**
#### **Notes:**
- Vintage story has really nice curvature, best seen in the clouds.

---

### **WEATHER:**
- [ ] Voxel clouds
- [ ] Cloud light caustics
- [ ] Lighting & thunder
- [ ] Wind lines (like wind waker but more subtle)
- [ ] Global weather simulation (rain, storms, natural weather disasters)
#### **Bugs:**
#### **Notes:**
- Clouds use the chunk system but have larger voxels. 
- Clouds also implement LOD for further away clouds.
---

### **WATER:**
- [ ] Edge foam
- [ ] Light Caustics
- [ ] Depth scattering
- [ ] Animated waves
- [ ] Wind based wave direction
- [ ] Underwater rendering
- [ ] Water currents
#### **Bugs:**
#### **Notes:**

---

### **PHYSICS:**
- [ ] Collisions
- [ ] Projectiles
#### **Bugs:**
- [ ] 
#### **Notes:**

---

### **ANIMATIONS:**
- [ ] Inverse Kinematics
#### **Bugs:**
- [ ]
#### **Notes:**

---

### **SOUND:**
- [ ] 3D sound & Reverberation
- [ ] Environment sounds
#### **Bugs:**
#### **Notes:**
- Relaxing sound tracks or medieval synth might work well.
  
---

### **USER INPUT**
- [X] Key input
- [X] Mouse input
- [X] Mouse position 
- [X] Mouse delta
- [X] Key Debounce
#### **Bugs:**
#### **Notes:**
- May need a clean up but basically complete.

---

### **NETWORKING:**
- [ ] Hole punch connecting
#### **Bugs:**
#### **Notes:**
- Multiplayer is not 100% happening.

---

### **SETTINGS:**
- [X] Custom settings saved to json file.
- [ ] Implement console to log file. [Tutorial Link](https://www.dev2qa.com/how-to-write-console-output-to-text-file-in-java/#:~:text=How%20To%20Write%20Console%20Output%20To%20Text%20File,Console%20Output%20To%20Text%20File%20In%20Java%20Examples.)
#### **Bugs:**
#### **Notes:**

---

### **LAUNCHER:**
- [ ] Download specific version of the game
- [ ] Download the latest version of the game
- [ ] Launch game by version
- [ ] Manage game install with jvm arguments
- [ ] Handle login player account
- [ ] Upload error logs on game crash
#### **Bugs:**
#### **Notes:**
-  

---

### **VERSIONING:**
- [ ] Users can have multiple game versions installed.
- [ ] Update game from launcher.
#### **Bugs:**
#### **Notes:**
- Versioning number scheme contains a major, minor, and patch number (2.08.05). 
- All versions can contain breaking changes and should be as such with a capital letter at the end (2.08.05B). A captial B representing a breaking change in this update.

---

### **MISC & QUALITY OF LIFE:**
- [ ] Arbitary insertion of large voxel structures
    - Rotations.
    - Could be used to place statues and large structures that are slightly broken.
- [ ] Procedural destruction for structures and buildings 
    - Remove orgnaic materials first.
    - Add plant life.
    - Top down topple structure(s).
- [ ] Photo Mode
- [ ] Image Library
- [ ] Toggle HUD button to take photos
#### **Bugs:**
#### **Notes:**

---

### **TEMPLATE:**
- [ ] Task.
#### **Bugs:**
- [ ]
#### **Notes:**