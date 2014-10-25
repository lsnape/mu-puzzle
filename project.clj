(defproject mu-puzzle ""

  :description "A game based on Douglas Hofstadter's MU puzzle in GEB"
  :url ""
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  
  :dependencies [[org.clojure/clojure "1.7.0-alpha2"]

                 [ring/ring-core "1.2.0"]
                 [compojure "1.1.6"]
                 [hiccup "1.0.5"]
                 [medley "0.5.0"]

                 [prismatic/om-tools "0.3.6"]
                 [sablono "0.2.22"]

                 [org.clojure/clojurescript "0.0-2371"]
                 [org.clojure/core.async "0.1.303.0-886421-alpha"]
                 [om "0.8.0-alpha1"]]

  :plugins [[jarohen/lein-frodo "0.3.2"]
            [lein-cljsbuild "1.0.3"]
            [lein-pdo "0.1.1"]

            [com.keminglabs/cljx "0.4.0"]
            [lein-shell "0.4.0"]]

  :frodo/config-resource "mu-puzzle-config.edn"

  :source-paths ["src" "target/generated/clj"]

  :resource-paths ["resources" "target/resources"]

  :cljx {:builds [{:source-paths ["common-src"]
                   :output-path "target/generated/clj"
                   :rules :clj}

                  {:source-paths ["common-src"]
                   :output-path "target/generated/cljs"
                   :rules :cljs}]}

  :cljsbuild {:builds {:dev
                       {:source-paths ["ui-src" "target/generated/cljs"]
                        :compiler {:output-to "target/resources/js/mu-puzzle.js"
                                   :output-dir "target/resources/js/"
                                   :optimizations :none
                                   :source-map "target/resources/js/mu-puzzle.js.map"
                                   :pretty-print true}}

                       :prod
                       {:source-paths ["ui-src" "target/generated/cljs"]
                        :compiler {:output-to "target/resources/js/mu-puzzle.js"
                                   :optimizations :advanced
                                   :pretty-print false
                                   :externs ["externs/jquery.js"]}}}}

  :aliases {"dev" ["do"
                   ["shell" "mkdir" "-p"
                    "target/generated/clj"
                    "target/generated/cljs"
                    "target/resources"]
                   ["cljx" "once"]
                   ["pdo"
                    ["cljx" "auto"]
                    ["cljsbuild" "auto" "dev"]
                    "frodo"]]
            
            "start" ["do"
                     ["cljx" "once"]
                     ["cljsbuild" "once" "prod"]
                     ["trampoline" "frodo"]]})
