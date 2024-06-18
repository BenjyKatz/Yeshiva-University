import numpy as np

def fib_rec(x):
    if(x == 0): return 0
    if(x ==1): return 1
    result = fib_rec(x-1)+fib_rec(x-2)
    return result

def fib_seq(x):
    if(x==0): return 0
    seq = [0, 1]
    index = 2
    while(index<=x):
        seq.append(seq[(index-1)]+seq[(index-2)])
        index+=1
    return seq[-1]
def fib_mat(x):
    base_mat = np.array([[1,1],[1,0]], dtype='object') 
    result_mat = np.linalg.matrix_power(base_mat, x)
    return result_mat[0][1]


print("seq")
print(fib_seq(15))
print("rec")
print(fib_rec(15))
print("mat")
print(fib_mat(15))