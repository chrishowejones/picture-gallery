(require '[picture-gallery.models.db :refer :all])
(require '[clojure.test :refer :all])
(require '[noir.util.crypt :refer [encrypt]])
(require '[ring.mock.request :refer :all])
(require '[cucumber.runtime.clj :refer :all])
(import 'cucumber.api.Scenario)
(use 'picture-gallery.handler)
(require '[noir.session :as session])
(require '[taoensso.timbre :as timbre])
(require '[kerodon.core :as kerodon])
(require '[kerodon.test :as kertest])
(require '[clojure.reflect :refer :all])
(import 'cucumber.api.DataTable)
(require '[clojure.pprint :refer [pprint]])
(require '[clj-webdriver.taxi :refer :all])
(require '[environ.core :refer :all])

(defmacro wrap-kerodon-test
  [test_func]
  "Wrap the kerodon test in a binding to that returns the test report counters that can be tested for :pass, :fail, etc."
  `(binding [clojure.test/*report-counters* (ref clojure.test/*initial-report-counters*)]
     ~test_func
     @*report-counters*))

(defmacro successful-kerodon-expr?
  "Return true if the kerodon test function passed as arg runs without reporting a test failure."
  [test_func]
  `(let [report-counters# (wrap-kerodon-test ~test_func)]
     (successful? report-counters#)))

(def user (atom {}))

(def request-map (atom {}))

(def session-user (atom ""))

(def session-state (atom nil))

(def message (atom nil))

(def base-url (str
               "http://"
               (env :host)
               ":"
               (env :port)))

(def page-map {"home" "/"
               "register" "/register"})

(def home-page (str base-url (get page-map "home")))

(def browser {:browser :htmlunit})

(def driver (new-driver browser))

(Before []
        (if @message (reset! message nil))
        (set-driver! browser))

(After []
       (if-let [message @message]
         (pprint (str "*****" message "*****")))
       (close)
       (quit))

(defn write [message-text]
  (reset! message message-text))

(defn write-append [message-text]
  (swap! message #(str % message-text "\n")))

(defn register-user
  ([userid] (register-user userid "password"))
  ([userid pass]
   (if-not (get-user userid)
     (create-user {:id userid :pass (encrypt pass)}))))

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
                  (app (request :post "/register" @user))))))

(When #"^I navigate to the \"([^\"]*)\" page$" [page]
      (let [location (get page-map page)]
        (reset! session-state
                (-> (kerodon/session app)
                    (kerodon/visit location)))
        (kertest/has @session-state (kertest/status? 200)
                     (str "supposed to navigate to " page " page"))))

(When #"^I type:$" [table]
      (doall
       (for [item (into [] (.asMaps table))]
         (let [item-map (into {} item)
               field-selector (keyword (str "#" (item-map "field")))
               value (item-map "value")]
           (reset! session-state
                   (-> @session-state
                       (kerodon/fill-in field-selector value)))))))

(When #"^I click on button \"([^\"]*)\"$" [button-label]
      (reset!
       session-state
       (->
        (kerodon/press @session-state button-label)
        (kerodon/follow-redirect))))

(Then #"^\"([^\"]*)\" displays$" [page]
      (assert (successful-kerodon-expr? (-> @session-state
                                            (kertest/has (kertest/status? 200))))
              (str page " has returned ok"))
      (let [location (get-in @session-state [:response :headers "Location"])]
        (assert (= location (get page-map page))
                (str page " not found." @session-state))))

(Then #"^the menu contains \"([^\"]*)\"$" [item]
      (assert (successful-kerodon-expr?
               (-> @session-state
                   (kerodon/within [:.menuitem]
                                   (kertest/has (kertest/link? item))))) (str "Menu didn't contain " item)))

(Then #"^the \"([^\"]*)\" page is displayed" [page]
      (let [uri (-> @session-state
                    (kertest/has (kertest/status? 200))
                    (get-in [:request :uri]))]
        (assert (= (get page-map page) uri)
                (str "got uri " uri))))

(Given #"^user \"([^\"]*)\" is already registered with a password of \"([^\"]*)\"$" [userid password]
       (if-not (get-user userid)
         (create-user {:id userid :pass (encrypt password)})))

(Given #"^I visit the home page$" []
       (to home-page))

(When #"^I type user name \"([^\"]*)\"$" [username]
      (input-text "#id" username))

(When #"^I type a password \"([^\"]*)\"$" [password]
      (input-text "#pass" password))

(When #"^I Click-on \"([^\"]*)\"$" [id]
      (let  [xpath {:xpath (str "//input[@type='submit' and @value='" id "']")}]
        (click (find-element xpath))))

(Then #"^I cannot login$" []
      (assert (exists? "#id")))

(Then #"^I see the home page in the browser$" []
      (assert (= home-page (current-url))))

(Then #"^the menu in browser contains \"([^\"]*)\"$" [menu-item]
      (let [xpath {:xpath (str "//div[@class='menuitem']/a[text()='" menu-item "']")}]
        (exists? xpath)))
