
# A Facebook echo Chat bot

This is a Facebook chat bot testbed built using Clojure, that deploys on the Heroku PaaS. 
For documentation and step by step instructions, see the [blog post](http://kirank.in/posts-output/2016-08-02-fb-bot-heroku-clojure/)

## Running Locally

Make sure you have Clojure installed.  Also, install the [Heroku Toolbelt](https://toolbelt.heroku.com/).

```sh
$ git clone https://github.com/shark8me/heroku-cbot.git
$ cd heroku-cbot 
$ lein repl
user=> (require 'heroku-cbot.web)
user=>(def server (heroku-cbot.web/-main))
```

Your chat app webhood should now be running on [localhost:5000](http://localhost:5000/).

## Deploying to Heroku

```sh
$ heroku create
$ git push heroku master
$ heroku open
```

or

[![Deploy to Heroku](https://www.herokucdn.com/deploy/button.png)](https://heroku.com/deploy)

## Documentation

For more information about using Clojure on Heroku, see these Dev Center articles:

- [Clojure on Heroku](https://devcenter.heroku.com/categories/clojure)

