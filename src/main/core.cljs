(ns main.core
  (:require ["three" :as THREE]))

(defn init []
  (let [canvas (.getElementById js/document "app")
        renderer (THREE/WebGLRenderer. #js {:canvas canvas})
        scene (THREE/Scene.)
        camera (THREE/PerspectiveCamera. 75
                                         (/ js/window.innerWidth js/window.innerHeight)
                                         0.1
                                         1000)
        geometry (THREE/BoxGeometry.)
        material (THREE/MeshBasicMaterial. #js {:color 0x00ff00})
        cube (THREE/Mesh. geometry material)]

    (.setSize renderer js/window.innerWidth js/window.innerHeight)
    (.add scene cube)
    (set! (.-z camera.position) 5)

    (defn animate []
      (js/requestAnimationFrame animate)
      (set! (.-x cube.rotation) (+ (.-x cube.rotation) 0.01))
      (set! (.-y cube.rotation) (+ (.-y cube.rotation) 0.01))
      (.render renderer scene camera))

    (animate)))
