# picture-gallery

Picture gallery application to provide the following workflows:

*       Account Registration - set up a new user account.
*       Login and logout - if not logged in display a login form, once logged in display logout button.
*       Uploading Pictures - Upload content to the site and create a thumbnail to display when listing galleries.
*       Displaying Pictures - Display thumbnails and use them as links to the full-sized pictures. Need to list users galleries.
*       Deleting Pictures - remove uploaded pictures.
*       Account Deletion - remove account and all associated pictures and thumbnails.


## Prerequisites

You will need [Leiningen][1] 1.7.0 or above installed. Also need to install [Postgresql][2].

[1]: https://github.com/technomancy/leiningen
[2]: http://www.postgresql.org/

You need to start the postgres database and create a schema called gallery with a user of admin and password of admin. Then open a REPL (e.g. lein repl) and type:

    $lein repl
    
    user> (use 'picture-gallery.models.schema)
    user> (create-users-table)
    user> (create-images-table)
    user> (quit)

These commands will create the necessary tables in the postgresql database. Once you have done this you can run the application.

## Running

To start a web server for the application, run:

    lein ring server

## License

Copyright © 2014 Eclipse
