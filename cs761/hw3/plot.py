import numpy as np
import matplotlib.pyplot as plt

data = np.genfromtxt('ice_parsed.csv',delimiter=',')
kde = np.genfromtxt('2_KDE.csv',delimiter=',')

plt.scatter(data[:,0], data[:,1])
plt.xlabel("Year")
plt.ylabel('Days Lake Frozen')
plt.plot(kde[:,0],kde[:,1], 'r')
plt.title('h = 10^2',color='r')
plt.show()
