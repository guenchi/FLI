

;; test tuple pass and operate

(begin
    (define x '(1 2 3 4 5))
    (py-initialize)
    (define t (list->*tuple x))
    (define a (py/tuple-get-item t 0))
    (define b (py/tuple-get-item t 1))
    (define c (py/tuple-get-item t 2))
    (define d (py/tuple-get-item t 3))
    (define e (py/tuple-get-item t 4))
    (display (py/long-as-long a))
    (newline)
    (display (py/long-as-long b))
    (newline)
    (display (py/long-as-long c))
    (newline)
    (display (py/long-as-long d))
    (newline)
    (display (py/long-as-long e))
    (newline)
    (py-finalize)
    (exit))