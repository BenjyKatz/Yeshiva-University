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
            return [s, f[1], f[2], f[3]]
        ns=state.get_next(s)
        for i in ns:
            frontier.insert(f,i)
    return 0
total_pops = 0
total_pushes = 0
total_cost = 0
trials = 100
for i in range(trials):
    answer = search(4)
    pushes = answer[2]
    pops = answer[3]
    path_cost = len(answer[0][1])
    total_pops+=pops
    total_pushes+=pushes
    total_cost+=path_cost
   # print(answer)
   # print("pushes: "+str(pushes)+"pops: "+ str(pops)+ "pathCost: "+str(path_cost))
print("Average Cost: "+ str(total_cost/trials))
print("Average Pushes: "+ str(total_pushes/trials))
print("Average Pops: "+str(total_pops/trials))
# 100 trials 4x4 h1
# Average Cost: 16.01
# Average Pushes: 88043.37
# Average Pops: 42365.57

# 100 trials 4x4 h2
# Average Cost: 15.6
# Average Pushes: 4132.7
# Average Pops: 2066.06

# 100 trials 4x4 weighted h1
# Average Cost: 16.28
# Average Pushes: 4432.68
# Average Pops: 2071.4

# 100 trials 4x4 weighted h2
# Average Cost: 17.79
# Average Pushes: 2080.72
# Average Pops: 1037.13

"""
Using a weighted heuristic does not improve optimality, but it does find a solution quicker.
The algorithm finds a solution that is likely not optimal. Because it weighs the heuristic more,
the algorithm is more likely to find a correct solution faster even though it is not the best solution becasue it over estimates the solution
"""