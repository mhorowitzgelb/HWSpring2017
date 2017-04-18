import numpy as np
import matplotlib.pyplot as plt

data = np.genfromtxt('ice_parsed.csv',delimiter=',')
print(data)
plt.scatter(data[:,0], data[:,1])
plt.xlabel("Year")
plt.ylabel('Days Lake Frozen')
plt.show()
