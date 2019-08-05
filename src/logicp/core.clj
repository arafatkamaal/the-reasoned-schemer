(ns logicp.core
  (:gen-class)
  (:require [clojure.core.logic :as cl]))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))

(cl/run* [q]
  (cl/membero q [1 2 3])
  (cl/membero q [2 3 4]))

(cl/run* [q]
  (cl/== q true))

(cl/run 1 [q]
  (cl/== q 1))

(cl/run* [q]
  (cl/== q 2))

(cl/run* [q]
  (cl/fresh [a]
    (cl/membero a [1 2 3])
    (cl/membero q [3 4 5])
    (cl/== a q)))

(cl/run* [q]
  (cl/conde
   [cl/succeed]
   [cl/fail]))

;; ((1 2 3))
(cl/run* [q]
  (cl/conso 1 [2 3] q))

;; ((2 3))
(cl/run* [q]
  (cl/conso 1 q [1 2 3]))

;; (1)
(cl/run* [q]
  (cl/conso q [2 3] [1 2 3]))

;; (2)
(cl/run* [q]
  (cl/conso 1 [q 3] [1 2 3]))

;; ([2 3])
(cl/run* [a b]
  (cl/conso 1 [a b] [1 2 3]))

;; ((2 3 4))
(cl/run* [q]
  (cl/resto [1 2 3 4] q))

;;((1 2 3 4 5 6))
(cl/run* [q]
  (cl/appendo [1 2 3] [4 5 6] q))

;;((3 5))
(cl/run* [a b]
  (cl/appendo [1 2 a] [4 b 6] [1 2 3 4 5 6]))

(cl/run 2 [q]
  (cl/appendo [q 2 3] [4 5 6] [[1 2] 2 3 4 5 6]))

(cl/run 2 [q]
  (cl/== q (take 10 (range)))
  (cl/appendo [q 2 3] [4 5 6] [q 2 3 4 5 6]))

;; The reasoned schemer starts here.

;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Chapter 1 - Playthings ;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;


;; This is a goal that succeeds
(cl/succeed 1)
;; Return the the parameter that comes after

;; Does this succeed for other things as well?
(cl/succeed false)
;; Returns false of course

;; Fails so returns nil
(cl/fail 1)

;; Gives exception
(comment
  (cl/run* [q]
    (cl/fail 1)))

;; Both goals fail
(cl/run* [q]
  cl/fail 1)

(cl/run* [q]
  cl/fail
  (cl/== true q))

;; Returns true
(cl/run* [q]
  cl/succeed
  (cl/== true q))

;; Returns (1)
(cl/run* [q]
  cl/succeed
  (cl/== 1 q))

;;(corn)
(cl/run* [q]
  cl/succeed
  (cl/== (quote corn) q))

;;((corn))
(cl/run* [q]
  cl/succeed
  (cl/== '(corn) q))

;; Because goal fails
(cl/run* [q]
  cl/fail
  (cl/== (quote corn) q))

;; (false)
(cl/run* [q]
  cl/succeed
  (cl/== false q))

;; succeeds
(let [x false]
  (cl/== false x))

;; () because x is false, and we ask what it could take for x to be true, which is nothing. Given x is already false
(cl/run* [x]
  (let [x false]
    (cl/== true x)))

;; (true) because we ask what value of q would it take it make it equal to true, which is true, x has no effect
;; Both x and q are fresh by the way
(cl/run* [q]
  (cl/fresh [x]
    (cl/== true x)
    (cl/== true q)))

;; (true) because order doesn't matter 
(cl/run* [q]
  (cl/fresh [x]
    (cl/== x true)
    (cl/== q true)))

;;(_0) a symbol representing a fresh variable
(cl/run* [q]
  cl/succeed)

;;(_0)
;;  because x returned is neither
;;           a. introduced by let
;;           b. introduced by run
;;  it is introduced by fresh. so its a fresh variable
(cl/run* [x]
  (let [x false]
    (cl/fresh [x]
      (cl/== true x))))

;;((_0 _1)) because we ask what is q if its equal to x and y cons'd. And x and y are fresh
(cl/run* [q]
  (cl/fresh [x y]
    (cl/== (cons x (cons y '())) q)))

;;((_0 _1)) because names of fresh variables don't matter
(cl/run* [q]
  (cl/fresh [a b]
    (cl/== (cons a (cons b '())) q)))

;;((_0 _1 _0)) because y is x, and x is _0 from first fresh, and x is _1 from the second fresh
(cl/run* [r]
  (cl/fresh [x]
    (let [y x]
      (cl/fresh [x]
        (cl/== (cons y (cons x (cons y '()))) r)))))

;; (), because we ask for what value of q is q both true and false, which none- there fore ()
(cl/run* [q]
  (cl/== false q)
  (cl/== true q))

;; (false), because in both the clauses we ask if there is value of q, where q is false, there is, which is (false)
(cl/run* [q]
  (cl/== false q)
  (cl/== false q))

;; (true), becase x and q are same, and x is (true) 
(cl/run* [q]
  (let [x q]
    (cl/== true x)))

;;(_0) because we have asked for what value or r, is x and r same. Answer: In one case where x is fresh. there fore r gets fresh x(_0)
(cl/run* [r]
  (cl/fresh [x]
    (cl/== x r)))

;;(true), because
;;    a. x is fresh
;;    b. x is then true
;;    c. x is unified with q
;; Obviously now q is true
(cl/run* [q]
  (cl/fresh [x]
    (cl/== true x)
    (cl/== x q)))


;; (true)
;;    Same as above example and
;;        a. x is fresh
;;        b. x is unified with q
;;        c. x is unified with true
;;        d. q gets x, so q is true
(cl/run* [q]
  (cl/fresh [x]
    (cl/== x q)
    (cl/== x true)))


;; (false)
;;     because
;;         a. x and q are different. Hence (= x q) returns false
;;         b. That false is unified with q
;;  This example is done to show every fresh introduces a different variable. run* is a kind of fresh so that too.
(cl/run* [q]
  (cl/fresh [x]
    (cl/== (= x q) q)))

;; (_0)
;;     because
;;         a. q in the last line is the q introduced by fresh a line above it. Which would be _0
;;         b. x was made q in the let statement. But that q and q introduced by fresh are different.
;;         c. The last q gets returned.
(cl/run* [q]
  (let [x q]
    (cl/fresh [q]
      (cl/== (= x q) q))))

;; LESSON : EVERY FRESH RETURNS A NEW VARIABLE

;; false, obviously
(cond
  false true
  :else false)

;; nil, because else is executed.
(cond
  false (cl/succeed true)
  :else (cl/fail false))

;; Doesn't succeed as both fail
(cl/conde
 [(cl/fail cl/succeed)]
 [(cl/fail false)])

;; true, as second succeeds.
(cl/conde
 [(cl/fail false)]
 [(cl/succeed true)])


;; CONDE IS OR BETWEEN [] AND AND INSIDE ()


;; (olive oil)
;;    because the first two goals suceed, olive comes from first, and oil comes from the second
(cl/run* [x]
  (cl/conde
   [(cl/== 'olive x) cl/s#]
   [(cl/== 'oil   x) cl/s#]
   [cl/u#]))

;; (olive), because we asked for the first line the succeeded
(cl/run 1 [x]
  (cl/conde
   [(cl/== 'olive x) cl/s#]
   [(cl/== 'oil   x) cl/s#]
   [cl/u#]))

;;(olive _0 oil)
;;    because
;;        a. first fails so pure is not included
;;        b. second succeeds so olive is included
;;        c. third succeeds so a fresh variable(_0) is added
;;        d. fourth succeeds so oil is included
;;        e. fifth fails so not included
(cl/run* [x]
  (cl/conde
   [(cl/== 'pure  x) cl/u#]
   [(cl/== 'olive x) cl/s#]
   [cl/s# cl/s#]
   [(cl/== 'oil   x) cl/s#]
   [cl/u#]))

;;(pure olive extra oil) because all succeed
(cl/run* [x]
  (cl/conde
   [(cl/== 'pure  x) cl/s#]
   [(cl/== 'olive x) cl/s#]
   [(cl/== 'extra x) cl/s#]
   [(cl/== 'oil   x) cl/s#]
   [cl/u#]))

;;(pure olive) because we asked for only first two
(cl/run 2 [x]
  (cl/conde
   [(cl/== 'pure  x) cl/s#]
   [(cl/== 'olive x) cl/s#]
   [(cl/== 'extra x) cl/s#]
   [(cl/== 'oil   x) cl/s#]
   [cl/u#]))

;; ((split pea))
(cl/run* [r]
  (cl/fresh [x y]
    (cl/== 'split x)
    (cl/== 'pea   y)
    (cl/== (cons x (cons y '())) r)))

;;((split pea) (navy bean))
(cl/run* [r]
  (cl/fresh [x y]
    (cl/conde
     [(cl/== 'split x) (cl/== 'pea  y)]
     [(cl/== 'navy  x) (cl/== 'bean y)]
     [cl/u#])
    (cl/== (cons x (cons y '())) r)))

;;((split pea soup) (navy bean soup))
(cl/run* [r]
  (cl/fresh [x y]
    (cl/conde
     [(cl/== 'split x) (cl/== 'pea  y)]
     [(cl/== 'navy  x) (cl/== 'bean y)]
     [cl/u#])
    (cl/== (cons x (cons y (cons 'soup '()))) r)))

;; Lets begin to work with functions
(def teacupo
  (fn [x]
    (cl/conde
     [(cl/== 'tea x) cl/s#]
     [(cl/== 'cup x) cl/s#]
     [cl/u#])))

;; (tea cup) 
(cl/run* [x]
  (teacupo x))

;; x is (false tea cup)
;; Because
;;    a. from first statement x is (tea cup)
;;    b. from second statement x is false
;; net: (false tea cup)
(cl/run* [r]
  (cl/fresh [x y]
    (cl/conde
     [(teacupo x)     (cl/== true y) cl/s#]
     [(cl/== false x) (cl/== true y)]
     [cl/u#])
    (cl/== x r)))

;; y is (true true true)
;;  Because true unifications from all statements
(cl/run* [r]
  (cl/fresh [x y]
    (cl/conde
     [(teacupo x)     (cl/== true y) cl/s#]
     [(cl/== false x) (cl/== true y)]
     [cl/u#])
    (cl/== y r)))

;;((false true) (tea true) (cup true))
;;  consing the above two s expressions
(cl/run* [r]
  (cl/fresh [x y]
    (cl/conde
     [(teacupo x)     (cl/== true y) cl/s#]
     [(cl/== false x) (cl/== true y)]
     [cl/u#])
    (cl/== (cons x (cons y '())) r)))

;;((_0 _1) (_0 _0))
(cl/run* [r]
  (cl/fresh [x y z]
    (cl/conde
     [(cl/== y x) (cl/fresh [x]
                    (cl/== z x))]
     [(cl/fresh [x]
        (cl/== y x)
        (cl/== z x))]
     [cl/u#])
    (cl/== (cons y (cons z '())) r)))

;;(false)
(cl/run* [q]
  (let [a (cl/== true  q)
        b (cl/== false q)]
    b))

;;(false)
(cl/run* [q]
  (let [a (cl/== true q)
        b (cl/fresh [x]
            (cl/== x     q)
            (cl/== false x))
        c (cl/conde
           [(cl/== true  q)]
           [(cl/== false q)])]
    b))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; 2. Teaching Old Toys New Tricks  ;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;; c , obviously
(let [x (fn [a] a)
      y 'c]
  (x y))

;;((_0 _1))
;;  Because y and x are both fresh
(cl/run* [r]
  (cl/fresh [y x]
    (cl/== (cons x (cons y '())) r)))

;;((_0 _1))
;; because
;;    a. v and w are fresh variables, which means they are _0 and _1
;;    b. let associates them with x and y, which means x and y are now _0 and _1
;;    c. They are cons'd and and unified with r
(cl/run* [r]
  (cl/fresh [v w]
    (cl/== (let [x v
                 y w]
             (cons x (cons y '()))) r)))

;; grape, from elementary clojure
(first '(grape raisin pear))

;; a, from elementary clojure
(first '(a c o r n))

;; (a), because firsto is the logical version of first
(cl/run* [r]
  (cl/firsto '(a c o r n) r))

;; (true), because we asked for what value of q, would q be true
(cl/run* [q]
  (cl/fresh [a]
    (cl/firsto '(a c o r n) a)
    (cl/== true q)))

;; (pear)
;;  Because
;;      a. from the first statement r is x
;;      b. from the second statement x is pear
;;  Therefore r is pear.
(cl/run* [r]
  (cl/fresh [x y]
    (cl/firsto (cons r (cons y '())) x)
    (cl/== 'pear x)))

;; Lets define our-firsto
(def our-firsto
  (fn [p a]
    (cl/fresh [d]
      (cl/== (cons a d) p))))

;; (grape a), obviously
(cons
 (first '(grape raisin pear))
 (first '((a) (b) (c))))

;; ((grape a)), obviously, but the above and below structures are same.
(cl/run* [r]
  (cl/fresh [x y]
    (cl/firsto '(grape raisin pear) x)
    (cl/firsto '((a) (b) (c))       y)
    (cl/== (cl/lcons x y) r)))

;; (raisin pear), obviously
(rest '(grape raisin pear))

;; c obviously
(first (rest '(a c o r n)))

;; c, basically shows how to arrive at the above.
(cl/run* [r]
  (cl/fresh [v]
    (cl/resto '(a c o r n) v)
    (cl/firsto v r)))

;; This is how resto is defined
(def our-resto
  (fn [p d]
    (cl/fresh [a]
      (cl/== (cons a d) p))))

;;((raisin pear) a) obviously
(cons
 (rest  '(grape raisin pear))
 (first '((a) (b) (c))))

;;(((raisin pear) a)), basically shows how to arrive at the above
(cl/run* [r]
  (cl/fresh [x y]
    (cl/resto  '(grap raisin pear) x)
    (cl/firsto '((a) (b) (c))      y)
    (cl/== r (cl/lcons x y))))

;; true, because we asked for what value of q, is q same as true
(cl/run* [q]
  (cl/resto '(a c o r n) '(c o r n))
  (cl/== true q))

;; (o)
;;  Because
;;     a. resto of corn is o  r n
;;     b. we asked         x? r n
;;  which is ofcourse o
(cl/run* [x]
  (cl/resto '(c o r n) (list x 'r 'n)))

;;((a c o r n))
;;  Because
;;     a. if resto of l is (c o r n), then l must be (? c o r n)
;;     b.  then we state firsto of l which is ? is x
;;     c.  then we say x is 'a
;;     d.  ? is therefore a
;;     e.  l becomes (a c o r n) from (? c o r n)
(cl/run* [l]
  (cl/fresh [x]
    (cl/resto  l '(c o r n) )
    (cl/firsto l x)
    (cl/== 'a x)))

;;(((a b c) d e))
;;  Because we asked what should l be if it had to be a cons of (a b c) (d e)
(cl/run* [l]
  (cl/conso '(a b c) '(d e) l))

;;(d)
;;  Because:
;;    a. we asked (cons ? '(a b c)) gives (d a b c)
;;    b. ? is obviously d
(cl/run* [x]
  (cl/conso x '(a b c) '(d a b c)))

;;((e a d c))
;; Why???? lets see in the next few snippets
(cl/run* [r]
  (cl/fresh [x y z]
    (cl/== (list 'e 'a 'd x) r)
    (cl/conso y (list 'a z 'c) r)))

;; z is d
(cl/run* [r s]
  (cl/fresh [x y z]
    (cl/== (list 'e 'a 'd x) r)
    (cl/conso y (list 'a z 'c) r)
    (cl/== z s)))

;; y is e
(cl/run* [r s]
  (cl/fresh [x y z]
    (cl/== (list 'e 'a 'd x) r)
    (cl/conso y (list 'a z 'c) r)
    (cl/== y s)))

;; Basically
;;    a. from first statement, r is (e a d x?)
;;    b. from the second statement cons of y and (a z? c)
;;          (e  a d  x?)
;;          (y? a z? c)
;;    c. We infer y is e, z is d, and x is c
;;    d. Lets fill in the value of x from point a.
;;    e. (e a d x?) becomes (e a d c)


;; (d)
;;  Because we asked what should x be if we cons'd it with (a x c) and produced (d a x c)
;;       (cons  (x?) (a x? c)) = (d a x c)
;;         x is d
(cl/run* [x]
  (cl/conso x (list 'a x 'c) (list 'd 'a x 'c)))

;; ((d a d c))
;;    Because
;;       a. From first statement l is (d a x? c)
;;       b. Next, we say consing x? and (a x? c) gives l
;;       c. So obviously x is d
;;       d. Lets fill up x? from step a. (d a x? c) becomes (d a d c)
(cl/run* [l]
  (cl/fresh [x]
    (cl/== (list 'd 'a x 'c) l)
    (cl/conso x (list 'a x 'c) l)))

;;((b e a n s))
;;    Because, statements:
;;      1. cons w and '(a n s) produce s
;;      2. rest of l produces s, which means rest of l is '(a n s)
;;      3. first of l is x
;;      4. x is 'b .... so 
(cl/run* [l]
  (cl/fresh [d x y w s]
    (cl/conso w '(a n s) s)
    (cl/resto l s)
    (cl/firsto l x)
    (cl/== 'b x)
    (cl/resto l d)
    (cl/firsto d y)
    (cl/== 'e y)))

;; false
(empty? '(grape raisin pear))

;; true
(empty? '())

;; (), because first goal fails?
(cl/run* [q]
  (cl/emptyo '(grape raisin pear))
  (cl/== true q))

;; (true), from the first goal?
(cl/run* [q]
  (cl/emptyo '())
  (cl/== true q))

;;(()), why?
(cl/run* [x]
  (cl/emptyo x))

;; Basically emptyo asks what should the value of x be, if it has to be '()
(def our-emptyo
  (fn [x]
    (cl/== '() x)))

;; false obviously
(= 'plum 'pear)

;; true obviously
(= 'plum 'plum)

;; definition
(def eqo
  (fn [x y]
    (cl/== x y)))

;; ()
(cl/run* [q]
  (eqo 'pear 'plum)
  (cl/== true q))

;; (true)
(cl/run* [q]
  (eqo 'pear 'pear)
  (cl/== true q))

;; (pear)
;;   q has to be pear, eqo tells what q has to be, to be the same as x
(cl/run* [q]
  (cl/fresh [x]
    (cl/== x 'pear)
    (eqo x q)))

;; Give a pair (split . pea)
(cl/llist 'split 'pea)

;; (split . x)
(cl/llist 'split 'x)

(def split-x
  (cl/llist '(split) 'pea))
;;split-x

;; definition
(defn pair? [x]
  (or (cl/lcons? x)
      (and (coll? x) (seq x))))

;; true
(pair? split-x)

;; nil
(pair? '())

;; false
(pair? 'pear)

;; false
(pair? 'pair)

;; (pear)
(pair? '(pair))

;; pear
(first '(pear))

;; ()
(rest '(pear))

;; ((split) . pea)
(cl/lcons '(split) 'pea)

;; ((_0 _1 . salad))
(cl/run* [r]
  (cl/fresh [x y]
    (cl/== (cl/lcons x (cl/lcons y 'salad)) r)))

;; definition
(def pairo
  (fn [p]
    (cl/fresh [a d]
      (cl/conso a d p))))

;; (true)
(cl/run* [q]
  (pairo (cl/lcons q q))
  (cl/== true q))

;; () 
(cl/run* [q]
  (pairo '())
  (cl/== true q))

;; ()
(cl/run* [q]
  (pairo 'pair)
  (cl/== true q))

;; ((_0 _1))
(cl/run* [x]
  (pairo x))

;; (_0)
(cl/run* [r]
  (pairo (cl/lcons r 'pear)))


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; 3. Seeing old friends in new ways;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;; true
(list? '(1 3 3 4))

;; true
(list? '((a) (a b) c))

;; false
(list? 's)

;; true
(seq? '((a) (a b) c))

;; true
(seq? ())

;; false
(seq? 's)

;;(d a t e . s)
(cl/llist 'd 'a 't 'e 's)

;; false, because its a not a proper list
(seq? (cl/llist 'd 'a 't 'e 's))

;; We use seqo, instead of listo, which is more consistent with our naming
(def seqo
  (fn [l]
    (cl/conde
     [(cl/emptyo l) cl/s#]
     [(pairo l) (cl/fresh [d]
                  (cl/resto l d)
                  (seqo d))]
     [cl/u#])))

;; (_0)
;; Because, _0 was introduced by a fresh variable
(cl/run* [x]
  (seqo (list 'a 'b x 'd)))

;; (())
;; Because, ('a 'b 'c . x) becomes a proper list if x is ()
(cl/run 1 [x]
  (seqo (cl/llist 'a 'b 'c x)))

;; Whoa! too many values
(cl/run* [x]
  (seqo (cl/llist 'a 'b 'c x)))

;; some of those two many values, (() (_0) (_0 _1) (_0 _1 _2) (_0 _1 _2 _3))
(cl/run 5 [x]
  (seqo (cl/llist 'a 'b 'c x)))

;; List of lists
(def lolo
  (fn [l]
    (cl/conde
     [(cl/emptyo l) cl/s#]
     [(cl/fresh [a]
        (cl/firsto l a)
        (seqo a))
      (cl/fresh [d]
        (cl/resto l d)
        (lolo d))]
     [cl/u#])))

;; Lots of values
(cl/run* [l]
  (lolo l))

;; (true)
(cl/run* [q]
  (cl/fresh [x y]
    (lolo (list '(a b) (list x 'c) (list 'd y)))
    (cl/== true q)))

;; (true)
(cl/run 1 [q]
  (cl/fresh [x]
    (lolo (cl/llist '(a b) x))
    (cl/== true q)))

;;(()) because
;;    we asked for what value of x, would ('(a b) '(c d) . x) be list of list
(cl/run 1 [x]
  (lolo (cl/llist '(a b) '(c d) x)))

;;  (() (()) ((_0)) (() ()) ((_0 _1))) , possible values to make it a list of list
(cl/run 5 [x]
  (lolo (cl/llist '(a b) '(c d) x)))

;; A twin
(list 'tofu 'tofu)

;; Not a twin
(list 'e 'tofu)

;; Not a twin dude, a triplet
(list 'g 'g 'g)

;; List of twins
(list '(g g) '(tofu tofu))

(def twinso
  (fn [s]
    (cl/fresh [x y]
      (cl/conso x y s)
      (cl/conso x '() y))))

;; Some examples
;; (tofu) because we asked what should q be, if the (tofu q) has to be a twin
(cl/run* [q]
  (twinso (list 'tofu q)))

;; true
(cl/run* [q]
  (twinso '(tofu tofu)) 
  (cl/== true q))

;; (tofu) obviously
(cl/run* [z]
  (twinso (list z 'tofu)))

;; twinso without conso
;;;;  we ask, for the answer to be s, what should an equal pair (x x) look like
(comment
  (def twinso
    (fn [s]
      (cl/fresh [x]
        (cl/== (list x x) s)))))

(def loto
  (fn [l]
    (cl/conde
     ;; if the list is empty just succeed
     [(cl/emptyo l) cl/s#]
     ;; lets check if the first element of a list is a twin
     [(cl/fresh [a]
        ;; first associate a with the first element of the list
        (cl/firsto l a)
        ;; check if a is a twin
        (twinso a))
      ;; AND lets if the the remaining elements of a list are pairs
      (cl/fresh [d]
        ;; first associate d with the remaining list
        (cl/resto l d)
        ;; call the same function recursively on d(remaining elements of the list)
        (loto d))]
     [cl/s# cl/u#])))

;; Lets start testing this
;;  (()) because that is what z has to be for the expression ((g g) . z) to be a list of twins
(cl/run 1 [z]
  (loto (cl/llist '(g g) z)))

(cl/run 5 [z]
  (loto (cl/llist '(g g) z)))

;; Another simple
;;  q has to be a for the expression to be a list of twins
(cl/run 1 [q]
  (loto (list (list 'g 'g) (list 'a q))))

(cl/run 5 [r]
  (cl/fresh [w x y z]
    (loto (cl/llist '(g g) (list 'e w) (list x y) z))
    (cl/== (list w (list x y) z) r)))

(cl/run 3 [out]
  (cl/fresh [w x y z]
    (cl/== (cl/llist '(g g) (list 'e w) (list x y) z) out)
    (loto out)))

;; definition
;; take a list, on each element of the list do a operation
(def listofo
  (fn [predo l]
    (cl/conde
     [(cl/emptyo l) cl/s#]
     [(cl/fresh [a]
        (cl/firsto l a)
        (predo a))
      (cl/fresh [d]
        (cl/resto l d)
        (listofo predo d))]
     [cl/s# cl/u#])))

(cl/run 3 [out]
  (cl/fresh [w x y z]
    (cl/== (cl/llist '(g g) (list 'e w) (list x y) z) out)
    (listofo twinso out)))

;; Redefining list of twins using listofo
(comment
  (def loto
    (fn [l]
      (listofo twinso l))))

;; Lets define member function recusively
(def eq-first?
  (fn [l x]
    (= (first l) x)))

(def member?
  (fn [l x]
    (cond
      (nil?      l)   false
      (eq-first? l x) true
      :else           (member? (next l) x))))

;; true
(eq-first? '(apple banana cherry) 'apple)

;; false
(eq-first? '(tiger lion zebra) 'lion)

;; true
(member? '(tiger lion zebra jackal) 'jackal)

;; false
(member? '(tiger lion zebra jackal) 'wolf)

;; Lets define the logic equivalents of above functions
(def eq-firsto
  (fn [l x]
    (cl/firsto l x)))

(def our-membero
  (fn [x l]
    (cl/conde
     [(cl/emptyo l) cl/u#]
     [(eq-firsto l x) cl/s#]
     [(cl/fresh [d]
        (cl/resto l d)
        (our-membero x d))])))

;; (true)
(cl/run* [q]
  (cl/membero 'olive '(virgin olive oil))
  (cl/== true q))

;; ()
(cl/run* [q]
  (cl/membero 'tiger '(virgin olive oil))
  (cl/== true q))

;; (hummus)
;;    because we asked for what value of y would membero return true, which is one of (hummus with pita)
(cl/run 1 [y]
  (cl/membero y '(hummus with pita)))

;; (hummus with pita)
;;   because we asked for what all of the values of y, would membero return true for (hummus with pita)
(cl/run* [y]
  (cl/membero y '(hummus with pita)))

;; (with)
;;    because see reasons above
(cl/run 1 [y]
  (cl/membero y '(with pita)))

;; (pita)
;;    because see reasons above
(cl/run 1 [y]
  (cl/membero y '(pita)))

;; ()
;;    because y has to be () to be a member of ()
(cl/run 1 [y]
  (cl/membero y '()))

;; will always return the value of l
(comment
  (cl/run* [y]
    (cl/membero y l)))

;;therefore
(def identity
  (fn [l]
    (cl/run* [y]
      (cl/membero y l))))

;; (a a)
;;;; simple test
(identity '(a a))

;; (e)
;;    Because we asked for what value of x would e be a member of (pasta ? fagioli)
(cl/run* [x]
  (cl/membero 'e (list 'pasta x 'fagioli)))

;; (_0)
;;    Becase we asked for what value of x would 'e be a member of (pasta e ? fagioli)
;;    The answer is anything, because x could be anything, e is already a part of that list
;;       Also recursion succeeds before it get to x
(cl/run 1 [x]
  (cl/membero 'e (list 'pasta 'e x 'fagioli)))

;; (e)
;;    Suprised?
;;     Because x comes first and membero thinks x can be e for membero to succeed
;;        Also recursion succeeds when it gets to x
(cl/run 1 [x]
  (cl/membero 'e (list 'pasta x 'e 'fagioli)))

;; ((e _0) (_0 e))
;;    in the first statement x needs to be 'e and y is fresh
;;    in the second statement, y gets x association, and x is refreshed
(cl/run* [r]
  (cl/fresh [x y]
    (cl/membero 'e (list 'pasta x 'fagioli y))
    (cl/== (list x y) r)))

;; ((tofu . _0))
;;   Because for tofu to be a member of a fresh l(_0), it has to be the first element of the list (tofu . _0) 
(cl/run 1 [l]
  (cl/membero 'tofu l))

;; Will run infinitely, becase there are infinite empty values for l, for which the argument could come true
(comment
  (cl/run* [l]
    (cl/membero 'tofu l)))

;; ((tofu . _0) (_0 tofu . _1) (_0 _1 tofu . _2) (_0 _1 _2 tofu . _3) (_0 _1 _2 _3 tofu . _4))
;;;; all 5 possible values 
(cl/run 5 [l]
  (cl/membero 'tofu l))

;; definition member of proper list?
(def pmembero
  (fn [x l]
    (cl/conde
     [(cl/emptyo l) cl/u#]
     [(eq-firsto l x) (cl/resto l '())]
     [(cl/fresh [d]
        (cl/resto l d)
        (pmembero x d))])))

;; ((tofu) (_0 tofu) (_0 _1 tofu) (_0 _1 _2 tofu) (_0 _1 _2 _3 tofu))
;;     Results of the previous expression with pmembero
(cl/run 5 [l]
  (pmembero 'tofu l))

;; (true)
(cl/run* [q]
  (pmembero 'tofu (list 'a 'b 'tofu 'd 'tofu))
  (cl/== true q))

;; redefinition
(def redefined-pmembero
  (fn [x l]
    (cl/conde
     [(cl/emptyo l)   cl/u#]
     [(eq-firsto l x) (cl/resto l ())]
     [(eq-firsto l x) cl/s#]
     [cl/s# (cl/fresh [d]
              (cl/resto l d)
              (redefined-pmembero x d))])))

;; (true true true)
(cl/run* [q]
  (redefined-pmembero 'tofu (list 'a 'b 'tofu 'd 'tofu))
  (cl/== true q))

(def reredefined-pmembero
  (fn [x l]
    (cl/conde
     [(cl/emptyo l)    cl/u#]
     [(cl/firsto l x) (cl/resto l ())]
     [(cl/firsto l x) (cl/fresh [a d]
                        (cl/resto l (cl/llist a d)))]
     [cl/s#           (cl/fresh [d]
                        (cl/resto l d)
                        (reredefined-pmembero x d))])))

;; (true true)
(cl/run* [q]
  (reredefined-pmembero 'tofu (list 'a 'b 'tofu 'd 'tofu))
  (cl/== true q))


;; ((tofu) (tofu _0 . _1) (_0 tofu) (_0 tofu _1 . _2) (_0 _1 tofu) (_0 _1 tofu _2 . _3) (_0 _1 _2 tofu) (_0 _1 _2 tofu _3 . _4) (_0 _1 _2 _3 tofu) (_0 _1 _2 _3 tofu _4 . _5) (_0 _1 _2 _3 _4 tofu) (_0 _1 _2 _3 _4 tofu _5 . _6))
(cl/run 12 [l]
  (reredefined-pmembero 'tofu l))

(def rereredefined-pmembero
  (fn [x l]
    (cl/conde
     [(eq-firsto l x) (cl/fresh [a d]
                        (cl/firsto l (cl/llist 'a 'd)))]
     [(eq-firsto l x) (cl/resto l ())]
     [(cl/fresh [d]
        (cl/resto l d)
        (rereredefined-pmembero x d))])))

;; ((tofu) (_0 tofu) (_0 _1 tofu) (_0 _1 _2 tofu) (_0 _1 _2 _3 tofu) (_0 _1 _2 _3 _4 tofu) (_0 _1 _2 _3 _4 _5 tofu) (_0 _1 _2 _3 _4 _5 _6 tofu) (_0 _1 _2 _3 _4 _5 _6 _7 tofu) (_0 _1 _2 _3 _4 _5 _6 _7 _8 tofu) (_0 _1 _2 _3 _4 _5 _6 _7 _8 _9 tofu) (_0 _1 _2 _3 _4 _5 _6 _7 _8 _9 _10 tofu))
(cl/run 12 [l]
  (rereredefined-pmembero 'tofu l))

;; definition
;;;; This asks a very basic question, what should y be, in order to be the first member of fresh l(_0)
(def first-value
  (fn [l]
    (cl/run 1 [y]
      (cl/membero y l))))

;; (pasta)
(first-value '(pasta e fagioli))

;; reverses list
(def memberrevo
  (fn [x l]
    (cl/conde
     [(cl/emptyo l)  cl/u#]
     [cl/s#         (cl/fresh [d]
                      (cl/resto   l d)
                      (memberrevo x d))]
     [cl/s#         (cl/firsto l x)])))

;; DOESNT WORK!!!
(cl/run* [x]
  (memberrevo x '(pasta e fagioli)))

(def reverse-list
  (fn [l]
    (cl/run* [y]
      (memberrevo y l))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Chapter 4: Members only ;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;; Give all the elements of a list, after the first match
(def mem
  (fn [x l]
    (cond
      (empty? l)      false
      (eq-first? l x) l
      :else           (mem x (next l)))))

;; (tofu d peas e)
(mem 'tofu '(a b tofu d peas e))

;; false
(mem 'tofu '(a b peas d peas e))

;; ((tofu d peas e))
;;;; Because we unified it with out
(cl/run* [out]
  (cl/== (mem 'tofu '(a b tofu d peas e)) out))

;; (peas e)
(mem 'peas
     (mem 'tofu '(a b tofu d peas e)))

;; (tofu d tofu e)
(mem 'tofu
     (mem 'tofu '(a b tofu d tofu e)))

;; (tofu e)
(mem 'tofu
     (rest (mem 'tofu '(a b tofu d tofu e))))

;; Definition
;;   a. first goal fails if list is empty
;;   b. if the first element matches with the targe, unify the string with out 
;;   c. Lastly recurse on the remaining list until you get to point b.
(def memo
  (fn [x l out]
    (cl/conde
     [(cl/emptyo l)    cl/u#]
     [(eq-firsto l x) (cl/== l out)]
     [(cl/fresh [d]
        (cl/resto l d)
        (memo     x d out))])))

;;((tofu d tofu e))
;;    if x was tofu
(cl/run 1 [out]
  (cl/fresh [x]
    (memo 'tofu (list 'a 'b x 'd 'tofu 'e) out)))

;; or consider this

;; (tofu)
;;    because we asked what should r be for the answer to be (tofu d tofu e) from (a b tofu d tofu e)
(cl/run* [r]
  (memo r
        (list 'a 'b 'tofu 'd 'tofu 'e)
        (list 'tofu 'd 'tofu 'e)))

;; (true)
;;    Because the first goal succeeds
(cl/run* [q]
  (memo 'tofu '(tofu e) '(tofu e))
  (cl/== true q))

;; Lets test a few more things
;; (tofu)
(cl/run 1 [q]
  (memo q '(tofu e) '(tofu e)))

;; ()
;;  Because the first goal fails
(cl/run* [q]
  (memo 'tofu '(tofu e) '(tofu))
  (cl/== true q))

;; (tofu)
;;  Because we asked what should x be to produce (tofu e)
(cl/run* [x]
  (memo 'tofu '(tofu e) (list x 'e)))

;; ()
;;  Because there is no value of x for which make (tofu e) same as (peas x)
(cl/run* [x]
  (memo 'tofu '(tofu e) (list 'peas x)))

;; ((tofu d tofu e) (tofu e))
;;    because:
;;      a. in one case is x is tofu, the out is (tofu d tofu e)
;;      b. in another case if x is not tofu, then out is (tofu e) 
(cl/run* [out]
  (cl/fresh [x]
    (memo 'tofu (list 'a 'b x 'd 'tofu 'e) out)))

;; (_0 _0 (tofu . _0) (_0 tofu . _1) (_0 _1 tofu . _2) (_0 _1 _2 tofu . _3) (_0 _1 _2 _3 tofu . _4) (_0 _1 _2 _3 _4 tofu . _5) (_0 _1 _2 _3 _4 _5 tofu . _6) (_0 _1 _2 _3 _4 _5 _6 tofu . _7) (_0 _1 _2 _3 _4 _5 _6 _7 tofu . _8) (_0 _1 _2 _3 _4 _5 _6 _7 _8 tofu . _9))
;; well! this is.... I mean z could could be a lot of things when u could a lot of things.
(cl/run 12 [z]
  (cl/fresh [u]
    (memo 'tofu (cl/llist 'a 'b 'tofu 'd 'tofu 'e z) u)))

;; memo simplified
(def memo
  (fn [x l out]
    (cl/conde
     [(eq-firsto l x) (cl/== l out)]
     [cl/s#           (cl/fresh [d]
                        (cl/resto l d)
                        (memo x d out))])))

;; definition
;; search for an element, if found, remove it and cons what's already see, with the remainder
(def rember
  (fn [x l]
    (cond
      (empty?    l)   ()
      (eq-first? l x) (next l)
      :else           (cons (first l) (rember x (next l))))))

;; (a b d peas e)
(rember 'peas '(a b peas d peas e))

;; definition
(def rembero
  (fn [x l out]
    (cl/conde
     [(cl/emptyo l)   (cl/==    () out)]
     [(cl/firsto l x) (cl/resto l  out)]
     [cl/s#           (cl/fresh [res]
                        (cl/fresh [d]
                          (cl/resto  l d)
                          (rembero   x d res))
                        (cl/fresh [a]
                          (cl/firsto l a)
                          (cl/conso  a res out)))])))

;; ((a b d peas e))
;;;; basically works
(cl/run 1 [out]
  (cl/fresh [y]
    (rembero 'peas (list 'a 'b y 'd 'peas 'e) out)))

;; ((b a d _0 e) (a b d _0 e) (a b d _0 e) (a b d _0 e) (a b _0 d e) (a b e d _0) (a b _0 d _1 e))
;;  All possible values of out of y and z   
(cl/run* [out]
  (cl/fresh [y z]
    (rembero y (list 'a 'b y 'd z 'e) out)))

;; ((d d) (d d) (_0 _0) (e e)) 
(cl/run* [r]
  (cl/fresh [y z]
    (rembero y (list y 'd z 'e) (list y 'd 'e))
    (cl/== (list y z) r)))

;; (_0 _0 _0 _0 _0 () (_0 . _1) (_0) (_0 _1 . _2) (_0 _1) (_0 _1 _2 . _3) (_0 _1 _2) (_0 _1 _2 _3 . _4))
(cl/run 13 [w]
  (cl/fresh [y z out]
    (rembero y (cl/llist 'a 'b y 'd z w) out)))

;; definition
;; For what value of s does remove s from (a b c) produce (a b c)
;;    any value of s apart from a b and c
(def surpriseo
  (fn [s]
    (rembero s '(a b c) '(a b c))))

;; (d)
(cl/run* [r]
  (cl/== 'd r)
  (surpriseo r))

;; (_0)
(cl/run* [r]
  (surpriseo r))

;; (b)
(cl/run* [r]
  (surpriseo r)
  (cl/==  'b r))

;; (b) makes no sense at all
(cl/run* [r]
  (cl/== 'b r)
  (surpriseo r))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Chapter 5, double your fun ;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;; Append two lists
(def append
  (fn [l s]
    (cond
      (empty? l) s
      :else      (cons (first l)
                       (append (next l) s)))))

;; (a b c d e)
(append '(a b c) '(d e))

;; (a b c)
(append '(a b c) ())

;; meaningless since 'a is not a list
(comment
  (append 'a '(d e)))

;; meaningless since 'a is not a list
(comment
  (append '(d e) 'a))

;; (a d e)
(conj '(d e) 'a)

(def appendo
  (fn [l s out]
    (cl/conde
     ;; if the first list is empty associate the out with the second list
     [(cl/emptyo l) (cl/== s out)]
     ;; if the first list is not empty ..
     [cl/s#         (cl/fresh [a d res]
                      (cl/firsto l a)
                      (cl/resto  l d)
                      (appendo   d s res)
                      (cl/conso  a res out))])))


;; ((cake tastes yummy))
;;   because we asked what should x be when you append (cake) and (tastes yummy)
(cl/run* [x]
  (appendo '(cake) '(tastes yummy) x))

;; (yummy)
;;    because what is appending (cake) and (tastes ?) produce (cakes tastes yummy)
(cl/run* [x]
  (appendo '(cake) (list 'tastes x) '(cake tastes yummy)))

;; ((cake with ice _0 tastes yummy))
;;   because y is fresh
(cl/run* [x]
  (cl/fresh [y]
    (appendo (list 'cake 'with 'ice y)
             '(tastes yummy) x)))

;; ((cake with ice cream . _0))
;;    because we asked what would x look like when y is fresh
(cl/run* [x]
  (cl/fresh [y]
    (appendo '(cake with ice cream) y x)))

;;  ((cake with ice d t))
;;    we asked with a fresh y, how would two two appended lists look like
(cl/run 1 [x]
  (cl/fresh [y]
    (appendo (cl/llist 'cake 'with 'ice y) '(d t) x)))

;;    we asked with a fresh y, how would two two appended lists look like
;;   ((cake with ice d t) (cake with ice _0 d t))
(cl/run 2 [x]
  (cl/fresh [y]
    (appendo (cl/llist 'cake 'with 'ice y) '(d t) x)))

;; Redefining appendo
(def appendo
  (fn [l s out]
    (cl/conde
     [(cl/emptyo l) (cl/== s out)]
     [cl/s#         (cl/fresh [a d res]
                      (cl/conso a d l)
                      (appendo  d s res)
                      (cl/conso a res out))]))) 

;; ((cake with ice d t) (cake with ice _0 d t) (cake with ice _0 _1 d t) (cake with ice _0 _1 _2 d t) (cake with ice _0 _1 _2 _3 d t))
;;  because we now get 5 fresh y values
(cl/run 5 [x]
  (cl/fresh [y]
    (appendo (cl/llist 'cake 'with 'ice y) '(d t) x)))

;; (() (_0) (_0 _1) (_0 _1 _2) (_0 _1 _2 _3))
(cl/run 5 [y]
  (cl/fresh [x]
    (appendo (cl/llist 'cake 'with 'ice y) '(d t) x)))

;;  ((cake with ice d t ()) (cake with ice _0 d t (_0)) (cake with ice _0 _1 d t (_0 _1)) (cake with ice _0 _1 _2 d t (_0 _1 _2)) (cake with ice _0 _1 _2 _3 d t (_0 _1 _2 _3)))
(cl/run 5 [x]
  (cl/fresh [y]
    (appendo (cl/llist 'cake 'with 'ice y) (list 'd 't y) x)))

;;  ((cake with ice cream d t _0))
;;    because z is fresh
(cl/run* [x]
  (cl/fresh [z]
    (appendo '(cake with ice cream) (list 'd 't z) x)))

;;  (() (cake) (cake with) (cake with ice) (cake with ice cream) (cake with ice cream d))
;;;;; basically wow!
(cl/run 6 [x]
  (cl/fresh [y]
    (appendo x y '(cake with ice cream d t))))

;; ((cake with ice cream d t) (with ice cream d t) (ice cream d t) (cream d t) (d t) (t))
(cl/run 6 [y]
  (cl/fresh [x]
    (appendo x y '(cake with ice cream d t))))

(comment

  This is just pure awesome ness
  
  ((() (cake with ice cream d t))
   ((cake) (with ice cream d t))
   ((cake with) (ice cream d t))
   ((cake with ice) (cream d t))
   ((cake with ice cream) (d t))
   ((cake with ice cream d) (t))))
(cl/run 6 [r]
  (cl/fresh [x y]
    (appendo x y '(cake with ice cream d t))
    (cl/== (list x y) r)))

(comment

  With 7 it will run forever
  
  (cl/run 7 [r]
    (cl/fresh [x y]
      (appendo x y '(cake with ice cream d t))
      (cl/== (list x y) r))))

;; Redefinition
(def appendo
  (fn [l s out]
    (cl/conde
     [(cl/emptyo l) (cl/== s out)]
     [cl/s#         (cl/fresh [a d res]
                      (cl/conso a d l)
                      (cl/conso a res out)
                      (appendo  d s res))])))

;; Runs now
(cl/run 7 [r]
  (cl/fresh [x y]
    (appendo x y '(cake with ice cream d t))
    (cl/== (list x y) r)))

;; (() (_0) (_0 _1) (_0 _1 _2) (_0 _1 _2 _3) (_0 _1 _2 _3 _4) (_0 _1 _2 _3 _4 _5))
(cl/run 7 [x]
  (cl/fresh [y z]
    (appendo x y z)))

;; (_0 _0 _0 _0 _0 _0 _0)
(cl/run 7 [y]
  (cl/fresh [x z]
    (appendo x y z)))

;;  (_0 (_0 . _1) (_0 _1 . _2) (_0 _1 _2 . _3) (_0 _1 _2 _3 . _4) (_0 _1 _2 _3 _4 . _5) (_0 _1 _2 _3 _4 _5 . _6))
(cl/run 7 [z]
  (cl/fresh [x y]
    (appendo x y z)))

;; ((() _0 _0) ((_0) _1 (_0 . _1)) ((_0 _1) _2 (_0 _1 . _2)) ((_0 _1 _2) _3 (_0 _1 _2 . _3)) ((_0 _1 _2 _3) _4 (_0 _1 _2 _3 . _4)) ((_0 _1 _2 _3 _4) _5 (_0 _1 _2 _3 _4 . _5)) ((_0 _1 _2 _3 _4 _5) _6 (_0 _1 _2 _3 _4 _5 . _6)))
(cl/run 7 [r]
  (cl/fresh [x y z]
    (appendo x y z)
    (cl/== (list x y z) r)))

(def swappendo
  (fn [l s out]
    (cl/conde
     [cl/s# (cl/fresh [a d res]
              (cl/conso  a d l)
              (cl/conso  a res out)
              (swappendo d s res))]
     [(cl/emptyo l) (cl/== s out)])))

;; (_0)
(cl/run 1 [z]
  (cl/fresh [x y]
    (swappendo x y z)))

;; Strip the paranthesis out
(def unwrap
  (fn [x]
    (cond
      (pair? x) (unwrap (first x))
      :else x)))

;; paranthesis removed
;; pizza
(unwrap '(((pizza))))

;; pizza
(unwrap '((((pizza pie) with)) extra cheese))

(def unwrapo
  (fn [x out]
    (cl/conde
     ;; if pairo, then extract the first element of list
     ;; then unwrap it recursively
     [(pairo x) (cl/fresh [a]
                  (cl/firsto x a)
                  (unwrapo a out))]
     ;; or just succeed with the non-pair
     [cl/s#     (cl/== x out)])))

;; ((((pizza))) ((pizza)) (pizza) pizza)
;;   progressively partially less wrapped pizza
(cl/run* [x]
  (unwrapo '(((pizza))) x))

;; (pizza)
(cl/run 1 [x]
  (unwrapo x 'pizza))

;; ()
(cl/run 1 [x]
  (unwrapo '((x)) 'pizza))

;; redefining unwrapo
(def unwrapo
  (fn [x out]
    (cl/conde
     [cl/s# (cl/== x out)]
     [cl/s# (cl/fresh [a]
              (cl/firsto x a)
              (unwrapo   a out))])))

;;  (pizza (pizza . _0) ((pizza . _0) . _1) (((pizza . _0) . _1) . _2) ((((pizza . _0) . _1) . _2) . _3))
(cl/run 5 [x]
  (unwrapo x 'pizza))

(cl/run 5 [x]
  (unwrapo x '((pizza))))

;;  (pizza (pizza . _0) ((pizza . _0) . _1) (((pizza . _0) . _1) . _2) ((((pizza . _0) . _1) . _2) . _3))
(cl/run 5 [x]
  (unwrapo (list (list (list x))) 'pizza))

;; flatten a nested list
;;   Doesn't work, besides using the clojure standard flatten
(comment
  (def our-flatten
    (fn [s]
      (cond
        (empty? s) ()
        (pair?  s) (append (our-flatten (first s))
                           (our-flatten (next  s)))
        :else      (cons s ())))))

;; (a b c)
(flatten '((a b) c))

;; definition
;; doesn't work, because of the absense of else?
(comment
  (def flatteno
    (fn [s out]
      (cl/conde
       [(cl/emptyo s) (cl/== () out)]
       [(pairo  s)    (cl/fresh [a d res-a res-d]
                        (cl/conso a d s)
                        (flatteno a res-a)
                        (flatteno d res-d)
                        (cl/appendo res-a res-d out))]
       [cl/s# (cl/conso s () out)]))))

;; new definitions
(defn pairo? [x]
  (or (cl/lcons? x) (and (coll? x) (seqo x))))

(def flatteno
  (fn [s out]
    (cl/conde
     [(cl/emptyo s) (cl/== () out)]
     [(pairo?    s) (cl/fresh [a d res-a res-d]
                      (cl/conso a d s)
                      (flatteno a res-a)
                      (flatteno d res-d)
                      (cl/appendo res-a res-d out))]
     [cl/s#         (cl/conso s () out)])))
  
(comment
;;;; taken from the internet
;;;;; https://blog.taylorwood.io/2018/05/10/clojure-logic.html
  (defn flatteno [l g]
      (cl/condu
       [(cl/emptyo l) (cl/emptyo g)]
       [(cl/fresh [h t]
          (cl/conso h t l)
          (cl/fresh [ft]
            (flatteno t ft)
            (cl/condu
             [(cl/fresh [fh]
                (flatteno h fh)
                (cl/appendo fh ft g))]
             [(cl/conso h ft g)])))])))

;; ((a b c))
(cl/run 1 [x]
  (flatteno '((a b) c) x))

;; ((a b c))
(cl/run 1 [x]
  (flatteno '(a (b c)) x))

;; ((a))
;;  The function in the reasoned schemer returns a very different result
(cl/run* [x]
  (flatteno '(a) x))

;;((a))
(cl/run* [x]
  (flatteno '((a)) x))

;; Ending flatten here since we couldn't get it work

;; this doesn't work either, looks like absence of else is the problem
(def flattenrevo
  (fn [s out]
    (cl/conde
     [ cl/s#        (cl/conso s () out)]
     [(cl/emptyo s) (cl/== () out)]
     [ cl/s#        (cl/fresh [a d res-a res-d]
                      (cl/conso    a d s)
                      (flattenrevo a res-a)
                      (flattenrevo d res-d)
                      (cl/appendo  res-a res-d out))])))

(cl/run 1 [q]
  (flattenrevo '((a b) c) q))

;; Could never make flatten work

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Chapter 6, The fun never ends... ;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;; first conde clause succeeds if goal g succeeds
;;    else try again.
(def anyo
  (fn [g]
    (cl/conde
     [g cl/s#]
     [cl/s# (anyo g)])))

;; never succeeds
(def nevero
  (anyo cl/u#))

;; no value
(comment
  (cl/run 1 [q]
    nevero
    (cl/== true q)))

(def alwayso
  (anyo cl/s#))

;; (true)
(cl/run 1 [q]
  alwayso
  (cl/== true q))

;;(true true true....)
(cl/run* [q]
  alwayso
  (cl/== true q))

;;(true true true....)
(cl/run* [q]
  (cl/== true q)
  alwayso)

;; succeed at least once
(def salo
  (fn [g]
    (cl/conde
     [cl/s# cl/s#]
     [cl/s# g])))

;; (true)
;;    because the first conde clause in salo succeeds and returns 
(cl/run 1 [q]
  (salo alwayso)
  (cl/== true q))

;; (true)
;;    because the first conde clause in salo succeeds and returns
(cl/run 1 [q]
  (salo nevero)
  (cl/== true q))

;; no value
;;  run never finishes getting the second value
(comment
  (cl/run* [q]
    (salo nevero)
    (cl/== true q)))

;; no value
(comment
  (cl/run 1 [q]
    (salo nevero)
    cl/u#
    (cl/== true q)))

;; no value
(comment
  (cl/run 1 [q]
    alwayso
    cl/u#
    (cl/== true q)))

;; no value
(comment
  (cl/run 1 [q]
    (cl/conde
     [(cl/== false q)  alwayso]
     [ cl/s#          (anyo (cl/== t q))])
    (cl/== true q)))

;; (true)
(cl/run 1 [q]
  (cl/conde
   [(cl/== false q) alwayso]
   [ cl/s#          (cl/== true q)])
  (cl/== true q))

;; no value
(comment
  (cl/run 2 [q]
    (cl/conde
     [(cl/== false q) alwayso]
     [ cl/s#          (cl/== true q)])
    (cl/== true q)))

;; (true true true true true)
(cl/run 5 [q]
  (cl/conde
   [(cl/== false q) alwayso]
   [ cl/s#          (anyo (cl/== true q))])
  (cl/== true q))

;; (false tea cup)
(cl/run 5 [r]
  (cl/conde
   [(teacupo r)     cl/s#]
   [(cl/== false r) cl/s#]
   [ cl/s#          cl/u#]))

;; (true true true true true)
(cl/run 5 [q]
  (cl/conde
   [(cl/== false q) alwayso]
   [(cl/== true  q) alwayso]
   [ cl/s#          cl/u#])
  (cl/== true q))

;; (true true true true true)
(cl/run 5 [q]
  (cl/conde
   [alwayso cl/s#]
   [cl/s#   cl/u#])
  (cl/== true q))

;; true
(cl/run 1 [q]
  (cl/all
   (cl/conde
    [(cl/== false q)  cl/s#]
    [ cl/s#          (cl/== true q)])
   alwayso)
  (cl/== true q))

;; (true true true true true)
(cl/run 5 [q]
  (cl/all
   (cl/conde
    [(cl/== false q)  cl/s#]
    [ cl/s#          (cl/== true q)])
   alwayso)
  (cl/== true q))

(cl/run 5 [q]
  (cl/all
   (cl/conde
    [(cl/== true q)  cl/s#]
    [ cl/s#          (cl/== false q)])
   alwayso)
  (cl/== true q))

(cl/run 5 [q]
  (cl/all
   (cl/conde
    [cl/s# cl/s#]
    [cl/s# nevero])
   alwayso)
  (cl/== true q))


;;;;;;;;;;;;;;;;;;;;;;;
;; 7. A bit too much ;;
;;;;;;;;;;;;;;;;;;;;;;;

;; a bit
0

;; a bit
1

;; not a bit
2

;; x is a bit if it is 1 or 0
(let [x 0
      y 1]
  (list x y))

;; a direct translation of the xor table.
(def bit-xoro
  (fn [x y r]
    (cl/conde
     [(cl/== 0 x) (cl/== 0 y) (cl/== 0 r)]
     [(cl/== 1 x) (cl/== 0 y) (cl/== 1 r)]
     [(cl/== 0 x) (cl/== 1 y) (cl/== 1 r)]
     [(cl/== 1 x) (cl/== 1 y) (cl/== 0 r)]
     [ cl/s# cl/u#])))

;;((0 0) (1 1))
;; Obviously, from xor table
(cl/run* [s]
  (cl/fresh [x y]
    (bit-xoro x y 0)
    (cl/== (list x y) s)))

;;redefining the bit-xoro using nand table
;;  A direct translation from the nand table
(def bit-nando
  (fn [x y r]
    (cl/conde
     [(cl/== 0 x) (cl/== 0 y) (cl/== 1 r)]
     [(cl/== 1 x) (cl/== 0 y) (cl/== 1 r)]
     [(cl/== 0 x) (cl/== 1 y) (cl/== 1 r)]
     [(cl/== 1 x) (cl/== 1 y) (cl/== 0 r)]
     [ cl/s# cl/u#])))

;; redefining from bit-nando
(def bit-xoro
  (fn [x y r]
    (cl/fresh [s t u]
      (bit-nando x y s)
      (bit-nando x s t)
      (bit-nando s y u)
      (bit-nando t u r))))

;;((0 0) (1 1))
;; Redefinition, from bit-nando table works
(cl/run* [s]
  (cl/fresh [x y]
    (bit-xoro x y 0)
    (cl/== (list x y) s)))

;; ((1 0) (0 1))
(cl/run* [s]
  (cl/fresh [x y]
    (bit-xoro x y 1)
    (cl/== (list x y) s)))

;;  ((0 0 0) (1 0 1) (0 1 1) (1 1 0))
;;    basically the whole xor table
(cl/run* [s]
  (cl/fresh [x y r]
    (bit-xoro x y r)
    (cl/== (list x y r) s)))

;; Direct translation of AND table
(def bit-ando
  (fn [x y r]
    (cl/conde
     [(cl/== 0 x) (cl/== 0 y) (cl/== 0 r)]
     [(cl/== 1 x) (cl/== 0 y) (cl/== 0 r)]
     [(cl/== 0 x) (cl/== 1 y) (cl/== 0 r)]
     [(cl/== 1 x) (cl/== 1 y) (cl/== 1 r)]
     [ cl/s# cl/u#])))

;;((1 1))
(cl/run* [s]
  (cl/fresh [x y]
    (bit-ando x y 1)
    (cl/== (list x y) s)))

;; definition of half adder using bit-xoro and bit-ando
(def half-addero
  (fn [x y r c]
    (cl/all
     (bit-xoro x y r)
     (bit-ando x y c))))

;; Straight from the half adder table
;;     Given bit x, y, r and c
;;     Half addero satisfies x + y = r + 2.c
(def half-addero
  (fn [x y r c]
    (cl/conde
     [(cl/== 0 x) (cl/== 0 y) (cl/== 0 r) (cl/== 0 c)]
     [(cl/== 1 x) (cl/== 0 y) (cl/== 1 r) (cl/== 0 c)]
     [(cl/== 0 x) (cl/== 1 y) (cl/== 1 r) (cl/== 0 c)]
     [(cl/== 1 x) (cl/== 1 y) (cl/== 0 r) (cl/== 1 c)]
     [ cl/u# cl/s#])))

;; From the above table
(cl/run* [r]
  (half-addero 1 1 r 1))

;; ((0 0 0 0) (1 0 1 0) (0 1 1 0) (1 1 0 1))
;;    Gives the whole table
(cl/run* [s]
  (cl/fresh [x y r c]
    (half-addero x y r c)
    (cl/== (list x y r c) s)))

;; Definition of full adder from the existing definition
(def full-addero
  (fn [b x y r c]
    (cl/fresh (w xy wz)
      (half-addero x y w xy)
      (half-addero w b r wz)
      (bit-xoro xy wz c))))

;; Full adder satisfies the following property
;;    b + x + y = r + 2.c
(def full-addero
  (fn [b x y r c]
    (cl/conde
     [(cl/== 0 b) (cl/== 0 x) (cl/== 0 y) (cl/== 0 r) (cl/== 0 c)]
     [(cl/== 1 b) (cl/== 0 x) (cl/== 0 y) (cl/== 1 r) (cl/== 0 c)]
     [(cl/== 0 b) (cl/== 1 x) (cl/== 0 y) (cl/== 1 r) (cl/== 0 c)]
     [(cl/== 1 b) (cl/== 1 x) (cl/== 0 y) (cl/== 0 r) (cl/== 1 c)]
     [(cl/== 0 b) (cl/== 0 x) (cl/== 1 y) (cl/== 1 r) (cl/== 0 c)]
     [(cl/== 1 b) (cl/== 0 x) (cl/== 1 y) (cl/== 0 r) (cl/== 1 c)]
     [(cl/== 0 b) (cl/== 1 x) (cl/== 1 y) (cl/== 0 r) (cl/== 1 c)]
     [(cl/== 1 b) (cl/== 1 x) (cl/== 1 y) (cl/== 1 r) (cl/== 1 c)]
     [ cl/s# cl/u#])))

;; (0 1)
(cl/run* [s]
  (cl/fresh [r c]
    (full-addero 0 1 1 r c)
    (cl/== (list r c) s)))

;; (1 1)
(cl/run* [s]
  (cl/fresh [r c]
    (full-addero 1 1 1 r c)
    (cl/== (list r c) s)))

;;  ((0 0 0 0 0) (1 0 0 1 0) (0 1 0 1 0) (1 1 0 0 1) (0 0 1 1 0) (1 0 1 0 1) (0 1 1 0 1) (1 1 1 1 1))
;;  The whole table basically
(cl/run* [s]
  (cl/fresh [b x y r c]
    (full-addero b x y r c)
    (cl/== (list b x y r c) s)))

(comment
  ;; 1. A number is a integer greater than or equal to 0
  ;; 2. 1 is (1) because 1 * (2 ^ 0)
  ;; 3. 5 is (1 0 1) because 1 * (2 ^ 0) + 0 * (2 ^ 1) + 1 * (2 ^ 2) which is 1 + 0 + 4 which 5
  ;; 4. 7 on the same lines is (1 1 1)
  ;; 5. 9 is (1 0 0 1)
  ;; 6. 6 is (0 1 1)
  ;; 7. 17920 is (0 1 0 1 0 0 0 1 1 1 0 0 0 0 1)
  )

;; Given a number produce a bit sequence
(def build-num
  (fn [n]
    (cond
      (zero? n)            ()
      (and (not (zero? n))
           (even? n))      (cons 0 (build-num (/ n 2)))
      (odd? n)             (cons 1 (build-num (/ (- n 1) 2))))))

;; ()
(build-num 0)

;; (0 0 1 0 0 1)
(build-num 36)

;; (1 1 0 0 1)
(build-num 19)

(def build-num
  (fn [n]
    (cond
      (odd? n)             (cons 1 (build-num (/ (- n 1) 2)))
      (and (not (zero? n))
           (even? n))      (cons 0 (build-num (/ n 2)))
      (zero? n)            ())))


(comment
  1. sum of (1) and (1) is (0 1) which is true
  2. sum of (0 0 0 1) and (1 1 1) is (1 1 1 1), which is fifteen)


(def poso
  (fn [n]
    (cl/fresh [a d]
      (cl/== (cl/llist a d) n))))

;; (true)
(cl/run* [q]
  (poso '(0 1 1))
  (cl/== true q))

;; ()
(cl/run* [q]
  (poso '())
  (cl/== true q))

;; ((_0 _1))
(cl/run* [r]
  (poso r))


;; ((_0 _1 . _2)) represents all numbers > 1
(cl/run* [q]
  (cl/fresh [x y z temp]
    (cl/== temp (cl/llist x y z))
    (cl/== temp q)))

;; Numbers greater than 0
(def >1o
  (fn [n]
    (cl/fresh [a ad dd]
      (cl/== (cl/llist a ad dd) n))))

;; true
(cl/run* [q]
  (>1o '(0 1 1))
  (cl/== true q))

;; true
(cl/run* [q]
  (>1o '(0 1))
  (cl/== true q))

;; ()
(cl/run* [q]
  (>1o '(1))
  (cl/== true q))

;; ()
(cl/run* [q]
  (>1o '())
  (cl/== true q))

;; (_0 _1 . _2)
(cl/run* [r]
  (>1o r))

(declare gen-addero)

;; Lets define addero and gen-addero
(def addero
  (fn [d n m r]
    (cl/conde
     [(cl/== 0 d)    (cl/== () m)   (cl/== n r)]
     [(cl/== 0 d)    (cl/== () n)   (cl/== m r) (poso m)]
     [(cl/== 1 d)    (cl/== () m)   (addero 0   n '(1) r)]
     [(cl/== 1 d)    (cl/== () n)   (addero 0 '(1)  m  r)]
     [(cl/== '(1) n) (cl/== '(1) m) (cl/fresh [a c]
                                      (cl/== (list a c) r)
                                      (full-addero d 1 1 a c))]
     [(cl/== '(1) n) (gen-addero d n m r)]
     [(cl/== '(1) m) (>1o n) (>1o r) (addero d '(1) n r)]
     [(>1o n)        (gen-addero d n m r)]
     [ cl/s# cl/u#])))

(def gen-addero
  (fn [d n m r]
    (cl/fresh [a b c e x y z]
      (cl/== (cl/llist a x) n)
      (cl/== (cl/llist b y) m) (poso y)
      (cl/== (cl/llist c z) r) (poso z)
      (cl/all
       (full-addero d a b c e)
       (addero      e x y z)))))

;;  ((_0 () _0) (() (_0 . _1) (_0 . _1)) ((1) (1) (0 1)))
(cl/run 3 [s]
  (cl/fresh [x y r]
    (addero 0 x y r)
    (cl/== (list x y r) s)))

(cl/run 22 [s]
  (cl/fresh [x y r]
    (addero 0 x y r)
    (cl/== (list x y r) s)))

;;(0 1 0 1)
(cl/run* [s]
  (gen-addero 1 '(0 1 1) '(1 1) s))

;; (((1 0 1) ()) (() (1 0 1)) ((1) (0 0 1)) ((0 0 1) (1)) ((1 1) (0 1)) ((0 1) (1 1)))
;;   Pairs of numbers that sum to five
(cl/run* [s]
  (cl/fresh [x y]
    (addero 0 x y '(1 0 1))
    (cl/== (list x y) s)))

(def +o
  (fn [n m k]
    (addero 0 n m k)))

;; rewrite list of all numbers that sum to five
(cl/run* [s]
  (cl/fresh [x y]
    (+o x y '(1 0 1))
    (cl/== (list x y) s)))

(def -o
  (fn [n m k]
    (+o m k n)))

;; ((1 1))
(cl/run* [q]
  (-o '(0 0 0 1) '(1 0 1) q))

;; (())
(cl/run* [q]
  (-o '(0 1 1) '(0 1 1) q))

;; () because we don't handle negative numbers(yet)
(cl/run* [q]
  (-o '(0 1 1) '(0 0 0 1) q))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Chapter 8. Just a bit more ;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;; Definitions
(def bound-*o
  (fn [q p n m]
    cl/s#))

(declare *o)

(def odd-*o
  (fn [x n m p]
    (cl/fresh [q]
      (bound-*o q p n m)
      (*o x m q)
      (+o (cl/llist 0 q) m p))))

(def *o
  (fn [n m p]
    (cl/conde
     [(cl/== () n)   (cl/== () p)]
     [(poso n)       (cl/== () m)   (cl/== () p)]
     [(cl/== '(1) n) (poso m)       (cl/== m  p)]
     [(>1o n)        (cl/== '(1) m) (cl/== n  p)]
     [(cl/fresh [x z]
        (cl/== (cl/llist 0 x) n) (poso x)
        (cl/== (cl/llist 0 z) p) (poso z)
        (>1o m)
        (*o x m z))]
     [(cl/fresh [x y]
        (cl/== (cl/llist 1 x) n) (poso x)
        (cl/== (cl/llist 0 y) m) (poso y)
        (*o m n p))]
     [(cl/fresh [x y]
        (cl/== (cl/llist 1 x) n) (poso x)
        (cl/== (cl/llist 1 y) m) (poso y)
        (odd-*o x n m p))]
     [cl/s# cl/u#])))

(cl/run 34 [t]
  (cl/fresh [x y r]
    (*o x y r)
    (cl/== (list x y r) t)))

;; (0 0 0 1)
(cl/run* [p]
  (*o '(0 1) '(0 0 1) p))

;;(((1) (1)))
;;    to get 1, you need 1 x 1
(cl/run 1 [t]
  (cl/fresh [n m]
    (*o n m '(1))
    (cl/== (list n m) t)))

;; never finishes running
(comment
  (cl/run 2 [t]
    (cl/fresh [n m]
      (*o n m '(1))
      (cl/== (list n m) t))))

;; redefine bound-*o
(def bound-*o
  (fn [q p n m]
    (cl/conde
     [(cl/emptyo q) (pairo p)]
     [ cl/s#        (cl/fresh [x y z]
                      (cl/firsto q x)
                      (cl/resto  p y)
                      (cl/conde
                       [(cl/emptyo n) (cl/resto m z) (bound-*o x y z ())]
                       [ cl/s#        (cl/resto n z) (bound-*o x y z m)]))])))

;; unlike the previous one this runs
(cl/run 2 [t]
  (cl/fresh [n m]
    (*o n m '(1))
    (cl/== (list n m) t)))

;; doesn't work
(cl/run* [p]
  (*o '(1 1 1) '(1 1 1 1 1 1) p))

(def =lo
  (fn [n m]
    (cl/conde
     [(cl/==  ()  n) (cl/==  ()  m)]
     [(cl/== '(1) n) (cl/== '(1) m)]
     [ cl/s#         (cl/fresh [a x b y]
                       (cl/== (cl/llist a x) n) (poso x)
                       (cl/== (cl/llist b y) m) (poso y)
                       (=lo x y))])))

;;  ((_0 _1 (_2 1)))
(cl/run* [t]
  (cl/fresh [w x y]
    (=lo (cl/llist 1 w x y) '(0 1 1 0 1))
    (cl/== (list w x y) t)))

;; (1)
(cl/run* [b]
  (=lo '(1) (list b)))

;; (_0 1)
(cl/run* [n]
  (=lo (cl/llist '1 '0 '1 n) '(0 1 1 0 1)))

;; ((() ()) ((1) (1)) ((_0 1) (_1 1)) ((_0 _1 1) (_2 _3 1)) ((_0 _1 _2 1) (_3 _4 _5 1)))
(cl/run 5 [t]
  (cl/fresh [y z]
    (=lo (cl/llist '1 y) (cl/llist '1 z))
    (cl/== (list y z) t)))

;;=> (((1) (1)) ((_0 1) (_1 1)) ((_0 _1 1) (_2 _3 1)) ((_0 _1 _2 1) (_3 _4 _5 1)) ((_0 _1 _2 _3 1) (_4 _5 _6 _7 1)))
(cl/run 5 [t]
  (cl/fresh [y z]
    (=lo (cl/llist 1 y) (cl/llist 0 z))
    (cl/== (list y z) t)))

;;=> (((_0 _1 _2 1) ()) ((_0 _1 _2 _3 1) (1)) ((_0 _1 _2 _3 _4 1) (_5 1)) ((_0 _1 _2 _3 _4 _5 1) (_6 _7 1)) ((_0 _1 _2 _3 _4 _5 _6 1) (_7 _8 _9 1)))
(cl/run 5 [t]
  (cl/fresh [y z]
    (=lo (cl/llist 1 y) (cl/llist 0 1 1 0 1 z))
    (cl/== (list y z) t)))

;; definition
(def <lo
  (fn [n m]
    (cl/conde
     [(cl/==  ()  n) (poso m)]
     [(cl/== '(1) n) (>1o m) ]
     [cl/s#          (cl/fresh [a x b y]
                       (cl/== (cl/llist a x) n) (poso x)
                       (cl/== (cl/llist b y) m) (poso y)
                       (<lo x y))])))

;;=> ((() _0) ((1) _0) ((_0 1) _1) ((_0 _1 1) _2) ((_0 _1 _2 1) (_3 . _4)) ((_0 _1 _2 _3 1) (_4 _5 . _6)) ((_0 _1 _2 _3 _4 1) (_5 _6 _7 . _8)) ((_0 _1 _2 _3 _4 _5 1) (_6 _7 _8 _9 . _10)))
(cl/run 8 [t]
  (cl/fresh [y z]
    (<lo (cl/llist 1 y) (cl/llist 0 1 1 0 1 z))
    (cl/== (list y z) t)))

;; no value
(comment
  (cl/run 1 [n]
    (<lo n n)))

(def <=lo
  (fn [n m]
    (cl/conde
     [(=lo  n m) cl/s#]
     [(<=lo n m) cl/s#]
     [ cl/s#     cl/u#])))

(cl/run 8 [t]
  (cl/fresh [n m]
    (<=lo n m)
    (cl/== (list n m) t)))

;; (() ())
(cl/run 1 [t]
  (cl/fresh [n m]
    (<=lo n m)
    (*o n '(0 1) m)
    (cl/== (list n m) t)))

;; no value
(comment
  (cl/run 2 [t]
    (cl/fresh [n m]
      (<=lo n m)
      (*o n '(0 1) m)
      (cl/== (list n m) t))))

;; => ((() ()) (() ()))
(cl/run 2 [t]
  (cl/fresh [n m]
    (<=lo n m)
    (*o n '(0 1) m)
    (cl/== (list n m) t)))

;; no value
(comment
  (cl/run 10 [t]
    (cl/fresh [n m]
      (<=lo n m)
      (*o n '(0 1) m)
      (cl/== (list n m) t))))


(cl/run 15 [t]
  (cl/fresh [n m]
    (<=lo n m)
    (cl/== (list n m) t)))

;; definitions
(def <o
  (fn [n m]
    (cl/conde
     [(<lo n m)  cl/s#]
     [(=lo n m) (cl/fresh [x]
                  (poso x)
                  (+o n x m))]
     [ cl/s#     cl/u#])))

(def <=o
  (fn [n m]
    (cl/conde
     [(cl/== n m) cl/s#]
     [(<o    n m) cl/s#]
     [ cl/s#      cl/u#])))

;; (true)
(cl/run* [q]
  (<o '(1 0 1) '(1 1 1))
  (cl/== true q))

;; ()
(cl/run* [q]
  (<o '(1 1 1) '(1 0 1))
  (cl/== true q))

;; ()
(cl/run* [q]
  (<o '(1 0 1) '(1 0 1))
  (cl/== true q))

;; => (() (1) (_0 1) (0 0 1))
(cl/run 6 [n]
  (<o n '(1 0 1)))

;; => ((_0 _1 _2 _3 . _4) (0 1 1) (1 1 1))
(cl/run 6 [n]
  (<o '(1 0 1) n))

;; no value
(comment
  (cl/run* [n]
    (<o n n)))

;; definitions
(def divo
  (fn [n m q r]
    (cl/conde
     [(cl/==  ()  q) (cl/== n  r) (<o    n m)]
     [(cl/== '(1) q) (cl/== () r) (cl/== n m) (<o r m)]
     [(<o     m   n) (<o    r  m) (cl/fresh [mq]
                                    (<=lo mq n)
                                    (*o   m  q mq)
                                    (+o   mq r n))]
     [ cl/s#   cl/u#])))

(cl/run 15 [t]
  (cl/fresh [n m q r]
    (divo n m q r)
    (cl/== (list n m q r) t)))

;; fails
(comment
  (cl/run* [m]
    (cl/fresh [r]
      (divo '(1 0 1) m '(1 1 1) r))))

(def divo
  (fn [n m q r]
    (cl/fresh [mq]
      (<o   r  m)
      (<=lo mq n)
      (*o   m  q mq)
      (+o   mq r n))))

;; definitions

(def splito
  (fn [n r l h]
    (cl/conde
     [(cl/== () n) (cl/== () h) (cl/== () l)]
     [(cl/fresh (b ne)
        (cl/== (cl/llist 0 b ne) n)
        (cl/== () r)
        (cl/== (cl/llist b ne) h)
        (cl/== () l))]
     [(cl/fresh [ne]
        (cl/== (cl/llist 1 ne) n)
        (cl/== ()   r)
        (cl/== ne   h)
        (cl/== '(1) l))]
     [(cl/fresh [b ne a re]
        (cl/== (cl/llist 0 b ne) n)
        (cl/== (cl/llist a re)   r)
        (cl/== () l)
        (splito (cl/llist b ne) re () h))]
     [(cl/fresh [ne a re]
        (cl/== (cl/llist 1 ne) n)
        (cl/== (cl/llist a re) r)
        (cl/== '(1) l)
        (splito ne re () h))]
     [(cl/fresh [b ne a re le]
        (cl/== (cl/llist b ne) n)
        (cl/== (cl/llist a re) r)
        (cl/== (cl/llist b le) l)
        (poso l)
        (splito ne re le h))]
     [ cl/s# cl/u#])))

(def divo
  (fn [n m q r]
    (cl/conde
     [(cl/== r    n) (cl/== () q) (<o n m)]
     [(cl/== '(1) q) (=lo   n  m) (+o r m n) (<o r m)]
     [ cl/s#         (cl/all
                      (<lo m n)
                      (<o  r m)
                      (poso  q)
                      (cl/fresh [nh nl qh ql qlm qlmr rr rh]
                        (cl/all
                         (splito n r nl nh)
                         (splito q r ql qh)
                         (cl/conde
                          [(cl/== () nh) (cl/== () qh) (-o nl r qlm) (*o ql m qlm)]
                          [ cl/s#        (cl/all
                                          (poso nh)
                                          (*o   ql   m  qlm)
                                          (+o   qlm  r  qlmr)
                                          (-o   qlmr nl rr)
                                          (splito rr r  () rh)
                                          (divo   nh m  qh rh))]))))])))

;; ()
(cl/run 3 [t]
  (cl/fresh [y z]
    (divo (cl/llist 1 0 y) '(0 1) z ())
    (cl/== (list y z) t)))

;; definitions
(def repeated-mulo
  (fn [n q nq]
    (cl/conde
     [(poso n)       (cl/== () q) (cl/== '(1) nq)]
     [(cl/== '(1) q) (cl/== n nq)]
     [(>1o q)        (cl/fresh [q1 nq1]
                       (+o q1 '(1) q)
                       (repeated-mulo n q1 nq1)
                       (*o nq1 n nq))]
     [ cl/s#          cl/u#])))

(def exp2o
  (fn [n b q]
    (cl/conde
     [(cl/== '(1) n) (cl/== ()   q)]
     [(>1o n)        (cl/== '(1) q) (cl/fresh [s]
                                      (splito n b s '(1)))]
     [(cl/fresh [q1 b2]
        (cl/all
         (cl/== (cl/llist 0 q1) q)
         (poso q1)
         (<lo  b n)
         (cl/appendo b (cl/llist 1 b) b2)
         (exp2o n b2 q1)))]
     [(cl/fresh [q1 nh b2 s]
        (cl/all
         (cl/== (cl/llist 1 q1) q)
         (poso q1)
         (poso nh)
         (splito n b s nh)
         (cl/appendo b (cl/llist 1 b) b2)
         (exp2o nh b2 q1)))]
     [ cl/s# cl/u#])))

(def logo
  (fn [n b q r]
    (cl/conde
     [(cl/== '(1) n) (poso b) (cl/== () q) (cl/== () r)]
     [(cl/==  ()  q) (<o n b) (+o r '(1) n)]
     [(cl/== '(1) q) (>1o b)  (=lo n b) (+o r b n)]
     [(cl/== '(1) b) (poso q) (+o r '(1) n)]
     [(cl/==  ()  b) (poso q) (cl/== r n)]
     [(cl/== '(0 1) b) (cl/fresh [a ad dd]
                         (poso dd)
                         (cl/== (cl/llist a ad dd) n)
                         (exp2o n () q)
                         (cl/fresh [s]
                           (splito n dd r s)))]
     [(cl/fresh [a ad add ddd]
        (cl/conde
         [(cl/== '(1 1) b)]
         [ cl/s# (cl/== (cl/llist a ad add ddd) b)]))
      (<lo b n)
      (cl/fresh [bw1 bw nw nw1 ql1 ql s]
        (exp2o b () bw1)
        (+o bw1 '(1) bw)
        (<lo q n)
        (cl/fresh [q1 bwq1]
          (+o q '(1) q1)
          (*o bw q1 bwq1)
          (<o nw1 bwq1)
          (exp2o n () nw1)
          (+o nw1 '(1) nw)
          (divo nw bw ql1 s)
          (+o ql '(1) ql1)
          (cl/conde
           [(cl/== q ql)]
           [ cl/s# (<lo ql q)])
          (cl/fresh [bql qh s qdh qd]
            (repeated-mulo b ql bql)
            (divo nw bw1 qh s)
            (+o ql qdh qh)
            (+o ql qd  q)
            (cl/conde
             [(cl/== qd qdh)]
             [ cl/s# (<o qd qdh)])
            (cl/fresh [bqd bq1 bq]
              (repeated-mulo b qd bqd)
              (*o bql bqd bq)
              (*o b   bq bq1)
              (+o bq  r  n)
              (<o n   bq1)))))]
     [cl/s# cl/u#])))

;; ((0 1 1))
(cl/run* [r]
  (logo '(0 1 1 1) '(0 1) '(1 1) r))

(cl/run 8 [s]
  (cl/fresh [b q r]
    (logo '(0 0 1 0 0 0 1) b q r)
    (>1o q)
    (cl/== (list b q r) s)))

;; definitions
(def expo
  (fn [b q n]
    (logo n b q ())))

;; doesn't work
(comment
  (cl/run* [t]
    (expo '(1 1) '(1 0 1) t)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; chapter 9: Under the hood ;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;; define variables u, v and w
(def u (var u))
(def v (var v))
(def w (var w))

;; define variable x, y and z
(def x (var x))
(def y (var y))
(def z (var z))

;; We have no dotted pairs in clojure so lets see how this goes
;;   Lets assume a normal list to be a dotted pair

'(z a)

(defn rhs [l]
  (first (rest l)))

;; b 
(rhs '(z b))

;; w
(rhs '(z w))

;; (x e y)
(rhs '(z (x e y)))

;; ((z a) (x w) (y z))
;;     Our way of representing a substitution 
;;     A list of associations
'((z a) (x w) (y z))

;; Not a substitution as we don't allow lhs and rhs to be same
'((z a) (x x) (y z))


;; Testing some associations
(def e '((a 1) (b 2) (c 3)))

(= '((a)) '((a)))
(= '((a (b c (d e f))))  '((a (b c (d e f)))))
(rest '(1))
(first '(1))
(not (= () (rest '())))
(not (= () (rest '(a b))))
(empty? '(1 2))
(count '(1 2))
(> 1 (count '(1 2)))

(defn is-pair? [s]
  (let [r (rest s)]
    (and (not (empty? s))
         (not (= () r))
         (not (< 1 (count r))))))

(is-pair? '(a b))
(is-pair? '(a))
(is-pair? '(a b c))
(is-pair? '(a (b c)))

(defn assq [s l]
  (let [frst (first l)
        rst  (rest  l)]
    (cond
      (= () l)              false
      (not (is-pair? frst)) (assq s rst)
      (= s (first frst))    frst
      :else                 (assq s rst))))

(assq 'a '((a e) (b f) (c g)))
(assq 'b '((b (a c) (1 q) (t 6))))
(assq 'b '((b (a c)) (1 q) (t 6)))
(assq 'z '((a e) (b f) (c g)))

;; Substitutions that don't contain any associations.
(def empty-s ())

;; definition of walk
;; Kind of book definition but doesn't work
(comment
  (def walk
    (fn [v s]
      (cond
        (not (seq? v)) (let [a (assq v s)]
                         (cond
                           (not (= false a)) (fn [a]
                                               (walk (rhs a) s))
                           :else v))
        :else    v))))

(def walk
  (fn [v s]
    (cond
      (not (seq? v)) (let [a (assq v s)]
                       (cond
                         (not (= false a)) (walk (rhs a) s)
                         :else v))
      :else    v)))

(def ext-s
  (fn [x v s]
    (cons (list x v) s)))

(ext-s 'x 'y '((z x) (y z)))

;; x
(comment
  (walk 'x (ext-s 'x 'y '((z x) (y z)))))

;; w , x -> y -> z -> w
(walk 'x (ext-s 'x 'y '((z w) (y z))))

;; a, z -> a
(walk 'z '((z a) (x w) (y z)))

;; z, y -> z -> a
(walk 'y '((z a) (x w) (y z)))

;; w, x -> w
(walk 'x '((z a) (x w) (y z)))

;; w
(walk 'w '((z a) (x w) (y z)))

;; circular walk
(comment
  (walk 'x '((x y) (z x) (y z))))

;; b, circular structure but not this walk
(walk 'w '((x y) (w b) (z x) (y z)))

;; b, x -> y -> b
(walk 'x '((y b) (x y) (v x) (w x) (u w)))

;; z, x -> y -> z
(walk 'x '((y z) (x y) (v x) (w x) (u w)))

;; (x e x), u -> w -> (x e x)
(walk 'u '((x b) (w (x e x)) (u w)))

;; y, fresh y
(walk 'y '((x e)))

;; e, y -> x -> e
(walk 'y (ext-s 'y 'x '((x e))))

;; z , x -> y -> z
(walk 'x '((y z) (x y)))

;; b, x -> y -> z -> b
(walk 'x (ext-s 'z 'b '((y z) (x y))))

;; w, x -> y -> z -> w
(walk 'x (ext-s 'z 'w '((y z) (x y))))

;; definition
(def walk*
  (fn [v s]
    (let [v (walk v s)]
      (cond
        (seq? v)       v
        (is-pair?  v)  (conj (walk* (first v) s) (walk* (rest v) s))
        :else          v))))

(walk* 'x '((y (a z c)) (x y) (z a)))


;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Chapter 10, Thin ice ;;
;;;;;;;;;;;;;;;;;;;;;;;;;;

;; A bit like if / else
(cl/conda
 [cl/u# cl/s#]
 [cl/s# cl/u#])

;; true from the second goal (else)
(cl/run 1 [q]
  (cl/conda
   [cl/u# (cl/== false q)]
   [cl/s# (cl/== true q)]))

;; true from the first goal (if)
(cl/run 1 [q]
  (cl/conda
   [cl/s# (cl/== true q)]
   [cl/s# (cl/== false q)]))

;; succeeds, in second goal
(cl/conda
 [cl/u# cl/s#]
 [cl/s# cl/s#])

;; fails, in first goal
(cl/conda
 [cl/s# cl/u#]
 [cl/s# cl/s#])

;; succeeds in the first goal
(cl/conda
 [cl/s# cl/s#]
 [cl/s# cl/u#])

;; Why doesn't this give error
(cl/conda
 [cl/u# cl/s#]
 [cl/u# cl/u#]
 [cl/s# cl/s#])

;; Because our understanding was wrong
;;  Once the head of a clause has succeeded all others are ignored


;; (olive)
(cl/run* [x]
  (cl/conda
   [(cl/== 'olive x) cl/s#]
   [(cl/== 'oil   x) cl/s#]
   [ cl/s#           cl/u#]))

;; ()
;;    Because first clause succeeds, but the first line as a whole fails
(cl/run* [x]
  (cl/conda
   [(cl/== 'pure  x) cl/u#]
   [(cl/== 'olive x) cl/s#]
   [(cl/== 'oil   x) cl/s#]
   [ cl/s#           cl/u#]))

;; ()
;;    Because in the fresh x got split, now conda fails, but x is already associated
(cl/run* [q]
  (cl/fresh [x y]
    (cl/== 'split x)
    (cl/== 'pea   y)
    (cl/conda
     [(cl/== 'split x) (cl/== x y)]
     [ cl/s#            cl/s#]))
  (cl/== true q))

;; true
(cl/run* [q]
  (cl/fresh [x y]
    (cl/== 'split x)
    (cl/== 'pea   y)
    (cl/conda
     [(cl/== x y) (cl/== 'split x)]
     [ cl/s#       cl/s#]))
  (cl/== true q))

(def not-pastao
  (fn [x]
    (cl/conda
     [(cl/== 'pasta x) cl/u#]
     [ cl/s#           cl/s#])))

;; (spaghetti)
(cl/run* [x]
  (cl/conda
   [(not-pastao x)  cl/u#]
   [ cl/s#         (cl/== 'spaghetti x)]))

;; ()
(cl/run* [x]
  (cl/== 'spaghetti x)
  (cl/conda
   [(not-pastao x) cl/u#]
   [ cl/s#        (cl/== 'spaghetti x)]))

;; never finishes executing
(comment
  (cl/run* [q]
    (cl/conda
     [alwayso cl/s#]
     [cl/s#   cl/u#])
    (cl/== true q)))

;;(true)
(cl/run* [q]
  (cl/condu
   [alwayso cl/s#]
   [cl/s#   cl/u#])
  (cl/== true q))

;; never finishes executing
(comment
  (cl/run* [q]
    (cl/condu
     [cl/s# alwayso]
     [cl/s# cl/u#])
    (cl/== true q)))

(comment
  (cl/run 1 [q]
    (cl/conda
     [alwayso cl/s#]
     [cl/s#   cl/u#])
    cl/u#
    (cl/== true q)))

;; ()
(cl/run 1 [q]
  (cl/condu
   [alwayso cl/s#]
   [cl/s#   cl/u#])
  cl/u#
  (cl/== true q))

(def onceo
  (fn [g]
    (cl/condu
     [g     cl/s#]
     [cl/s# cl/u#])))

;; (tea)
(cl/run* [x]
  (onceo (teacupo x)))

;; ()
(cl/run 1 [q]
  (onceo (salo nevero))
  cl/u#)

;; (false tea cup)
(cl/run* [r]
  (cl/conde
   [(teacupo r)     cl/s#]
   [(cl/== false r) cl/s#]
   [ cl/s#          cl/u#]))

;; (tea cup)
(cl/run* [r]
  (cl/conda
   [(teacupo r)     cl/s#]
   [(cl/== false r) cl/s#]
   [ cl/s#          cl/u#]))

;; (false)
(cl/run* [r]
  (cl/== false r)
  (cl/conda
   [(teacupo r)     cl/s#]
   [(cl/== false r) cl/s#]
   [ cl/s#          cl/u#]))

;; (false)
(cl/run* [r]
  (cl/== false r)
  (cl/condu
   [(teacupo r)     cl/s#]
   [(cl/== false r) cl/s#]
   [ cl/s#          cl/u#]))

;; definition
(def bumpo
  (fn [n x]
    (cl/conde
     [(cl/== n x)  cl/s#]
     [ cl/s#      (cl/fresh [m]
                    (-o n '(1) m)
                    (bumpo m x))])))

;; => ((1 1 1) (0 1 1) (1 0 1) (0 0 1) (1 1) (0 1) (1) ())
(cl/run* [x]
  (bumpo '(1 1 1) x))

;; definitions
(def gen&testo
  (fn [op i j k]
    (onceo
     (cl/fresh [x y z]
       (op x y z)
       (cl/== i x)
       (cl/== j y)
       (cl/== k z)))))

;; (true)
(cl/run* [q]
  (gen&testo +o '(0 0 1) '(1 1) '(1 1 1))
  (cl/== true q))

;; no value
(comment
  (cl/run 1 [q]
    (gen&testo +o '(0 0 1) '(1 1) '(0 1 1))))

;; definitions
(def enumerato
  (fn [op r n]
    (cl/fresh [i j k]
      (bumpo n i)
      (bumpo n j)
      (op  i j k)
      (gen&testo op i j k)
      (cl/== (list i j k) r))))

(cl/run* [s]
  (enumerato +o s '(1 1)))

;; definitions
(cl/run 1 [s]
  (enumerato +o s '(1 1 1)))

;; End of book
