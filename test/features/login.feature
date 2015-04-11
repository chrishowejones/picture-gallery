Feature: Login
  As a user I want to log in.

Scenario: Log in with valid credentials
  Given user "Julian" is already registered with a password of "password"
  And I visit the home page
  When I type user name "Julian"
  And I type a password "password"
  And I Click-on "login"
  Then I see the home page in the browser
  And the menu in browser contains "logout Julian"

Scenario: Log in with invalid credentials
  Given user "Julian" is already registered with a password of "password"
  And I visit the home page
  When I type user name "Julin"
  And I type a password "julia"
  And I Click-on "login"
  Then I cannot login
  And I see the home page in the browser

Scenario: Log in with a blank password.
  Given user "Julian" is already registered with a password of "password"
  And I visit the home page
  When I type user name "Julian"
  And I Click-on "login"
  Then I cannot login
  And I see the home page in the browser

Scenario: Log in with a blank user name

Scenario: Log in with empty user name and password

Scenario: Log in with valid user name and invalid password

Scenario: Log in with invalid user name and valid password
