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
    [:h1 {:class "title"} "Coding Like U Care!"]]
   [:div {:class "row first"}
    [:p {:class "col-sm-12"} "A brief talk about our craft, how we can improve our thinking, our team comunication and the art of express intetion through code."]]])

(defn problems-page []
  [:section
   [:div {:class "row well"}
    [:h1 "Problems"]]
   [:div {:class "row main"}
    [:ul
     [:li "Code Maintenance"]
     [:li "Procedural and Closed Implementations"]
     [:li "Poluted Code"]
     [:li "Large Classes"]
     [:li "Large Methods"]
     [:li "And lots of bugs..."]]]])

(defn topics-page []
  [:section
   [:div {:class "row well"}
    [:h1 "Topics"]]
   [:div {:class "row main"}
    [:div {:class "col-sm-4"}
     [:div {:class "row"}
      [:h3 "Code"]]
     [:div {:class "row"}
      [:ul
       [:li "Naming"]
       [:li "Functions"]
       [:li "Form"]
       [:li "Refactoring"]
       [:li "Code Smells"]]]]
    [:div {:class "col-sm-4"}
     [:div {:class "row"}
      [:h3 "Design"]]
     [:div {:class "row"}
      [:ul
       [:li "SOLID"]
       [:li "Package Cohesion/Coupling Principles"]
       [:li "Design Smells"]
       [:li "Patterns"]]]]
    [:div {:class "col-sm-4"}
     [:div {:class "row"}
      [:h3 "Practices"]]
     [:div {:class "row"}
      [:ul
       [:li "Testing"]]]]]])

(defn weakness-page []
  [:section
   [:div {:class "row well"}
    [:h1 "Weakness"]]
   [:div {:class "row main"}
    [:ul
     [:li "Write code that is easy to understand and maintain"]
     [:li "Separate code in small reusable units"]
     [:li "Test in efficient way"]
     [:li "Talk about code"]
     [:li "Have a life"]]]])

(defn current-page []
  [:div [(session/get :current-page)]])

;; -------------------------
;; Routes
(secretary/set-config! :prefix "#")

(secretary/defroute "/" []
  (session/put! :current-page #'home-page))

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

(def pages [#'home-page
            #'topics-page
            #'problems-page
            #'weakness-page])

(def cur-page (atom 0))

(defn next-page []
  (let [cur (deref cur-page)
        next (inc (deref cur-page))]
    (if (= next (count pages)) cur
        next)))

(defn move-to-next []
  (let [n (next-page)]
    (reset! cur-page n)
    (session/put! :current-page (nth pages n))))

(defn prev-page []
  (let [cur (deref cur-page)
        prev (dec (deref cur-page))]
    (if (= cur 0) cur
        prev)))

(defn move-to-prev []
  (let [n (prev-page)]
    (reset! cur-page n)
    (session/put! :current-page (nth pages n))))

(defn keydown [event]
  (let [key (.-keyCode event)]
    (cond
      (= key 37) (move-to-prev)
      (= key 39) (move-to-next))))


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
