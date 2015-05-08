(ns ^:figwheel-no-load coding-like-u-care.dev
  (:require [coding-like-u-care.core :as core]
            [figwheel.client :as figwheel :include-macros true]
            [weasel.repl :as weasel]
            [reagent.core :as r]))

(enable-console-print!)

(figwheel/watch-and-reload
  :websocket-url "ws://localhost:3449/figwheel-ws"
  :jsload-callback core/mount-root)

(weasel/connect "ws://localhost:9001" :verbose true)

(core/init!)
