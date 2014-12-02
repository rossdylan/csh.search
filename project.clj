(defproject csh.search "0.1.0-SNAPSHOT"
  :description "Internal search engine for Computer Science House"
  :url "https://github.com/rossdylan/csh.search"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.clojure/core.async "0.1.346.0-17112a-alpha"]
                 [com.codesignals/flux "0.5.0"]
                 [org.clojure/data.xml "0.0.8"]
                 [org.clojure/data.zip "0.1.1"]
                 [org.clojure/tools.trace "0.7.8"]
                 [org.slf4j/slf4j-log4j12 "1.7.7"]
                 [commons-logging "1.1.3"]]
  :main csh.search.core)
