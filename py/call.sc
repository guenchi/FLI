;  MIT License

;  Copyright guenchi (c) 2018 
         
;  Permission is hereby granted, free of charge, to any person obtaining a copy
;  of this software and associated documentation files (the "Software"), to deal
;  in the Software without restriction, including without limitation the rights
;  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
;  copies of the Software, and to permit persons to whom the Software is
;  furnished to do so, subject to the following conditions:
         
;  The above copyright notice and this permission notice shall be included in all
;  copies or substantial portions of the Software.
         
;  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
;  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
;  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
;  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
;  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
;  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
;  SOFTWARE.






(library (fli py call)
    (export
        py-call
        py-args
        list->py-list
        list->py-tuple
        vector->py-list
        vector->py-tuple
        py-list->list
        py-tuple->list
    )
    (import
        (scheme)
        (match match)
        (fli py ffi))

    (define py-call
        (lambda (lst)
            ;(py-initialize)
            (let l ((lst lst))
                (if (not (null? lst))  
                    (begin
                        (eval (parse (car lst)))
                        (l (cdr lst)))))
            ;(py-finalize)
            ))

            
    (define parse
        (lambda (lst)
            (define Sym
                (lambda (x)
                    (match x
                        (,s (guard (symbol? s)) s))))
            (define Var
                (lambda (x)
                    (match x
                        (,s (guard (symbol? s)) s)
                        (,(Pycl -> f) f))))
            (define Func
                (lambda (x)
                    (match x
                        (list->py-list list->py-list)
                        (list->py-tuple list->py-tuple)
                        (vector->py-list vector->py-list)
                        (vector->py-tuple vector->py-tuple)
                        (py-list->list py-list->list)
                        (py-tuple->list py-tuple->list))))
            (define Pycl 
                (lambda (x)
                    (match x
                        ((,(Sym -> f) ,(Var -> x) ...) `(py/object-call-object ,f (py-args ,x ...))))))
            (define Expr
                (lambda (x)
                    (match x
                        ((,(Func -> f) ,(Var -> x) ...) `(,f ,x ...)))))
            (match lst
                ((define ,(Sym -> x) ,(Var -> y)) `(define ,x ,y))
                ((import ,(Sym -> lib)) 
                    `(define ,lib (py/import-import-module ,(symbol->string lib))))
                ((import ,(Sym -> lib) as ,(Sym -> l)) 
                    `(define ,l (py/import-import-module ,(symbol->string lib))))
                ((get ,(Sym -> o) ,(Sym -> x))
                    `(define ,x (py/object-get-attr-string ,o ,(symbol->string x))))
                ((get ,(Sym -> o) ,(Sym -> x) as ,(Sym -> k))
                    `(define ,k (py/object-get-attr-string ,o ,(symbol->string x))))
                (,(Expr -> f) f)
                (,(Pycl -> f) f))))


    (define py-args
        (lambda args 
            (define len (length args))
            (define *p (py/tuple-new len))
            (let l ((n 0)(args args))
                (if (< n len)
                    (begin 
                        (py/tuple-set-item! *p n (car args))
                        (l (+ n 1) (cdr args))
                    *p)))))

    
    (define list->py-list
        (lambda (t lst)
            (define len (length lst))
            (define *p (py/list-new len))
            (define f
                (case t
                    ('int py/long-from-long)
                    ('float py/float-from-double)))
            (let l ((n 0)(lst lst))
                (if (< n len)
                    (begin
                        (py/list-set-item! *p n (f (car lst)))
                        (l (+ n 1) (cdr lst)))
                    *p))))

    (define list->py-tuple
        (lambda (t lst)
            (define len (length lst))
            (define *p (py/tuple-new len))
            (define f
                (case t
                    ('int py/long-from-long)
                    ('float py/float-from-double)))
            (let l ((n 0)(lst lst))
                (if (< n len)
                    (begin
                        (py/tuple-set-item! *p n (f (car lst)))
                        (l (+ n 1) (cdr lst)))
                    *p))))
    
    (define py-list->list
        (lambda (t *p)
            (define len (py/list-size *p))
            (define f
                (case t
                    ('int py/long-as-long)
                    ('float py/float-as-double)))
            (let l ((n 0))
                (if (< n len)
                    (cons (f (py/list-get-item *p n)) (l (+ n 1)))
                    '()))))


    (define py-tuple->list
        (lambda (t *p)
            (define len (py/tuple-size *p))
            (define f
                (case t
                    ('int py/long-as-long)
                    ('float py/float-as-double)))
            (let l ((n 0))
                (if (< n len)
                    (cons (f (py/tuple-get-item *p n)) (l (+ n 1)))
                    '()))))



    (define vector->py-list
        (lambda (t vct)
            (define len (vector-length vct))
            (define *p (py/list-new len))
            (define f
                (case t
                    ('int py/long-from-long)
                    ('float py/float-from-double)))
            (let l ((n 0))
                (if (< n len)
                    (begin
                        (py/list-set-item! *p n (f (vector-ref vct n)))
                        (l (+ n 1)))
                    *p))))
    
    (define vector->py-tuple
        (lambda (t vct)
            (define len (vector-length vct))
            (define *p (py/tuple-new len))
            (define f
                (case t
                    ('int py/long-from-long)
                    ('float py/float-from-double)))
            (let l ((n 0))
                (if (< n len)
                    (begin
                        (py/tuple-set-item! *p n (f (vector-ref vct n)))
                        (l (+ n 1)))
                    *p))))
    
    (define alist->py-dict
        (lambda (lst)
            (define *p (py/dict-new))
            (let l ((i (car lst))(r (cdr lst)))
                (py/dict-set-item! *p (py/string-from-string (car i)) (py/long-from-long (cdr i)))
                (if (null? r)
                    *p
                    (l (car r)(cdr r))))))    



)

; ((get ,(Sym -> o) ,(Sym -> x))
; `(define ,x (py/object-get-attr-string ,o ,(symbol->string x))))
; ((get ,(Sym -> o) ,(Sym -> x) as ,(Sym -> k))
; `(define ,k (py/object-get-attr-string ,o ,(symbol->string x))))
; ((,f ,x ...) `(py/object-call-object ,f (py-args ,x ...))))))