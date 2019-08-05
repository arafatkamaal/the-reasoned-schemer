# The Reasoned Schemer

Code from the book reasoned schemer in Clojure.

## Installation

All the code is in core.clj. This is so that the entire code is in one place with all dependencies.

One should be able to load the whole code by M-x cider-restart and then M-x cider-refresh

## Usage

Go to the end of any s-expression and do a C-x C-e. Assuming you have done the same for definitions above.

Either way if you do M-x cider-restart and M-x cider-refresh they should get loaded

## How much of the reasoned schemer has been covered?

The Reasoned Schemer can't be exactly translated to Clojure because there is no 1-1 translation from scheme to Clojure.

Plenty of things like dotted pairs, lack of assq etc prevent this. Plus there are other differences highlighted here: https://github.com/clojure/core.logic/wiki/Differences-from-The-Reasoned-Schemer

## License

https://dev.perl.org/licenses/artistic.html
