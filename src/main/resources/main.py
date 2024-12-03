import sys
import numpy as np
from sklearn.cluster import DBSCAN
import findingCenters

# Read arguments from the command line
args = sys.argv[1:]  # Skip the script name
points = np.array([list(map(float, arg.split(','))) for arg in args])


dbscan = DBSCAN(eps=0.8, min_samples=2)
labels = dbscan.fit_predict(points)

# Print the cluster labels
print("Cluster Labels:", labels)

# Create a list of clusters
clusters = []
centers =[]
outliers = []


for label in set(labels):
    if label != -1:  # Skip noise points
        cluster_points = points[labels == label]
        clusters.append(cluster_points)
    else:
        outliers.append(points[labels == label])


centers.append(points[0])
# Print the separate clusters
for i, cluster in enumerate(clusters):
    center_node = findingCenters.find_central_node(cluster)
    centers.append(center_node)

print("Centers")
for center in centers:
  print(center)

print("Outliers")
for noise in outliers:
    print(noise)