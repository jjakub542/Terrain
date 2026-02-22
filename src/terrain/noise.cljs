(ns terrain.noise)

(defonce permutation
  [151 160 137 91 90 15 131 13 201 95 96 53 194 233 7 225
   140 36 103 30 69 142 8 99 37 240 21 10 23 190 6 148
   247 120 234 75 0 26 197 62 94 252 219 203 117 35 11 32
   57 177 33 88 237 149 56 87 174 20 125 136 171 168 68 175
   74 165 71 134 139 48 27 166 77 146 158 231 83 111 229 122
   60 211 133 230 220 105 92 41 55 46 245 40 244 102 143 54
   65 25 63 161 1 216 80 73 209 76 132 187 208 89 18 169
   200 196 135 130 116 188 159 86 164 100 109 198 173 186 3 64
   52 217 226 250 124 123 5 202 38 147 118 126 255 82 85 212
   207 206 59 227 47 16 58 17 182 189 28 42 223 183 170 213
   119 248 152 2 44 154 163 70 221 153 10 155 167 43 172 9
   129 22 39 253 19 98 108 110 79 113 224 232 178 185 112 104
   218 246 97 228 251 34 242 193 238 210 144 12 191 179 162 241
   81 51 145 235 249 14 239 107 49 192 214 31 181 199 106 157
   184 84 204 176 115 121 50 45 127 4 150 254 138 236 205 93
   222 114 67 29 24 72 243 141 128 195 78 66 215 61 156 180])

(defonce p
  (into-array (concat permutation permutation)))

(defn- fade [t]
  (* t t t (+ (* t (- (* t 6) 15)) 10)))

(defn- lerp [t a b]
  (+ a (* t (- b a))))

(defn- grad [hash x y]
  (let [h (bit-and hash 3)
        u (if (< h 2) x y)
        v (if (< h 2) y x)]
    (+ (if (zero? (bit-and h 1)) u (- u))
       (if (zero? (bit-and h 2)) v (- v)))))

(defn noise-2d [x y]
  (let [xi (bit-and (int (Math/floor x)) 255)
        yi (bit-and (int (Math/floor y)) 255)
        xf (- x (Math/floor x))
        yf (- y (Math/floor y))
        u (fade xf)
        v (fade yf)
        aa (aget p (+ xi (aget p yi)))
        ab (aget p (+ xi (aget p (bit-and (inc yi) 255))))
        ba (aget p (+ (bit-and (inc xi) 255) (aget p yi)))
        bb (aget p (+ (bit-and (inc xi) 255) (aget p (bit-and (inc yi) 255))))
        x1 (lerp u (grad aa xf yf)
                 (grad ba (dec xf) yf))
        x2 (lerp u (grad ab xf (dec yf))
                 (grad bb (dec xf) (dec yf)))]
    ;; normalize to [0,1]
    (/ (lerp v x1 x2) 2.0)))
