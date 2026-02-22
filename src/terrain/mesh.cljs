(ns terrain.mesh
  (:require ["three" :as THREE]
            [terrain.heightmap :as hm]))

(defn- height-to-color [y max-height]
  "Convert height value to RGB color.
   Green at sea level, brown at mid, white at peaks."
  (let [norm (/ y max-height)
        r (cond (< norm 0.4) 34    ;; darker green at base
                (< norm 0.6) 139   ;; brown in middle
                :else 220)         ;; light gray at peaks
        g (cond (< norm 0.4) 139   ;; green
                (< norm 0.6) 90    ;; brown
                :else 220)         ;; light gray
        b (cond (< norm 0.4) 34    ;; dark green
                (< norm 0.6) 60    ;; brown
                :else 240)]        ;; white-ish
    #js [r g b]))

(defn create-terrain-mesh
  [{:keys [width height segments scale opts]
    :or {width 400
         height 400
         segments 199
         scale 1.0
         opts {}}}]
  (let [geom (THREE/PlaneGeometry. width height segments segments)
        pos-attr (.-position (.-attributes geom))
        arr (.-array pos-attr)
        w (inc segments)
        h (inc segments)
        heights (hm/generate-heightmap w h opts)
        max-height (apply max heights)]

    ;; Update vertex heights (Z axis for horizontal plane)
    (dotimes [i (* w h)]
      (let [height (aget heights i)
            idx (* i 3)]
        (aset arr (+ idx 2) height)))  ;; Z component (idx+2)

    (set! (.-needsUpdate pos-attr) true)

    ;; Add vertex colors based on height
    (let [color-attr (THREE/BufferAttribute. (js/Uint8Array. (* 3 (count heights))) 3 true)
          color-arr (.-array color-attr)]
      (dotimes [i (count heights)]
        (let [h (aget heights i)
              [r g b] (height-to-color h max-height)]
          (aset color-arr (* i 3) r)
          (aset color-arr (+ (* i 3) 1) g)
          (aset color-arr (+ (* i 3) 2) b)))
      (.setAttribute geom "color" color-attr))

    (.computeVertexNormals geom)

    ;; Create material with vertex colors
    (let [mat (THREE/MeshStandardMaterial.
               #js {:color 0xffffff
                    :vertexColors true
                    :roughness 0.85
                    :metalness 0.1
                    :flatShading false
                    :side (.-DoubleSide THREE)})
          mesh (THREE/Mesh. geom mat)]
      mesh)))
