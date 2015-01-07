(ns mu-puzzle.service.routes)

(def site-routes
  ["" {"/" {:get ::page-handler}
       "/css" {"/site.css" {:get ::site-css}}}])

(def api-routes
  ["/api" {}])
