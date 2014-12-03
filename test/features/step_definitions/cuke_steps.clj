(use 'picture-gallery.models.db)
(use 'clojure.test)
(use 'noir.util.crypt)
(require '[picture-gallery.handler :as handler])
(require '[ring.mock.request :refer :all])
(require '[noir.session :as session])
(require '[taoensso.timbre :as timbre])

(def user (atom {}))

(def request-map (atom {}))

(def session-user (atom ""))

(Given #"there is no user \"(.*)\" registered" [userid]
       (delete-user userid))

(Given #"^user \"([^\"]*)\" is already registered$" [userid]
       (if-not (get-user userid)
         (create-user {:id userid :pass (encrypt "password")})))

(When #"I register a user \"([^\"]*)\"" [userid]
      (reset! user {"id" userid "pass" ""}))

(When #"^I type a password of \"([^\"]*)\"$" [pass]
      (swap! user assoc "pass" pass))

(When #"^I type a retype password of \"([^\"]*)\"$" [pass]
      (swap! user assoc "pass1" pass))

(Then #"^I see the home page$" []
      ;; check redirect to /
      (let [location (get-in @request-map [:headers "Location"])
            status (@request-map :status)]
        (assert (= 302 status) (format "Expected status 302 actual %s" status))
        (assert (= "/" location) (format "Expected location / found location %s" location))))

(Then #"^the session contains user \"([^\"]*)\"$" [userid]
      (assert (= userid @session-user) (format "Expected user %s found %s" userid @session-user)))

(Then #"^I should see an error message \"([^\"]*)\"$" [expected-msg]
      (let [error-msg (first (re-seq (re-pattern (str "<div class=\"error\">" expected-msg "</div>")) (:body @request-map)))]
        
        (assert error-msg (format "Expected %s but not found" expected-msg))))

(Then #"^I click on \"([^\"]*)\"$" [button-label]
      (if (= button-label "create account")
        ;; store the result of the request sent to app
        ;; call app passing a mock request for the stored user map
        (with-redefs [session/put! (fn [key id] (reset! session-user id))]
          (reset! request-map
                  (handler/app (request :post "/register" @user))))))


(first (re-seq #"<div class=\"error\">errors message</div>" "<div class=\"error\">error message</div>")) 
