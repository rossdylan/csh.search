(ns csh.search.core
  (:gen-class)
  (:require [csh.search :as search]
            [csh.search.wiki :as wiki]
            [clojure.data.xml :as xml]
            [clojure.java.io :as io]
            [clojure.zip :as zip]
            [clojure.core.async :as async]))

(defn ingest-file
  "Lazily read and parse the given file using data.xml, then use
  produce-wikimaps to send a map representing a simplified version
  of a wiki page to the given core.async channel."
  [file channel]
  (let [wxml (-> file
                 io/file
                 xml/parse
                 zip/xml-zip)]
    (wiki/produce-wikimaps wxml channel)))

(defn ingest-stdin
  "Lazily parse xml from stdin using data.xml and then use produce-wikimaps to
  send the generated maps to a core.async channel."
  [channel]
  (let [wxml (-> *in*
                 xml/parse
                 zip/xml-zip)]
    (do
      (wiki/produce-wikimaps wxml channel))))

(defn -main
  "Main function for interacting with the system from the command line."
  [& args]
  ; mio-chan is a poorly named channel used to link the xml parser (ingest-stdin)
  ; to the solr indexer running in search/consume-maps
  (let [mio-chan (async/chan)
        indexer (async/thread (search/consume-maps mio-chan))]
    (do
      (ingest-stdin mio-chan)
      (async/<!! indexer)))) ; shim to make sure we actually index everting, this blocks forever
