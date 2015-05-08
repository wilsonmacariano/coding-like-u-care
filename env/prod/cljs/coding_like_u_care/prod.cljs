(ns coding-like-u-care.prod
  (:require [coding-like-u-care.core :as core]))

;;ignore println statements in prod
(set! *print-fn* (fn [& _]))

(core/init!)
