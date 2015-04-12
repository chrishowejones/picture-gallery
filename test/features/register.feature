Feature: Register
  I want to register a new user to allow me to upload pictures.

Scenario: register new user
  Given there is no user "Fred" registered
  When I register a user "Fred"
  And I type a password of "password"
  And I type a retype password of "password"
  And I click on "create account"
  Then I see the home page
  And the session contains user "Fred"

Scenario: register new user with password less than five characters
  Given there is no user "Michael" registered
  When I register a user "Michael"
  And I type a password of "mike"
  And I type a retype password of "mike"
  And I click on "create account"
  Then I should see an error message "password must be at least 5 characters"

Scenario: register new user with valid password and invalid retype password
  Given there is no user "Michael" registered
  When I register a user "Kuldeep"
  And I type a password of "kulde"
  And I type a retype password of "wadhwa"
  And I click on "create account"
  Then I should see an error message "entered passwords do not match"

Scenario: register a new user for Kerodon
  Given there is no user "Fred" registered
  When I navigate to the "register" page
  And I type:
  | field    | value     |
  | id       | Fred      |
  | pass     | password  |
  | pass1    | password  |
  And I click on button "create account"
  Then the "home" page is displayed
  And the menu contains "logout Fred"

Scenario: register with existing user
  Given user "Julian" is already registered with a password of "password"
  When I register a user "Julian"
  Then I type a password of "werty"
  And I type a retype password of "werty"
  And I click on "create account"
  Then I should see an error message "The user with id Julian already exists!"