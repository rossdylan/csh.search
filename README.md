# csh.search
## Indexing system for a Computer Science House Search Engine.

A Clojure library for taking a bunch of different CSH data sources and indexing it into solr.
Support is planned for:
[x] The CSH MediaWiki instance.
[ ] Webnews
[ ] Other things..?

## Usage

```
cat mediawikidump.xml | java -jar <csh.search jar file>
```

## Warnings

* This is really really alpha. Currently it works on my machine with only mediawiki xml. The plan is to expand it slowly into a system that can index
a bunch of different things. However right now it is not that.

* It should also be noted that this software is going to be pretty CSH specific, and probably won't work for usecases outside of that.
## License

Copyright © 2014 Ross Delinger

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
