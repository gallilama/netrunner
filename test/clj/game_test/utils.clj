(ns game-test.utils
  (:require [monger.core :as mg]
            [monger.collection :as mc]
            [jinteki.cards :refer [all-cards]]))

(defn load-card [title]
  (let [conn (mg/connect {:host "127.0.0.1" :port 27017})
        db (mg/get-db conn "netrunner")
        card (mc/find-maps db "cards" {:title title})
        ret (first card)]
    (mg/disconnect conn)
    ret))

(defn load-cards []
  (let [conn (mg/connect {:host "127.0.0.1" :port 27017})
        db (mg/get-db conn "netrunner")
        cards (doall (mc/find-maps db "cards"))]
    (mg/disconnect conn)
    cards))

(defn qty [card amt]
  (let [loaded-card (if (string? card) (@all-cards card) card)]
    (when-not loaded-card
      (throw (Exception. (str card " not found in @all-cards"))))
    {:card loaded-card :qty amt}))

(defn make-deck [identity deck]
  {:identity identity
   :deck (map #(if (string? %) (qty % 1) %) deck)})

(defn default-corp
  ([] (default-corp [(qty "Hedge Fund" 3)]))
  ([deck] (make-deck "Custom Biotics: Engineered for Success" deck)))

(defn default-runner
  ([] (default-runner [(qty "Sure Gamble" 3)]))
  ([deck] (make-deck "The Professor: Keeper of Knowledge" deck)))
