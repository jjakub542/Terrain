(ns terrain.scene
  (:require ["three" :as THREE]
            [terrain.mesh :as tmesh]))

(defn create-renderer [canvas]
  (let [renderer (THREE/WebGLRenderer. #js {:canvas canvas
                                            :antialias true})]
    (.setPixelRatio renderer (.-devicePixelRatio js/window))
    (.setSize renderer js/window.innerWidth js/window.innerHeight)
    renderer))

(defn create-camera []
  (let [camera (THREE/PerspectiveCamera.
                45
                (/ js/window.innerWidth js/window.innerHeight)
                0.1
                2000)]
    (set! (.-x (.-position camera)) 400)
    (set! (.-y (.-position camera)) 250)
    (set! (.-z (.-position camera)) 600)
    (.lookAt camera (THREE/Vector3. 0 100 0))
    camera))

(defn create-lights [scene]
  (let [ambient (THREE/AmbientLight. 0xffffff 0.4)
        dir (THREE/DirectionalLight. 0xffffff 0.8)]
    (set! (.-x (.-position dir)) 100)
    (set! (.-y (.-position dir)) 200)
    (set! (.-z (.-position dir)) 100)
    (.add scene ambient)
    (.add scene dir)))

(defn create-scene []
  (let [scene (THREE/Scene.)]
    (set! (.-background scene) (THREE/Color. 0x87ceeb)) ; sky-ish
    (create-lights scene)
    scene))

(defn create-terrain []
  (tmesh/create-terrain-mesh
   {:width 600
    :height 1000
    :segments 199
    :opts {:scale 0.03
           :amplitude 40.0
           :octaves 5
           :persistence 0.5}}))
