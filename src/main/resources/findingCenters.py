import numpy as np

def euclidean_distance(p1, p2):
    return np.sqrt(np.sum((p2 - p1) ** 2))

# Calculate the total distance for each node
def find_central_node(nodes):
    min_distance = float('inf')
    central_node = None
    
    for i in range(len(nodes)):
        total_distance = 0
        for j in range(len(nodes)):
            if i != j:
                total_distance += euclidean_distance(nodes[i], nodes[j])
        if total_distance < min_distance:
            min_distance = total_distance
            central_node = nodes[i]
    
    return central_node