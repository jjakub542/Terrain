(ns main.core
  (:require [terrain.scene :as tscene]))

;; Global app state that survives hot reloads
(defonce state (atom nil))

;; Handle browser window resizing
(defn resize! []
  (when-let [{:keys [renderer ^js camera]} @state]
    (let [w js/window.innerWidth
          h js/window.innerHeight]
      (set! (.-aspect camera) (/ w h))
      (.updateProjectionMatrix camera)
      (.setSize renderer w h))))

;; Animation loop
(defn animate []
  (when-let [{:keys [renderer scene ^js camera]} @state]
    (.render renderer scene camera)
    (js/requestAnimationFrame animate)))

;; App initialization
(defn init []
  (let [canvas (.getElementById js/document "app")
        renderer (tscene/create-renderer canvas)
        camera (tscene/create-camera)
        scene (tscene/create-scene)
        terrain (tscene/create-terrain)]

    (.add scene terrain)

    ;; Store everything in global state
    (reset! state {:renderer renderer
                   :camera   camera
                   :scene    scene
                   :terrain  terrain})

    ;; Initial resize + start animation
    (resize!)
    (js/requestAnimationFrame animate)

    ;; Listen for window resizes
    (.addEventListener js/window "resize" resize!)))

;; Entry point for shadow-cljs
(defn ^:export main []
  (when-not @state
    (init)))
