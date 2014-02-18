(ns resistance-calc.core
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [sablono.core :as html :refer-macros [html]]))

(enable-console-print!)

(def app-state (atom {:bands [{:value 0 :tolerance 0 :color "black" :label "None"}
                              {:value 0 :tolerance 0 :color "black" :label "None"}
                              {:value 0 :tolerance 0 :color "black" :label "None"}
                              {:value 0 :tolerance 0 :color "black" :label "None"}
                              {:value 0 :tolerance 0 :color "black" :label "None"}]}))

(def band-options
  [{:value 0 :tolerance 0 :color "black" :label "None"}
   {:value 1 :tolerance 1 :color "brown" :label "Brown"}
   {:value 2 :tolerance 2 :color "red" :label "Red"}
   {:value 3 :color "orange" :label "Orange"}
   {:value 4 :color "yellow" :label "Yellow"}
   {:value 5 :tolerance 0.5 :color "green" :label "Green"}
   {:value 6 :tolerance 0.25 :color "blue" :label "Blue"}
   {:value 7 :tolerance 0.10 :color "violet" :label "Violet"}
   {:value 8 :tolerance 0.05 :color "grey" :label "Grey"}
   {:value 9 :color "white" :label "White"}
   {:value 10 :tolerance 5 :color "#FFD700" :label "Gold"}
   {:value 11 :tolerance 10 :color "#C0C0C0" :label "Silver"}])

(defn register [bands owner]
  (reify
    om/IRender
    (render [_]
      (let [[band-1 band-2 band-3 band-4 band-5] bands]
        (html
         [:svg {:width 300 :height 100 :version "1.1" :xmlns "http://www.w3.org/2000/svg"}
          [:rect {:x 0 :y 26 :rx 5 :width 300 :height 7 :fill "#d1d1d1"}]
          [:rect {:x 50 :y 0 :rx 15 :width 200 :height 57 :fill "#FDF7EB"}]
          [:rect {:id "band1" :x 70 :y 0 :rx 0 :width 7 :height 57 :fill (:color band-1)}]
          [:rect {:id "band2" :x 100 :y 0 :rx 0 :width 7 :height 57 :fill (:color band-2)}]
          [:rect {:id "band3" :x 130 :y 0 :rx 0 :width 7 :height 57 :fill (:color band-3)}]
          [:rect {:id "band4" :x 160 :y 0 :rx 0 :width 7 :height 57 :fill (:color band-4)}]
          [:rect {:id "band5" :x 210 :y 0 :rx 0 :width 7 :height 57 :fill (:color band-5)}]])))))

(defn resistance-calculator [data owner]
  (reify
    om/IInitState
    (init-state [_]
      {:bands [0 0 0 0 0] :resistance 0 :tolerance 0})
    om/IRender
    (render [_]
      (om/build register (:bands data)))))

(om/root
  (fn [app owner]
    (om/build resistance-calculator app))
  app-state
  {:target (. js/document (getElementById "app"))})
