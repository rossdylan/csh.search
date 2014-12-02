(ns csh.search.wiki
  (:require [clojure.xml :as xml]
            [clojure.zip :as zip]
            [clojure.data.zip.xml :as zip-xml]
            [clojure.core.async :as async]))


(defn extract-text
  "Extract the textual data from an xml path specified using the given predicates.
  Documentation for the predicates is found in clojure.data.zip.xml."
  [loc & pred]
  (map zip-xml/text (apply zip-xml/xml-> loc pred)))

(defn clean-title
  "Clean the title of bad characters so we can turn it into a url later."
  [title]
  (clojure.string/replace title #" " "_"))

(defn create-wikimap
  "Take the title and text of a wiki and make a map with all the extra bits
  we want to add to solr (id, url, etc)."
  [title text]
  (let [ct (clean-title title)]
     {:title title
      :text text
      :url (str "https://wiki.csh.rit.edu/wiki/" ct)
      :id (str "wiki_" ct)}))

(defn produce-wikimaps
  "Zip up the page titles with the page contents extracted using zippers from the wiki xml dump.
  Then put it into a channel to be used somewhere else."
  [wxml channel]
  (dorun
    (map
      (fn [title text] (async/>!! channel
                                  (create-wikimap title text)))
      (extract-text wxml :page :title)
      (extract-text wxml :page :revision :text))))
