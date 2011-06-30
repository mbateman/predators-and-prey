(ns predators-and-prey.simulation
	(:use predators-and-prey.constants)
	(:use predators-and-prey.collisions))

(def predator {:max-velocity 7 :radius 15})
(def prey {:max-velocity 4 :radius 10})

(def animals (atom {}))

(defn create-predator [x y horizontal-velocity vertical-velocity]
	(conj {:x x :y y :vx horizontal-velocity :vy vertical-velocity} predator))
	
(defn create-prey
	([screen-size]
	(conj {:x (rand-int screen-size) :y (rand-int screen-size) :vx (rand-int (:max-velocity prey)) :vy (rand-int (:max-velocity prey))} prey))
	([x y vx vy]
	(conj {:x x :y y :vx vx :vy vy} prey)))
	
(defn prey-generator [screen-size]
	#(conj {:x (rand-int screen-size) :y (rand-int screen-size) :vx (rand-int (:max-velocity prey)) :vy (rand-int (:max-velocity prey))} prey))
	
(defn initial-state []
	{:predators [(create-predator 25 50 (rand-int 7) (rand-int 7))
	(create-predator (- screen-size 100) (- screen-size 100) -4 -4)]
	:prey (take 200 (repeatedly (prey-generator screen-size)))})

(defn move [animal]
	(let [x (:x animal) y (:y animal)
	vx (:vx animal) vy (:vy animal)]
	(assoc animal :x (mod (+ x vx) screen-size) :y (mod (+ y vy) screen-size))))

(defn grow [animal]
	(assoc animal :radius (+ 1 (animal :radius)))) 

(defn had-lunch? [predators]
  (fn [predator]
     (if (nil? (some #(collides? predator %) prey)) true false)))
(defn grow-if-collides [animal pack]
	(if (some #(collides? animal %) pack) (grow animal) animal))

(defn surviving? [predators]
	(fn [prey]
		(if (nil? (some #(collides? prey %) predators)) true false)))

(defn think [current-state]
	(let [new-predators (map #(grow-if-collides (move %) (:prey current-state)) (:predators current-state))
		remaining-prey (filter (surviving? new-predators) (:prey current-state))
		
]
	( 
		-> @animals
		(assoc :predators new-predators :prey remaining-prey))))

(defn pulse []
	(let [bounded-screen-size (- screen-size 20)]
	(if (empty? @animals)
		(reset! animals (initial-state))
		(swap! animals think))))
