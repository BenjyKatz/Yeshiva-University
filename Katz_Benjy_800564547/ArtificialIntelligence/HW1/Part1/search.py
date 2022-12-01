#search

import state
import frontier

def search(n):
    s=state.create(n)
    print(s)
    f=frontier.create(s)
    while not frontier.is_empty(f):
        s=frontier.remove(f)
        if state.is_target(s):
            return [s, f[1], f[4], f[5]] #f[1] is the depth
        ns=state.get_next(s)
        #print(ns)
        for i in ns:
            frontier.insert(f,i)
    return 0

totalDepth = 0
totalInsertions = 0
totalRemovals = 0
trials = 3
for i in range(trials):
    answer=search(4)
    totalDepth = totalDepth + answer[1]
    totalInsertions = totalInsertions +answer[2]
    totalRemovals = totalRemovals + answer[3]
print("Average Depth: "+ str(totalDepth/trials))
print("Average Insertions: "+ str(totalInsertions/trials))
print("Average Removals: "+ str(totalRemovals/trials))
# 2x2 with 100 trials
# Average Depth: 1.69
# Average Insertions: 6.58
# Average Removals: 6.1

# 3x3 with 100 trials 
# Average Depth: 6.23
# Average Insertions: 1816.56
# Average Removals: 1047.16

# 4x4 with 3 lucky trials
# Average Depth: 17.33
# Average Insertions: 6954415.67
# Average Removals: 3264276.33