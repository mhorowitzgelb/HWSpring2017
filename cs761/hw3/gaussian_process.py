import numpy as np
from math import exp
import sys
import matplotlib.pyplot as plt

sigma = 1

x = [40,120]
y= np.array([0,1]).reshape((2,1))

x_s = np.arange(1,101)
x_s = x_s[x_s!=40]


K_n_s = np.zeros((2,99))
K_n_n = np.zeros((2,2))
K_s_s = np.zeros((99,99))


for i in range(99):
    for j in range(99):
        K_s_s[i,j] = (1/16.)*exp(-
                          (x_s[i]-x_s[j])**2
                          /(2*sigma**2))
for i in range(2):
    for j in range(2):
        K_n_n[i,j] = (1/16.)*exp(-
                          (x[i]-x[j])**2
                          /(2*sigma**2))

for i in range(2):
    for j in range(99):
        K_n_s[i, j] = (1 / 16.) * exp(-
                                      (x[i] - x_s[j]) ** 2
                                      / (2 * sigma ** 2))

K_s_n = np.transpose(K_n_s)



m_x = np.dot(K_s_n, np.dot(np.linalg.inv(K_n_n),y)).reshape((99,))

cov = K_s_s - np.dot(K_s_n,np.dot(np.linalg.inv(K_n_n),K_n_s))


plt.plot(x_s,m_x,linewidth=5, label='mean')

for i in range(6):
    plt.plot(x_s, np.random.multivariate_normal(mean=m_x,cov=cov), '--',linewidth =1, label='sample {0}'.format(i+1))

plt.legend()
plt.title("sigma = {0}".format(sigma), color='r')
plt.show()