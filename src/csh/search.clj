(ns csh.search
  (:require [flux.http :as http]
            [flux.core]
            [flux.query]
            [clojure.core.async :as async]))

(def conn (http/create "http://localhost:8984/solr" :collection1))

(defn flush-buffer
  "Flush the contents of the given buffer out to solr using flux.core/add.
  We then clear the buffer and reset the counter to 0."
  [buffer counter]
  (cond (> @counter 0) (do
                        (flux.core/with-connection conn
                          (flux.core/add @buffer)
                          (flux.core/commit))
                        (swap! buffer (fn [b] []))
                        (swap! counter (fn [c] 0N)))))

(defn append-buffer
  "Add a map representing a document to be indexed by solr to the buffer and increment our counter.
  If the buffer has reached a given size (currently hard coded at 50) flush the buffer out to solr
  using flush-buffer."
  [value buffer counter]
  (do
    (swap! buffer #(conj % value))
    (swap! counter inc)
    (println (str "Added " (:title value)))
    (cond (>= @counter 50) (flush-buffer buffer counter))))

(defn consume-maps
  "Block on the given channel and index all the maps that come out of it.
  The main while loop executes in a thread and then each call to <! is put
  into a go block."
  [channel]
  ; Awww yeah! atomic buffer and counter, this is how we batch operations
  (let [buffer (atom []) counter (atom 0N)]
    (while true
        ; use alts!! and timeout to fill the buffer and auto flush after a period of time
        (let [[v c] (async/alts!! [channel (async/timeout 10000)])]
          (async/go
            (if (= c channel)
              ; First case is if we recieved something so we add it
              ; to the buffer, increment the counter and move kon with our lives
              (append-buffer v buffer counter)
              ; If we reach this point we have timed out, dump our buffer to solr
              (flush-buffer buffer counter)))))))

