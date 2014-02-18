(ns resistance-calc.core
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [sablono.core :as html :refer-macros [html]]))

(enable-console-print!)

(def app-state (atom {}))

(defn resistance-calculator [data owner]
  (reify
    om/IRender
    (render [_]
      (html
       [:div "Resistance"]))))

(om/root
  (fn [app owner]
    (om/build resistance-calculator app))
  app-state
  {:target (. js/document (getElementById "app"))})
