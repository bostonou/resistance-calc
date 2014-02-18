(ns resistance-calc.core
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [sablono.core :as html :refer-macros [html]]
            [cljs.core.async :refer [<! chan put! sliding-buffer]]))

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

(def omit-ops
  [[10 11]
   [10 11]
   [10 11]
   [8 9]
   [3 4 9]])

(defn tolerance-indicator [{:keys [tolerance]} owner]
  (reify
    om/IRender
    (render [_]
      (let [tolerance-str (if (= 0 tolerance) "" (str "±" tolerance "%"))]
        (html
         [:p {:id "toleranceValue"} tolerance-str])))))

(defn band-selector [data owner]
  (reify
    om/IInitState
    (init-state [_]
      {:selected 0})
    om/IRenderState
    (render-state [_ {:keys [selected band-chan]}]
      (let [{:keys [omit-ops band idx]} data
            valid-opts (filter (fn [{v :value}] (not (some #{v} omit-ops))) band-options)
            options (map (fn [{:keys [value label]}]
                           [:option {:value value} label])
                         valid-opts)]
        (html
         [:div {:class "bandOption"}
          [:label (str "Band " (inc idx))]
          [:select {:ref "menu" :value selected
                    :onChange #(let [v (.. % -target -value)]
                                 (put! band-chan [idx v])
                                 (om/set-state! owner [:selected] v))}
           options]])))))

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

(defn calc-tolerance [bands]
  (:tolerance (nth bands 4)))

(defn resistance-calculator [data owner]
  (reify
    om/IInitState
    (init-state [_]
      {:band-chan (chan (sliding-buffer 1))})
    om/IWillMount
    (will-mount [_]
      (if-let [band-chan (om/get-state owner :band-chan)]
        (go (while true
              (when-let [[idx selected] (<! band-chan)]
                (om/update! data [:bands idx] (get band-options selected)))))))
    om/IRenderState
    (render-state [_ {:keys [band-chan]}]
      (html
       [:div
        (om/build tolerance-indicator (om/graft {:tolerance (calc-tolerance (:bands data))} data))
        (om/build register (:bands data))
        (om/build-all band-selector (map-indexed (fn [idx band]
                                                   (om/graft {:idx idx :band band :omit-ops (get omit-ops idx)} band))
                                                 (:bands data))
                      {:init-state {:band-chan band-chan}})]))))

(om/root
  (fn [app owner]
    (om/build resistance-calculator app))
  app-state
  {:target (. js/document (getElementById "app"))})
