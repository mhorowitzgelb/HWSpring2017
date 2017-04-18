import numpy as np
import matplotlib.pyplot as plt

data = np.genfromtxt('ice_parsed.csv',delimiter=',')
kde = np.genfromtxt('1_2_KDE.csv',delimiter=',')

plt.scatter(data[:,0], data[:,1])
plt.xlabel("Year")
plt.ylabel('Days Lake Frozen')
plt.plot(kde[:,0],kde[:,1], 'r')

plt.show()
