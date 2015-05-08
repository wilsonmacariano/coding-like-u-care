(ns coding-like-u-care.core
    (:require [reagent.core :as reagent :refer [atom]]
              [reagent.session :as session]
              [secretary.core :as secretary :include-macros true]
              [goog.events :as events]
              [goog.history.EventType :as EventType]
              [cljsjs.react :as react])
    (:import goog.History))

;; -------------------------
;; Views

(defn home-page []
  [:section
   [:div {:class "row well"}
    [:h1 {:class "col-sm-12"} "Coding Like U Fucking Care "]]
   [:div {:class "row"}
    [:h3 {:class "col-sm-8"} "A brief talk about our craft and how we can improve our way to work and comunicate through code."]]])

(defn about-page []
  [:div [:h2 "About coding-like-u-care"]
   [:div [:a {:href "#/"} "go to the home page"]]])

(defn current-page []
  [:div [(session/get :current-page)]])

;; -------------------------
;; Routes
(secretary/set-config! :prefix "#")

(secretary/defroute "/" []
  (session/put! :current-page #'home-page))

(secretary/defroute "/about" []
  (session/put! :current-page #'about-page))

;; -------------------------
;; History
;; must be called after routes have been defined
(defn hook-browser-navigation! []
  (doto (History.)
    (events/listen
     EventType/NAVIGATE
     (fn [event]
       (secretary/dispatch! (.-token event))))
    (.setEnabled true)))

(def pages [#'home-page #'about-page])

(def cur-page (atom 0))

(defn next-page []
  (let [cur (get pages cur-page)]
    (if (> cur (inc (count pages))) cur
        (inc cur))))

(defn move-to-next []
  (let [n (next-page)]
    (reset! cur-page n)
    (session/put! :current-page (nth pages n))))

(defn prev-page []
  (let [cur (get pages cur-page)]
    (if (<= cur 0) 0
        (dec cur))))

(defn move-to-prev []
  (let [n (prev-page)]
    (reset! cur-page n)
    (session/put! :current-page (nth pages n))))

(defn keydown [event]
  (let [key (.-keyCode event)]
    (if (= key 37) (move-to-next)
        (move-to-prev))))


(defn document-listener []
  (.addEventListener js/document
                     "keydown"
                       keydown))

;; -------------------------
;; Initialize app
(defn mount-root []
  (reagent/render [current-page] (.getElementById js/document "app")))

(defn init! []
  (hook-browser-navigation!)
  (document-listener)
  (mount-root))
