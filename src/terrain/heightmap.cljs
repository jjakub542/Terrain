(ns terrain.heightmap
  (:require [terrain.noise :as noise]))

(defn generate-heightmap
  "Returns a flat JS array of size (width * height) with height values."
  [width height {:keys [scale amplitude octaves persistence]
                 :or {scale 0.05
                      amplitude 20.0
                      octaves 4
                      persistence 0.5}}]
  (let [data (js/Array.)]
    (dotimes [y height]
      (dotimes [x width]
        (let [nx (* x scale)
              ny (* y scale)
              h (loop [o 0
                       freq 1.0
                       amp 1.0
                       total 0.0]
                  (if (< o octaves)
                    (recur (inc o)
                           (* freq 2.0)
                           (* amp persistence)
                           (+ total (* amp (noise/noise-2d (* nx freq)
                                                           (* ny freq)))))
                    total))]
          (.push data (* amplitude h)))))
    data))
